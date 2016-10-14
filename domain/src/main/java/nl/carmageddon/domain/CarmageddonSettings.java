package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class CarmageddonSettings {

    private TrafficLightSettings trafficLightSettings;
    private RoadSettings roadSettings;
    private long delay;
    private int maxThrottle;
    private Dimension cameraDimension;
    private boolean showVideo;
    private BeagleBoneSettings beagleBoneSettings;

    public TrafficLightSettings getTrafficLightSettings() {
        return trafficLightSettings;
    }

    public void setTrafficLightSettings(TrafficLightSettings trafficLightSettings) {
        this.trafficLightSettings = trafficLightSettings;
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

    public int getMaxThrottle() {
        return maxThrottle;
    }

    public void setMaxThrottle(int maxThrottle) {
        this.maxThrottle = maxThrottle;
    }

    public void setShowVideo(boolean showVideo) {
        this.showVideo = showVideo;
    }

    public boolean isShowVideo() {
        return showVideo;
    }

    public void setBeagleBoneSettings(BeagleBoneSettings beagleBoneSettings) {
        this.beagleBoneSettings = beagleBoneSettings;
    }

    public BeagleBoneSettings getBeagleBoneSettings() {
        return beagleBoneSettings;
    }
}
