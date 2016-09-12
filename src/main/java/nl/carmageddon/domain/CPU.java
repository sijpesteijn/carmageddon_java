package nl.carmageddon.domain;

import nl.carmageddon.service.TrafficLightLookout;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class CPU extends Observable implements Observer {
    private Car car;
    private TrafficLightLookout trafficLightLookout;
    private boolean racing;
    private boolean finished = false;

    @Inject
    public CPU(Car car, TrafficLightLookout trafficLightLookout) {
        this.car = car;
        this.trafficLightLookout = trafficLightLookout;
        this.trafficLightLookout.addObserver(this);
    }

    public void race() {
        racing = true;
        notifyClients(new AutonoumousEvent(AutonoumousEventType.CAR_READY_TO_RACE, null, car.isConnected()));
//        while(racing && !finished) {
            this.trafficLightLookout.lookForTrafficLight();
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//        }
        if (!racing && !finished) {
            this.trafficLightLookout.cancel();
            notifyClients(new AutonoumousEvent(AutonoumousEventType.CAR_STOPPED, null, false));
        }
    }

    private void notifyClients(AutonoumousEvent event) {
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
        notifyClients((AutonoumousEvent) arg);
    }
}