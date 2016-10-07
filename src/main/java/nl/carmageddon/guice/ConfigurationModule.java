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

            AutonomousSettings autonomousSettings = new AutonomousSettings();
            autonomousSettings.setViewType(ViewType.valueOf(configuration.getString("common.viewtype")));
            autonomousSettings.setDelay(configuration.getLong("common.delay"));
            Dimension cameraDimension = new Dimension();
            cameraDimension.setWidth(configuration.getInt("common.camera.cameraDimension.width"));
            cameraDimension.setHeight(configuration.getInt("common.camera.cameraDimension.height"));
            autonomousSettings.setCameraDimension(cameraDimension);

            TrafficLightSettings trafficLightSettings = new TrafficLightSettings();
            autonomousSettings.setTrafficLightSettings(trafficLightSettings);
            HSV lowerHSVMin = new HSV();
            lowerHSVMin.setHue(configuration.getInt("trafficlight.lowerbound.min_hsv.h"));
            lowerHSVMin.setSaturation(configuration.getInt("trafficlight.lowerbound.min_hsv.s"));
            lowerHSVMin.setBrightness(configuration.getInt("trafficlight.lowerbound.min_hsv.v"));
            trafficLightSettings.setLowerHSVMin(lowerHSVMin);

            HSV lowerHSVMax = new HSV();
            lowerHSVMax.setHue(configuration.getInt("trafficlight.lowerbound.max_hsv.h"));
            lowerHSVMax.setSaturation(configuration.getInt("trafficlight.lowerbound.max_hsv.s"));
            lowerHSVMax.setBrightness(configuration.getInt("trafficlight.lowerbound.max_hsv.v"));
            trafficLightSettings.setLowerHSVMax(lowerHSVMax);

            HSV upperHSVMin = new HSV();
            upperHSVMin.setHue(configuration.getInt("trafficlight.upperbound.min_hsv.h"));
            upperHSVMin.setSaturation(configuration.getInt("trafficlight.upperbound.min_hsv.s"));
            upperHSVMin.setBrightness(configuration.getInt("trafficlight.upperbound.min_hsv.v"));
            trafficLightSettings.setUpperHSVMin(upperHSVMin);

            HSV upperHSVMax = new HSV();
            upperHSVMax.setHue(configuration.getInt("trafficlight.upperbound.max_hsv.h"));
            upperHSVMax.setSaturation(configuration.getInt("trafficlight.upperbound.max_hsv.s"));
            upperHSVMax.setBrightness(configuration.getInt("trafficlight.upperbound.max_hsv.v"));
            trafficLightSettings.setUpperHSVMax(upperHSVMax);

            Dimension minDimension = new Dimension();
            minDimension.setWidth(configuration.getInt("trafficlight.minBox.width"));
            minDimension.setHeight(configuration.getInt("trafficlight.minBox.width"));
            trafficLightSettings.setMinDimension(minDimension);
            Dimension maxDimension = new Dimension();
            maxDimension.setWidth(configuration.getInt("trafficlight.maxBox.width"));
            maxDimension.setHeight(configuration.getInt("trafficlight.maxBox.width"));
            trafficLightSettings.setMaxDimension(maxDimension);

            ROI roi = new ROI();
            roi.setX(configuration.getInt("trafficlight.roi.x"));
            roi.setY(configuration.getInt("trafficlight.roi.y"));
            roi.setWidth(configuration.getInt("trafficlight.roi.width"));
            roi.setHeight(configuration.getInt("trafficlight.roi.height"));
            trafficLightSettings.setRoi(roi);

            RoadSettings roadSettings = new RoadSettings();
            roadSettings.setRoiHeight(configuration.getInt("road.roi.height"));
            roadSettings.setCannyThreshold1(configuration.getInt("road.canny.threshold1"));
            roadSettings.setCannyThreshold2(configuration.getInt("road.canny.threshold2"));
            roadSettings.setCannyApertureSize(configuration.getInt("road.canny.apertureSize"));

            roadSettings.setLinesThreshold(configuration.getInt("road.lines.threshold"));
            roadSettings.setLinesMinLineSize(configuration.getInt("road.lines.minLineSize"));
            roadSettings.setLinesMaxLineGap(configuration.getInt("road.lines.maxLineGap"));

            autonomousSettings.setRoadSettings(roadSettings);

            bind(AutonomousSettings.class).toInstance(autonomousSettings);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("lasforce.properties could not be found on the classpath.");
        }
    }
}