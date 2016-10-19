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
            carmageddonSettings.setShowVideo(configuration.getBoolean("common.showVideo"));

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
            roadSettings.setViewType(ViewType.valueOf(configuration.getString("road.viewtype")));
            roadSettings.setAddFound(configuration.getBoolean("road.addFound"));

            roadSettings.setRoiHeight(configuration.getInt("road.roi.height"));
            roadSettings.setCannyThreshold1(configuration.getInt("road.canny.threshold1"));
            roadSettings.setCannyThreshold2(configuration.getInt("road.canny.threshold2"));
            roadSettings.setCannyApertureSize(configuration.getInt("road.canny.apertureSize"));

            roadSettings.setLinesThreshold(configuration.getInt("road.lines.threshold"));
            roadSettings.setLinesMinLineSize(configuration.getInt("road.lines.minLineSize"));
            roadSettings.setLinesMaxLineGap(configuration.getInt("road.lines.maxLineGap"));

            carmageddonSettings.setRoadSettings(roadSettings);

            bind(CarmageddonSettings.class).toInstance(carmageddonSettings);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("lasforce.properties could not be found on the classpath.");
        }
    }
}