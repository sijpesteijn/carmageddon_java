package nl.carmageddon.service;

import nl.carmageddon.domain.AutonomousStatus;
import nl.carmageddon.domain.Car;
import nl.carmageddon.domain.LookoutResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class StraightTrackLookout extends Observable implements Lookout {
    private Car car;
    private boolean run;
    private LookoutResult result;

    @Inject
    public StraightTrackLookout(Car car) {
        this.car = car;
    }

    @Override
    public LookoutResult start() {
        run = true;
        while(run) {
            car.getEngine().setThrottle(20);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            car.getEngine().setThrottle(0);
            result = new LookoutResult(AutonomousStatus.RACE_FINISHED, this.car.getCamera().makeSnapshotInByteArray());
            notifyClients(result);
            run = false;
        }
        return result;
    }

    @Override
    public void stop() {
        run = false;
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
//        logger.debug(event.getStatus() + " send to clients");
    }


}
