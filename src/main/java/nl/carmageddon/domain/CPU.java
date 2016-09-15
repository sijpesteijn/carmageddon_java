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
    private Configuration configuration;
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
        this.trafficLightLookout.addObserver(this);
        this.lookouts.add(this.trafficLightLookout);
        this.straightTrackLookout = straightTrackLookout;
        this.straightTrackLookout.addObserver(this);
        this.lookouts.add(this.straightTrackLookout);
    }

    // TODO dit moet makkelijker kunnen
    private AutonomousSettings loadSettings(Configuration configuration) {
        AutonomousSettings settings = new AutonomousSettings();
        RGB lowerRGBMin = new RGB();
        lowerRGBMin.setRed(configuration.getInt("trafficlight.lowerbound.min_rgb.red"));
        lowerRGBMin.setGreen(configuration.getInt("trafficlight.lowerbound.min_rgb.green"));
        lowerRGBMin.setBlue(configuration.getInt("trafficlight.lowerbound.min_rgb.blue"));
        settings.setLowerRGBMin(lowerRGBMin);

        RGB lowerRGBMax = new RGB();
        lowerRGBMax.setRed(configuration.getInt("trafficlight.lowerbound.max_rgb.red"));
        lowerRGBMax.setGreen(configuration.getInt("trafficlight.lowerbound.max_rgb.green"));
        lowerRGBMax.setBlue(configuration.getInt("trafficlight.lowerbound.max_rgb.blue"));
        settings.setLowerRGBMax(lowerRGBMax);

        RGB upperRGBMin = new RGB();
        upperRGBMin.setRed(configuration.getInt("trafficlight.upperbound.min_rgb.red"));
        upperRGBMin.setGreen(configuration.getInt("trafficlight.upperbound.min_rgb.green"));
        upperRGBMin.setBlue(configuration.getInt("trafficlight.upperbound.min_rgb.blue"));
        settings.setUpperRGBMin(upperRGBMin);

        RGB upperRGBMax = new RGB();
        upperRGBMax.setRed(configuration.getInt("trafficlight.upperbound.max_rgb.red"));
        upperRGBMax.setGreen(configuration.getInt("trafficlight.upperbound.max_rgb.green"));
        upperRGBMax.setBlue(configuration.getInt("trafficlight.upperbound.max_rgb.blue"));
        settings.setUpperRGBMax(upperRGBMax);

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
                notifyClients(new LookoutResult(AutonomousStatus.READY_TO_RACE, null));
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
        return new LookoutResult(AutonomousStatus.READY_TO_RACE, this.car.getCamera().makeSnapshot());
    }

    public void useSettings(AutonomousSettings settings) {
        this.trafficLightLookout.setLowerRGBMin(settings.getLowerRGBMin());
        this.trafficLightLookout.setLowerRGBMax(settings.getLowerRGBMax());
        this.trafficLightLookout.setUpperRGBMin(settings.getUpperRGBMin());
        this.trafficLightLookout.setUpperRGBMax(settings.getUpperRGBMax());
    }

    public AutonomousSettings getSettings() {
        return settings;
    }
}