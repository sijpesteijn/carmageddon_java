package nl.carmageddon.service;

import nl.carmageddon.domain.*;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    private AutonomousSettings settings;
    private boolean racing;
    private Car car;
    private Lookout currentLookout;
    private List<Lookout> lookouts = new ArrayList<>();
    private TrafficLightLookout trafficLightLookout;
    private StraightTrackLookout straightTrackLookout;

    private ScheduledExecutorService statusTimer;
    private Runnable statusRunner = () -> {
        if (this.car.getCamera().getCamera().isOpened()) {
            notifyClients(
                    new LookoutResult(AutonomousStatus.READY_TO_RACE, this.car.getCamera().makeSnapshotInByteArray()));
        } else {
            notifyClients(new LookoutResult(AutonomousStatus.NO_CAMERA, null));
        }
    };
    private long delay;

    @Inject
    public CPU(Configuration configuration, Car car, TrafficLightLookout trafficLightLookout, StraightTrackLookout
            straightTrackLookout) {
        this.settings = loadSettings(configuration);
        this.car = car;
        this.car.addObserver(this);
        this.trafficLightLookout = trafficLightLookout;
        this.trafficLightLookout.addObserver(this);
        this.lookouts.add(this.trafficLightLookout);
        this.straightTrackLookout = straightTrackLookout;
        this.straightTrackLookout.addObserver(this);
        this.lookouts.add(this.straightTrackLookout);
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
                    notifyClients(new LookoutResult(AutonomousStatus.READY_TO_RACE, this.car.getCamera().makeSnapshotInByteArray()));
                }
            }
        }
        racing = false;
        startWebcamPushTimer();
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
        logger.debug(event.getStatus() + " send to clients");
    }

    public boolean isRacing() {
        return racing;
    }

    public void stopRacing() {
        if (this.currentLookout != null) {
            this.currentLookout.stop();
            this.currentLookout = null;
            startWebcamPushTimer();
            notifyClients(new LookoutResult(AutonomousStatus.RACE_STOPPED, this.car.getCamera()
                                                                                    .makeSnapshotInByteArray()));
        }
        this.racing = false;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof LookoutResult) {
            LookoutResult event = (LookoutResult) arg;
            notifyClients(event);
        }
        // TODO misschien ook een event
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

    public void useSettings(AutonomousSettings settings) {
        this.trafficLightLookout.setLowerHSVMin(settings.getTrafficLight().getLowerHSVMin());
        this.trafficLightLookout.setLowerHSVMax(settings.getTrafficLight().getLowerHSVMax());
        this.trafficLightLookout.setUpperHSVMin(settings.getTrafficLight().getUpperHSVMin());
        this.trafficLightLookout.setUpperHSVMax(settings.getTrafficLight().getUpperHSVMax());
        this.trafficLightLookout.setViewType(settings.getViewType());
        this.trafficLightLookout.setDelay(settings.getDelay());
        this.delay = settings.getDelay();
        if (car.getMode() == Mode.autonomous && this.racing == false) {
            shutdownWebcamPushTimer();
            startWebcamPushTimer();
        }
        this.trafficLightLookout.setMinBoxBox(settings.getTrafficLight().getMinBox());
        this.trafficLightLookout.setMaxBoxBox(settings.getTrafficLight().getMaxBox());
    }

    public AutonomousSettings getSettings() {
        return settings;
    }

    // TODO dit moet ergens anders en makkelijker kunnen
    private AutonomousSettings loadSettings(Configuration configuration) {
        AutonomousSettings autonomousSettings = new AutonomousSettings();
        autonomousSettings.setViewType(ViewType.valueOf(configuration.getString("common.viewtype")));
        autonomousSettings.setDelay(configuration.getLong("common.delay"));

        TrafficLightSettings trafficLightSettings = new TrafficLightSettings();
        autonomousSettings.setTrafficLight(trafficLightSettings);
        HSV lowerHSVMin = new HSV();
        lowerHSVMin.setHue(configuration.getInt("trafficlight.lowerbound.min_hsv.h"));
        lowerHSVMin.setSaturation(configuration.getInt("trafficlight.lowerbound.min_hsv.s"));
        lowerHSVMin.setBrightness(configuration.getInt("trafficlight.lowerbound.min_hsv.v"));
        trafficLightSettings.setLowerHSVMin(lowerHSVMin);

        HSV lowerHSVMax = new HSV();
        lowerHSVMax.setHue(configuration.getInt("trafficlight.lowerbound.max_hsv.h"));
        lowerHSVMax.setSaturation(configuration.getInt("trafficlight.lowerbound.max_hsv.s"));
        lowerHSVMax.setBrightness(configuration.getInt("trafficlight.lowerbound.max_hsv.v"));
        trafficLightSettings.setLowerHSVMax(lowerHSVMax);

        HSV upperHSVMin = new HSV();
        upperHSVMin.setHue(configuration.getInt("trafficlight.upperbound.min_hsv.h"));
        upperHSVMin.setSaturation(configuration.getInt("trafficlight.upperbound.min_hsv.s"));
        upperHSVMin.setBrightness(configuration.getInt("trafficlight.upperbound.min_hsv.v"));
        trafficLightSettings.setUpperHSVMin(upperHSVMin);

        HSV upperHSVMax = new HSV();
        upperHSVMax.setHue(configuration.getInt("trafficlight.upperbound.max_hsv.h"));
        upperHSVMax.setSaturation(configuration.getInt("trafficlight.upperbound.max_hsv.s"));
        upperHSVMax.setBrightness(configuration.getInt("trafficlight.upperbound.max_hsv.v"));
        trafficLightSettings.setUpperHSVMax(upperHSVMax);

        Box minBox = new Box();
        minBox.setWidth(configuration.getInt("trafficlight.minBox.width"));
        minBox.setHeight(configuration.getInt("trafficlight.minBox.width"));
        trafficLightSettings.setMinBox(minBox);
        Box maxBox = new Box();
        maxBox.setWidth(configuration.getInt("trafficlight.maxBox.width"));
        maxBox.setHeight(configuration.getInt("trafficlight.maxBox.width"));
        trafficLightSettings.setMaxBox(maxBox);
        return autonomousSettings;
    }

    public void destroy() {
        if (car.getCamera().isOpened()) {
            System.out.println("** RELEASING CAMERA **");
            car.getCamera().getCamera().release();
        }
        shutdownWebcamPushTimer();
        if (this.currentLookout != null) {
            this.currentLookout.stop();
            this.currentLookout = null;
        }
        this.racing = false;
    }
}