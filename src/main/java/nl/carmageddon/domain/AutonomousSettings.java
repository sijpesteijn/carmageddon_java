package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class AutonomousSettings {
    private TrafficLightSettings trafficLight;
    private ViewType viewType;

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
}
