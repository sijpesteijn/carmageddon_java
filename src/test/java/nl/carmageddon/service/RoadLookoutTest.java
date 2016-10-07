package nl.carmageddon.service;

import nl.carmageddon.domain.Car;
import nl.carmageddon.domain.LinesView;
import nl.carmageddon.domain.RoadSettings;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertTrue;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * @author Gijs Sijpesteijn
 */
@RunWith(MockitoJUnitRunner.class)
public class RoadLookoutTest {
    @Mock
    private Car car;

    @InjectMocks
    private RoadLookout lookout;

    private static RoadSettings settings = new RoadSettings();

    static {
        settings.setRoiHeight(120);
        settings.setCannyThreshold1(80);
        settings.setCannyThreshold2(120);
        settings.setCannyApertureSize(3);
        settings.setLinesThreshold(52);
        settings.setLinesMinLineSize(50);
        settings.setLinesMaxLineGap(50);
    }

    @BeforeClass
    public static void setup() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void detectLines() throws Exception {
        lookout.setRoadSettings(settings);
        Mat img = Imgcodecs.imread("./src/main/resources/ws4.jpg");
        Mat org = img.clone();
        LinesView linesView = lookout.detectLines(img);

        lookout.addRoadHighlights(linesView, org);
        imwrite("moe.jpg", org);
//        imgshow(org);


        assertTrue(linesView.getRoadLines().size() > 0);

    }


    private static void imgshow(Mat gray) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JLabel(new ImageIcon(mat2Img(gray))));
        frame.pack();
        frame.setVisible(true);
    }

    public static BufferedImage mat2Img(Mat in) {
        BufferedImage out;
        byte[] data = new byte[in.width() * in.height() * (int) in.elemSize()];
        int type;
        in.get(0, 0, data);

        if (in.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;

        out = new BufferedImage(in.width(), in.height(), type);

        out.getRaster().setDataElements(0, 0, in.width(), in.height(), data);
        return out;
    }
}