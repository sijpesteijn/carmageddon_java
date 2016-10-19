package nl.carmageddon.service;

import nl.carmageddon.domain.Camera;
import nl.carmageddon.domain.Line;
import nl.carmageddon.domain.LinesView;
import nl.carmageddon.domain.RoadSettings;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.circle;

/**
 * @author Gijs Sijpesteijn
 */
@RunWith(MockitoJUnitRunner.class)
public class RoadLookoutTest {
    public static final double RAD2DEG = 180 / Math.PI;
    private static RoadSettings settings = new RoadSettings();

    static {
        settings.setRoiHeight(80);
        settings.setCannyThreshold1(0);
        settings.setCannyThreshold2(50);
        settings.setCannyApertureSize(3);
        settings.setLinesThreshold((int) Math.PI / 180);
        settings.setLinesMinLineSize(20);
        settings.setLinesMaxLineGap(50);
    }

    @Mock
    private Camera camera;
    @InjectMocks
    private RoadLookout lookout;

    @BeforeClass
    public static void setup() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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


    @Test
    public void detectLines_0deg_0off() throws Exception {
        lookout.setRoadSettings(settings);
        Mat img = Imgcodecs.imread("./src/main/resources/roadsamples/frame[-0.02 -0.00].jpeg");
        Mat org = img.clone();
        LinesView linesView = lookout.detectLines(img);
        final Line averageLine = linesView.getAverageLine();

        lookout.addRoadHighlights(linesView, org);
        imwrite("detectLines_0deg_0off.jpg", org);

        assertThat(averageLine, not(nullValue()));
        // assertThat(angle(averageLine), is(closeTo(0, 3.0)));
        assertThat(linesView.getAverageX(), is(closeTo(150, 10.0))); //image dead center
    }

    @Test
    public void detectLines_5degNeg_21off() throws Exception {
        lookout.setRoadSettings(settings);
        Mat img = Imgcodecs.imread("./src/main/resources/roadsamples/frame[-5.01 -0.21].jpeg");
        Mat org = img.clone();
        LinesView linesView = lookout.detectLines(img);
        final Line averageLine = linesView.getAverageLine();

        lookout.addRoadHighlights(linesView, org);
        imwrite("detectLines_5degNeg_21off.jpg", org);

        assertThat(averageLine, not(nullValue()));
        //assertThat(angle(averageLine), is(closeTo(-5, 2.0)));
        assertThat(linesView.getAverageX(), is(closeTo(170, 5.0))); //image dead center

    }

    @Test
    public void detectLines_10degNeg_51off() throws Exception {
        lookout.setRoadSettings(settings);
        Mat img = Imgcodecs.imread("./src/main/resources/roadsamples/frame[-10.04 -0.51].jpeg");
        Mat org = img.clone();
        LinesView linesView = lookout.detectLines(img);
        final Line averageLine = linesView.getAverageLine();

        lookout.addRoadHighlights(linesView, org);
        imwrite("detectLines_10degNeg_51off.jpg", org);

        assertThat(averageLine, not(nullValue()));
        //assertThat(angle(averageLine), is(closeTo(-10, 2.0)));
        assertThat(linesView.getAverageX(), is(closeTo(176, 5.0))); //image dead center

    }

    @Test
    public void detectLines_20degNeg_132off() throws Exception {
        lookout.setRoadSettings(settings);
        Mat img = Imgcodecs.imread("./src/main/resources/roadsamples/frame[-20.08 -1.32].jpeg");
        Mat org = img.clone();
        LinesView linesView = lookout.detectLines(img);
        final Line averageLine = linesView.getAverageLine();

        lookout.addRoadHighlights(linesView, org);
        imwrite("detectLines_20degNeg_132off.jpg", org);

        assertThat(averageLine, not(nullValue()));
        assertThat(angle(averageLine), is(closeTo(-10, 2.0)));
        assertThat(linesView.getAverageX(), is(closeTo(176, 5.0))); //image dead center

    }

    @Test
    public void testPCA() throws Exception {
        Mat mat = new Mat(350, 350, CvType.CV_8UC3, new Scalar(255,255,255));
        List<Point> points = new ArrayList<>();
        points.add(new Point(250,350-240));
        points.add(new Point(50,350-70));
        points.add(new Point(220,350-290));
        points.add(new Point(190,350-220));
        points.add(new Point(310,350-300));
        points.add(new Point(230,350-270));
        points.add(new Point(200,350-160));
        points.add(new Point(110,350-100));
        points.add(new Point(150,350-160));
        points.add(new Point(110,350-90));
        RoadLookout.PCA pca = lookout.calculatePCA(points);
        System.out.println(pca.getAngle());
        points.forEach(point -> {
            circle(mat, point, 2, new Scalar(0, 0, 255), 1);
        });
        circle(mat, pca.getCenter(), 5, new Scalar(255, 0, 0), 2);
        lookout.drawAxis(mat, pca.getCenter(), pca.getAxisX(), new Scalar(255,0,255), 0.2);
//        lookout.drawAxis(mat, pca.getCenter(), pca.getAxisY(), new Scalar(0, 255,255), 0.2);
        imwrite("pca.jpg", mat);
    }

    private double angle(Line line) {
        return ((Math.atan(Math.abs(line.getEnd().y - line.getStart().y) / Math.abs(line.getEnd().x - line.getStart().x))) * RAD2DEG) - 90;

    }
}