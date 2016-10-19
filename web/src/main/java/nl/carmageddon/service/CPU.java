package nl.carmageddon.service;

import nl.carmageddon.domain.*;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Car Processing Unit :)
 *
 * Regelt de lookouts en controleert de auto
 *
 */
@Singleton
public class CPU extends Observable implements Observer {
    private static Logger logger = LoggerFactory.getLogger(CPU.class);
    private CarmageddonSettings settings;
    private boolean racing;
    private Lookout currentLookout;
    private List<Lookout> lookouts = new ArrayList<>();
    private TrafficLightLookout2 trafficLightLookout;
    private Camera camera;
    private Car car;
    private CarInstructionSender carInstructionSender;
    private RoadLookout roadLookout;
    private long delay;
    private ScheduledExecutorService statusTimer;

    private Runnable statusRunner = () -> {
        if (this.camera.getCamera() != null && this.camera.getCamera().isOpened()) {
            sendReadyToRace();
        } else {
            notifyClients(new LookoutResult(AutonomousStatus.NO_CAMERA, null));
        }
    };

    @Inject
    public CPU(CarmageddonSettings settings, TrafficLightLookout2 trafficLightLookout, RoadLookout
            roadLookout, Camera camera, Car car, CarInstructionSender carInstructionSender) {
        this.settings = settings;
        this.camera = camera;
        this.car = car;
        this.carInstructionSender = carInstructionSender;

        this.trafficLightLookout = trafficLightLookout;
        this.trafficLightLookout.addObserver(this);
        this.lookouts.add(this.trafficLightLookout);

        this.roadLookout = roadLookout;
        this.roadLookout.addObserver(this);
        this.lookouts.add(this.roadLookout);

        useSettings(this.settings);
    }

    public void race() {
        racing = true;
        shutdownWebcamPushTimer();
        for(Lookout lookout : lookouts) {
            if (racing) {
                this.currentLookout = lookout;
                LookoutResult result = this.currentLookout.start();
                if (result.getStatus() == AutonomousStatus.RACE_FINISHED) {
                    racing = false;
                    this.currentLookout = null;
                    sendReadyToRace();
                }
            }
        }
        racing = false;
        startWebcamPushTimer();
    }

    private void sendReadyToRace() {
        Mat snapshot = null;
        if (settings.isShowVideo()) {
            snapshot = this.camera.makeSnapshot();
            if (settings.getTrafficLightSettings().isAddFound()) {
                TrafficLightView trafficLightViewview = this.trafficLightLookout.getTrafficLightView(snapshot);
                this.trafficLightLookout.addTrafficLightHighlight(trafficLightViewview, snapshot);
            }
            if (settings.getRoadSettings().isAddFound()) {
                LinesView linesView = this.roadLookout.detectLines(snapshot);
//                logger.debug("Possible roads: " + linesView.getRoadLines().size());
//                logger.debug("Possible finish lines: " + linesView.getFinishLines().size());
                this.roadLookout.addRoadHighlights(linesView, snapshot);
            }
        }
        notifyClients(new LookoutResult(AutonomousStatus.READY_TO_RACE, snapshot));
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
    }

    public boolean isRacing() {
        return racing;
    }

    public void stopRacing() throws IOException {
        if (this.currentLookout != null) {
            this.currentLookout.stop();
            this.currentLookout = null;
            startWebcamPushTimer();
            notifyClients(new LookoutResult(AutonomousStatus.RACE_STOPPED, this.camera.makeSnapshot()));
        }
        this.carInstructionSender.sendMessage("angle", 0);
        this.carInstructionSender.sendMessage("throttle", 0);
        this.racing = false;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof LookoutResult) {
            LookoutResult event = (LookoutResult) arg;
            notifyClients(event);
        }
//        // TODO misschien ook een event
        if (arg == null) {
            if (car.getMode() == Mode.autonomous && this.racing == false) {
                startWebcamPushTimer();
            } else if(this.statusTimer != null) {
                shutdownWebcamPushTimer();
            }
        }
    }

    private void startWebcamPushTimer() {
        if(this.statusTimer == null && this.car.getMode() == Mode.autonomous && !this.racing) {
            this.statusTimer = Executors.newSingleThreadScheduledExecutor();
            this.statusTimer.scheduleAtFixedRate(statusRunner, 0, this.delay, TimeUnit.MILLISECONDS);
        }
    }

    private void shutdownWebcamPushTimer() {
        if (this.statusTimer != null) {
            this.statusTimer.shutdown();
            this.statusTimer = null;
        }
    }

    public void useSettings(CarmageddonSettings settings) {
        this.trafficLightLookout.setTrafficLightSettings(settings.getTrafficLightSettings());
        this.roadLookout.setRoadSettings(settings.getRoadSettings());
        this.roadLookout.setDelay(settings.getDelay());
        this.settings = settings;

        this.delay = settings.getDelay();
        if (car.getMode() == Mode.autonomous && this.racing == false) {
            shutdownWebcamPushTimer();
            startWebcamPushTimer();
        }

    }

    public CarmageddonSettings getSettings() {
        return settings;
    }

    public void destroy() {
        shutdownWebcamPushTimer();
        if (this.currentLookout != null) {
            this.currentLookout.stop();
            this.currentLookout = null;
        }
        this.racing = false;
    }
}