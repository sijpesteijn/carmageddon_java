package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class AutonomousSettings {
    private TrafficLightSettings trafficLight;
    private ViewType viewType;
    private long delay;

    public TrafficLightSettings getTrafficLight() {
        return trafficLight;
    }

    public void setTrafficLight(TrafficLightSettings trafficLight) {
        this.trafficLight = trafficLight;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }
}
