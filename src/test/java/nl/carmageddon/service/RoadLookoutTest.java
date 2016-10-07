package nl.carmageddon.service;

import nl.carmageddon.domain.Car;
import nl.carmageddon.domain.LinesView;
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
public class RoadLookoutTest {
    @Mock
    private Car car;

    @InjectMocks
    private RoadLookout lookout;

    @BeforeClass
    public static void setup() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void detectLines() throws Exception {
        Mat img = Imgcodecs.imread("./src/main/resources/ws4.jpg");
        lookout.setRoiHeight(120);
        LinesView linesView = lookout.detectLines(img);
        assertTrue(linesView.getRoadLines().size() > 0);

    }

}