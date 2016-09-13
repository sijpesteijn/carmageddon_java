package nl.carmageddon.domain;

import nl.carmageddon.service.Lookout;
import nl.carmageddon.service.StraightTrackLookout;
import nl.carmageddon.service.TrafficLightLookout;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class CPU extends Observable implements Observer {
    private Car car;
    private Lookout currentLookout;
    private List<Lookout> lookouts = new ArrayList<Lookout>();
    private TrafficLightLookout trafficLightLookout;
    private StraightTrackLookout straightTrackLookout;

    private boolean racing;
    private boolean finished = false;

    @Inject
    public CPU(Car car, TrafficLightLookout trafficLightLookout, StraightTrackLookout straightTrackLookout) {
        this.car = car;
        this.trafficLightLookout = trafficLightLookout;
        this.trafficLightLookout.addObserver(this);
        this.lookouts.add(this.trafficLightLookout);
        this.straightTrackLookout = straightTrackLookout;
        this.straightTrackLookout.addObserver(this);
        this.lookouts.add(this.straightTrackLookout);
    }

    public void race() {
        racing = true;
        LookoutResult result;
        for(Lookout lookout : lookouts) {
            this.currentLookout = lookout;
            result = this.currentLookout.start();
            if (result.getStatus() == AutonomousStatus.RACE_FINISHED) {
                racing = false;
            }
        }
//        if (!racing && !finished) {
//            this.currentLookout.stop();
//            notifyClients(new AutonomousEvent(AutonomousStatus.CAR_STOPPED, null, racing, false));
//        } else {
//            notifyClients(new AutonomousEvent(AutonomousStatus.RACE_FINISHED, null, racing, false));
//        }
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
    }

    public boolean isRacing() {
        return racing;
    }

    public void setRacing(boolean racing) {
        this.racing = racing;
    }

    @Override
    public void update(Observable o, Object arg) {
        LookoutResult event = (LookoutResult) arg;
        if (!event.sucess()) {
            this.racing = false;
        }
        notifyClients(event);
    }

    public LookoutResult getStatus() {
        return new LookoutResult(AutonomousStatus.CAR_READY_TO_RACE, this.car.getCamera().makeSnapshot());
    }
}