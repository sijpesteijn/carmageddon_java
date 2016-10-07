package nl.carmageddon.service;

import nl.carmageddon.domain.Car;
import nl.carmageddon.domain.LinesView;
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

/**
 * @author Gijs Sijpesteijn
 */
@RunWith(MockitoJUnitRunner.class)
public class RoadLookoutTest {
    @Mock
    private Car car;

    @InjectMocks
    private RoadLookout lookout;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    @Test
    public void detectLines() throws Exception {
        Mat img = Imgcodecs.imread("./src/main/resources/ws4.jpg");
        lookout.setRoiHeight(120);
        LinesView linesView = lookout.detectLines(img);

        lookout.addRoadHighlights(linesView, img);
        imgshow(img);

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