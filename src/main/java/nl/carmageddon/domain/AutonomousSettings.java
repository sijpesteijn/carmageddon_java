package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class AutonomousSettings {
    private TrafficLightSettings trafficLightSettings;
    private RoadSettings roadSettings;
    private ViewType viewType;
    private long delay;
    private int maxThrottle;
    private Dimension cameraDimension;

    public TrafficLightSettings getTrafficLightSettings() {
        return trafficLightSettings;
    }

    public void setTrafficLightSettings(TrafficLightSettings trafficLightSettings) {
        this.trafficLightSettings = trafficLightSettings;
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

    public Dimension getCameraDimension() {
        return cameraDimension;
    }

    public void setCameraDimension(Dimension cameraDimension) {
        this.cameraDimension = cameraDimension;
    }

    public RoadSettings getRoadSettings() {
        return roadSettings;
    }

    public void setRoadSettings(RoadSettings roadSettings) {
        this.roadSettings = roadSettings;
    }
}
