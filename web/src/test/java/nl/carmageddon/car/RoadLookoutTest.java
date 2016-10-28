package nl.carmageddon.car;


import javafx.geometry.Orientation;
import nl.carmageddon.domain.Line;
import nl.carmageddon.domain.LineSettings;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.carmageddon.car.TrackLineHelper.getIntersection;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;

public class RoadLookoutTest {

    @BeforeClass
    public static void setup() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    @Test
    public void detectLines_0deg_0off() throws Exception {

        boolean rightlane = false;

        Mat img = Imgcodecs.imread("./src/main/resources/ws5.jpg");
        Mat clone = img.clone();

        LineSettings lineSettings = new LineSettings();
        lineSettings.setCannyApertureSize(3);
        lineSettings.setCannyThreshold1(55);
        lineSettings.setCannyThreshold2(155);
        lineSettings.setLinesMaxLineGap(50);
        lineSettings.setLinesMinLineSize(50);
        lineSettings.setLinesThreshold(52);

        final List<Point> pointPairsInMath = RoadLookout.findPointPairsInMath(img, lineSettings, 80);
        final List<Point> points = RoadLookout.getPoints(Orientation.VERTICAL, pointPairsInMath);

        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < points.size(); i = i + 2) {
            lines.add(new Line(points.get(i), points.get(i + 1)));
        }

        final Optional<Line> rightLine = TrackLineHelper.getRightLine(lines, rightlane);
        rightLine.ifPresent(line -> line(clone, line.getStart(), line.getEnd(), new Scalar(0, 255, 255), 2));

        final Optional<Line> leftLine = TrackLineHelper.getLeftLine(lines, rightlane);
        leftLine.ifPresent(line -> line(clone, line.getStart(), line.getEnd(), new Scalar(0, 255, 255), 2));

        List<Point> pointz = new ArrayList<>();

        rightLine.ifPresent(z -> {
            pointz.add(z.getStart());
            pointz.add(z.getEnd());
        });

        leftLine.ifPresent(z -> {
            pointz.add(z.getStart());
            pointz.add(z.getEnd());
        });

        final Line line1 = new Line(leftLine.get().getStart(), rightLine.get().getEnd());
        final Line line2 = new Line(leftLine.get().getEnd(), rightLine.get().getStart());
        // line(clone, line.getStart(), line.getEnd(), new Scalar(0, 255, 255), 2);

        final Point point = getIntersection(line1, line2).get();


        circle(clone, point, 5, new Scalar(0, 0, 255), 1);

        imwrite("result.jpg", clone);

    }
}