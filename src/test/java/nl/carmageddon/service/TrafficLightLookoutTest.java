package nl.carmageddon.service;

import nl.carmageddon.domain.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import static org.junit.Assert.assertTrue;

/**
 * @author Gijs Sijpesteijn
 */
@RunWith(MockitoJUnitRunner.class)
public class TrafficLightLookoutTest {
    @Mock
    private Car car;

    @InjectMocks
    private TrafficLightLookout lookout;

    private static TrafficLightSettings settings = new TrafficLightSettings();

    static {
        settings.setRoi(new ROI(58,35,25,25));
        settings.setMinDimension(new Dimension(4,4));
        settings.setMaxDimension(new Dimension(40,40));
        settings.setLowerHSVMin(new HSV(2,87,191));
        settings.setLowerHSVMax(new HSV(0,255,255));
        settings.setUpperHSVMin(new HSV(3,100,100));
        settings.setUpperHSVMax(new HSV(365,100,100));
    }

    @BeforeClass
    public static void setup() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void detectLines() throws Exception {
        Mat img = Imgcodecs.imread("./src/main/resources/ws.jpg");
        lookout.setTrafficLightSettings(settings);
        TrafficLightView linesView = lookout.getTrafficLightView(img);
        assertTrue(linesView.getFoundRectangles().size() == 1);

    }

}