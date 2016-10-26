package nl.carmageddon.guice;

import com.google.inject.AbstractModule;
import nl.carmageddon.domain.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Gijs Sijpesteijn
 */
public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        try {

            PropertiesConfiguration configuration = new PropertiesConfiguration("application.properties");
            CarmageddonSettings carmageddonSettings = new CarmageddonSettings();
            carmageddonSettings.setDelay(configuration.getLong("common.delay"));
            carmageddonSettings.setPreview(configuration.getBoolean("common.preview"));

            BeagleBoneSettings beagleBoneSettings = new BeagleBoneSettings();
            beagleBoneSettings.setBeagleBoneIp(configuration.getString("bb.ip"));
            beagleBoneSettings.setStreamPort(configuration.getString("bb.stream.port"));
            beagleBoneSettings.setLifeLinePort(configuration.getInt("bb.lifeline.port"));
            beagleBoneSettings.setCarStatusPort(configuration.getInt("bb.carstatus.port"));
            beagleBoneSettings.setCarControlPort(configuration.getInt("bb.carcontrol.port"));
            beagleBoneSettings.setThrottleLimit(configuration.getInt("bb.throttle.limit"));
            carmageddonSettings.setBeagleBoneSettings(beagleBoneSettings);

            Dimension cameraDimension = new Dimension();
            cameraDimension.setWidth(configuration.getInt("common.camera.cameraDimension.width"));
            cameraDimension.setHeight(configuration.getInt("common.camera.cameraDimension.height"));
            carmageddonSettings.setCameraDimension(cameraDimension);

            TrafficLightSettings trafficLightSettings = new TrafficLightSettings();
            carmageddonSettings.setTrafficLightSettings(trafficLightSettings);

            ROI roi = new ROI();
            roi.setX(configuration.getInt("trafficlight.roi.x"));
            roi.setY(configuration.getInt("trafficlight.roi.y"));
            roi.setWidth(configuration.getInt("trafficlight.roi.width"));
            roi.setHeight(configuration.getInt("trafficlight.roi.height"));
            trafficLightSettings.setRoi(roi);

            RoadSettings roadSettings = new RoadSettings();
            roadSettings.setShowFinishLines(configuration.getBoolean("road.show.finishLines"));
            roadSettings.setShowRoadLines(configuration.getBoolean("road.show.roadlines"));
            roadSettings.setRoiHeight(configuration.getInt("road.roi.height"));

            LineSettings laneLineSettings = new LineSettings();
            laneLineSettings.setCannyThreshold1(configuration.getInt("road.lane.canny.threshold1"));
            laneLineSettings.setCannyThreshold2(configuration.getInt("road.lane.canny.threshold2"));
            laneLineSettings.setCannyApertureSize(configuration.getInt("road.lane.canny.apertureSize"));

            laneLineSettings.setLinesThreshold(configuration.getInt("road.lane.lines.threshold"));
            laneLineSettings.setLinesMinLineSize(configuration.getInt("road.lane.lines.minLineSize"));
            laneLineSettings.setLinesMaxLineGap(configuration.getInt("road.lane.lines.maxLineGap"));

            roadSettings.setLaneLineSettings(laneLineSettings);

            LineSettings finishLineSettings = new LineSettings();
            finishLineSettings.setCannyThreshold1(configuration.getInt("road.finish.canny.threshold1"));
            finishLineSettings.setCannyThreshold2(configuration.getInt("road.finish.canny.threshold2"));
            finishLineSettings.setCannyApertureSize(configuration.getInt("road.finish.canny.apertureSize"));

            finishLineSettings.setLinesThreshold(configuration.getInt("road.finish.lines.threshold"));
            finishLineSettings.setLinesMinLineSize(configuration.getInt("road.finish.lines.minLineSize"));
            finishLineSettings.setLinesMaxLineGap(configuration.getInt("road.finish.lines.maxLineGap"));

            roadSettings.setFinishLineSettings(finishLineSettings);
            carmageddonSettings.setRoadSettings(roadSettings);

            bind(CarmageddonSettings.class).toInstance(carmageddonSettings);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("lasforce.properties could not be found on the classpath.");
        }
    }
}