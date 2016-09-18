package nl.carmageddon.domain;

import nl.carmageddon.service.Lookout;
import nl.carmageddon.service.StraightTrackLookout;
import nl.carmageddon.service.TrafficLightLookout;
import org.apache.commons.configuration.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Car Processing Unit :)
 *
 * Regelt de lookouts en controleert de auto
 *
 */
@Singleton
public class CPU extends Observable implements Observer {

    private AutonomousSettings settings;
    private boolean racing;
    private Car car;
    private Lookout currentLookout;
    private List<Lookout> lookouts = new ArrayList<>();
    private TrafficLightLookout trafficLightLookout;
    private StraightTrackLookout straightTrackLookout;

    @Inject
    public CPU(Configuration configuration, Car car, TrafficLightLookout trafficLightLookout, StraightTrackLookout
            straightTrackLookout) {
        this.settings = loadSettings(configuration);
        this.car = car;
        this.trafficLightLookout = trafficLightLookout;
        this.trafficLightLookout.setLowerHSVMin(settings.getLowerHSVMin());
        this.trafficLightLookout.setLowerHSVMax(settings.getLowerHSVMax());
        this.trafficLightLookout.setUpperHSVMin(settings.getUpperHSVMin());
        this.trafficLightLookout.setUpperHSVMax(settings.getUpperHSVMax());
        this.trafficLightLookout.addObserver(this);
        this.lookouts.add(this.trafficLightLookout);
        this.straightTrackLookout = straightTrackLookout;
        this.straightTrackLookout.addObserver(this);
        this.lookouts.add(this.straightTrackLookout);
    }

    // TODO dit moet makkelijker kunnen
    private AutonomousSettings loadSettings(Configuration configuration) {
        AutonomousSettings settings = new AutonomousSettings();
        HSV lowerHSVMin = new HSV();
        lowerHSVMin.setHue(configuration.getInt("trafficlight.lowerbound.min_hsv.h"));
        lowerHSVMin.setSaturation(configuration.getInt("trafficlight.lowerbound.min_hsv.h"));
        lowerHSVMin.setValue(configuration.getInt("trafficlight.lowerbound.min_hsv.v"));
        settings.setLowerHSVMin(lowerHSVMin);

        HSV lowerHSVMax = new HSV();
        lowerHSVMax.setHue(configuration.getInt("trafficlight.lowerbound.max_hsv.h"));
        lowerHSVMax.setSaturation(configuration.getInt("trafficlight.lowerbound.max_hsv.s"));
        lowerHSVMax.setValue(configuration.getInt("trafficlight.lowerbound.max_hsv.v"));
        settings.setLowerHSVMax(lowerHSVMax);

        HSV upperHSVMin = new HSV();
        upperHSVMin.setHue(configuration.getInt("trafficlight.upperbound.min_hsv.h"));
        upperHSVMin.setSaturation(configuration.getInt("trafficlight.upperbound.min_hsv.s"));
        upperHSVMin.setValue(configuration.getInt("trafficlight.upperbound.min_hsv.v"));
        settings.setUpperHSVMin(upperHSVMin);

        HSV upperHSVMax = new HSV();
        upperHSVMax.setHue(configuration.getInt("trafficlight.upperbound.max_hsv.h"));
        upperHSVMax.setSaturation(configuration.getInt("trafficlight.upperbound.max_hsv.s"));
        upperHSVMax.setValue(configuration.getInt("trafficlight.upperbound.max_hsv.v"));
        settings.setUpperHSVMax(upperHSVMax);

        return settings;
    }

    public void race() {
        racing = true;
        LookoutResult result;
        for(Lookout lookout : lookouts) {
            this.currentLookout = lookout;
            result = this.currentLookout.start();
            if (result.getStatus() == AutonomousStatus.RACE_FINISHED) {
                racing = false;
                this.currentLookout = null;
                notifyClients(new LookoutResult(AutonomousStatus.READY_TO_RACE, null));
            }
        }
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
    }

    public boolean isRacing() {
        return racing;
    }

    public void stopRacing() {
        if (this.currentLookout != null)
            this.currentLookout.stop();
        this.racing = false;
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
        if (this.currentLookout == null)
            return new LookoutResult(AutonomousStatus.READY_TO_RACE, this.car.getCamera().makeSnapshotInByteArray());
        return this.currentLookout.getStatus();
    }

    public void useSettings(AutonomousSettings settings) {
        this.trafficLightLookout.setLowerHSVMin(settings.getLowerHSVMin());
        this.trafficLightLookout.setLowerHSVMax(settings.getLowerHSVMax());
        this.trafficLightLookout.setUpperHSVMin(settings.getUpperHSVMin());
        this.trafficLightLookout.setUpperHSVMax(settings.getUpperHSVMax());
    }

    public AutonomousSettings getSettings() {
        return settings;
    }
}