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
            result = new LookoutResult(AutonomousStatus.RACE_FINISHED, this.car.getCamera().makeSnapshotInByteArray());
            setChanged();
            notifyObservers(result);
            run = false;
        }
        return result;
    }

    @Override
    public void stop() {
        run = false;
    }

    @Override
    public LookoutResult getStatus() {
        return result;
    }

}
