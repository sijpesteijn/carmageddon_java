package nl.carmageddon.service;

import nl.carmageddon.domain.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static org.opencv.imgproc.Imgproc.line;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class RoadLookout2 extends Observable implements Lookout {
    private boolean run;
    private LookoutResult result;
    private long delay;
    private ViewType viewType;
    private RoadSettings settings;
    private Camera camera;
    private CarInstructionSender carInstructionSender;

    private CarmageddonSettings carmageddonSettings;

    @Inject
    public RoadLookout2(Camera camera, CarInstructionSender carInstructionSender, CarmageddonSettings carmageddonSettings) {
        this.camera = camera;
        this.carInstructionSender = carInstructionSender;
        this.carmageddonSettings = carmageddonSettings;
    }

    private static Comparator<Point> getPointComparator() {
        return (p1, p2) -> p1.x - p2.x == 0 ? 0 : p1.x - p2.x > 0 ? 1 : -1;
    }

    @Override
    public LookoutResult start() {
        run = true;
        while (run) {
            int index = 0;
            while (run && index++ < 10) {
                Mat snapshot = this.camera.makeSnapshot();
                LinesView linesView = detectLines(snapshot.clone());
                addRoadHighlights(linesView, snapshot);
                result = new LookoutResult(AutonomousStatus.RACING, snapshot);
                this.carInstructionSender.sendMessage("throttle", 18);
                notifyClients(result);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            if (run) {
                this.carInstructionSender.sendMessage("throttle", 0);
                result = new LookoutResult(AutonomousStatus.RACE_FINISHED,
                        this.camera.makeSnapshot());
                notifyClients(result);
            }
            run = false;
        }
        return result;
    }

    @Override
    public void stop() {
        run = false;
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public LinesView detectLines(Mat snapshot) {
        LinesView view = new LinesView();
        Mat lines = new Mat();

        // Get region to look at
        Rect roi = new Rect(0, settings.getRoiHeight(), snapshot.width(), snapshot.height() - settings.getRoiHeight());
        Mat roiMath = new Mat(snapshot, roi);

        // Blur and convert to gray
        Imgproc.GaussianBlur(roiMath, roiMath, new Size(3, 3), 3);
        Imgproc.cvtColor(roiMath, roiMath, Imgproc.COLOR_RGB2GRAY, 0);

        // Edge detection
        Imgproc.Canny(roiMath, roiMath, settings.getCannyThreshold1(), settings.getCannyThreshold2(), settings.getCannyApertureSize(),
                false);

        // Find the lines
        Imgproc.HoughLinesP(roiMath, lines, 1, Math.PI / 180, settings.getLinesThreshold(), settings.getLinesMinLineSize(),
                settings.getLinesMaxLineGap());

        List<Point> points = new ArrayList<>();
        List<Line> roadLines = new ArrayList<>();
        List<Line> finishLines = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1] + roi.y,
                    x2 = vec[2],
                    y2 = vec[3] + roi.y;
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);


            // filter vertical/horizontal
            if (Math.abs(x1 - x2) > 50 && Math.abs(y1 - y2) > 50) {
                points.add(start);
                points.add(end);
                roadLines.add(new Line(start, end));
            } else {
                finishLines.add(new Line(start, end));
            }
        }
        final Double averageX = getCenterX(points);

//        final List<Point> rightLines = points.stream().sorted((p1, p2) -> compareTo(p1.y, p2.y))
//                                            .filter(p -> p.x > averageX.intValue())
//                                            .collect(Collectors.toList());

        List<Line> leftLines = roadLines.stream().filter(line -> { return (line.getStart().x < averageX.intValue() &&
        line.getEnd().x < averageX.intValue());}).collect(Collectors.toList());
//        leftLines = makeSameLength(leftLines);
        leftLines.forEach(line -> {
            System.out.println((line.getStart().x < line.getEnd().x) + " " + (line.getStart().y > line.getEnd().y));
        });

//        Line averageLine = new Line(new Point( (leftLine.getStart().x + rightLine.getStart().x) /2,
//                                    (leftLine.getStart().y + rightLine.getStart().y)/2),
//                                    new Point((leftLine.getAxisX().x + rightLine.getAxisX().x)/2,
//                                    (leftLine.getAxisX().y + rightLine.getAxisX().y)/2));
        view.setResult(roiMath);
        view.setAverageLine(leftLines.get(0));
        view.setRoadLines(leftLines);
        view.setFinishLines(finishLines);
        view.setRoi(roi);
        return view;
    }

    public float getAngle(Line line) {
        float angle = (float) Math.toDegrees(Math.atan2(line.getEnd().y - line.getStart().y, line.getEnd().x - line
                .getStart().x));

//        if(angle < 0){
//            angle += 360;
//        }

        return Math.abs(angle);
    }

    private List<Line> makeSameLength(List<Line> leftLines) {
        leftLines.forEach(line -> {
            double angle = angle(line);
            double a;
            double c;
            double b;
            a = line.getStart().y;
            c = a / sin(angle);
            b = sqrt((c*c) - (a*a));
            line.getStart().y = carmageddonSettings.getCameraDimension().getHeight();
//            line.getStart().x -= b;
//
            a = line.getEnd().y - settings.getRoiHeight();
            c = a /sin(angle);
            b = sqrt((c*c) - (a*a));
            line.getEnd().y = settings.getRoiHeight();
            line.getEnd().x += b;
            System.out.println("angle: " + angle + ", a: " + a + ", b:" + b + ", c: " + c + ", x: " + line.getEnd().x +
                               ", y: " + line.getEnd().y);
        });
        return leftLines;
    }

    private double angle(Line line) {
        return ((Math.atan(Math.abs(line.getEnd().y - line.getStart().y) / Math.abs(line.getEnd().x - line.getStart().x))) * 180 / Math.PI);

    }

    private List<Point> getMedianPoints(List<Point> roadLinePair) {
        final Double averageX = getCenterX(roadLinePair);

        //System.out.println(averageX);

        final List<Point> rightLines = roadLinePair.stream().sorted((p1, p2) -> compareTo(p1.y, p2.y))
                .filter(p -> p.x > averageX.intValue())
                .collect(Collectors.toList());

        final List<Point> leftLines = roadLinePair.stream().sorted((p1, p2) -> compareTo(p1.y, p2.y))
                .filter(p -> p.x <= averageX.intValue())
                .collect(Collectors.toList());

        List<Point> median = new ArrayList<>();
        if (rightLines.size() < leftLines.size()) {
            for (int i = 0; i < rightLines.size(); i++) {
                final Point rP = rightLines.get(i);
                final Point lP = leftLines.get(i);
                median.add(new Point((rP.x + lP.x) / 2, (rP.y + lP.y) / 2));
            }
        } else {
            for (int i = 0; i < leftLines.size(); i++) {
                final Point rP = rightLines.get(i);
                final Point lP = leftLines.get(i);
                median.add(new Point((rP.x + lP.x) / 2, (rP.y + lP.y) / 2));
            }
        }

        final Double medX = getCenterX(median);
        final Double medY = getCenterY(median);

        return median;
    }

    public Double getCenterX(List<Point> roadLinePair) {
        return roadLinePair.stream().map(p -> p.x).collect(Collectors.averagingInt(x -> x.intValue()));
    }

    public Double getCenterY(List<Point> roadLinePair) {
        return roadLinePair.stream().map(p -> p.y).collect(Collectors.averagingInt(y -> y.intValue()));
    }

    private int compareTo(double x, double y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public void addRoadHighlights(LinesView view, Mat snapshot) {
        if (view.getRoadLines().size() > 0) {
            view.getRoadLines().forEach(roadLine -> {
                line(snapshot, roadLine.getStart(), roadLine.getEnd(), new Scalar(0, 255, 0), 2);
            });
            view.getFinishLines().forEach(finishLine -> {
                line(snapshot, finishLine.getStart(), finishLine.getEnd(), new Scalar(255, 255, 0), 2);
            });
            line(snapshot, view.getAverageLine().getStart(), view.getAverageLine().getEnd(), new Scalar(255, 0, 0), 2);
        }
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public void setRoadSettings(RoadSettings settings) {
        this.settings = settings;
    }
}
