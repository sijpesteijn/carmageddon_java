package nl.carmageddon.service;

import nl.carmageddon.domain.*;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.util.stream.Collectors.toList;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class RoadLookout extends Observable implements Lookout {
    private static final Logger logger = LoggerFactory.getLogger(TrafficLightLookout.class);
    private boolean run;
    private LookoutResult result;
    private long delay;
    private RoadSettings settings;

    private Camera camera;

    private CarInstructionSender carInstructionSender;

    private CarmageddonSettings carmageddonSettings;

    @Inject
    public RoadLookout(Camera camera, CarInstructionSender carInstructionSender, CarmageddonSettings carmageddonSettings) {
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
            Mat snapshot = this.camera.makeSnapshot();
            LinesView linesView = detectLines(snapshot.clone());
            addRoadHighlights(linesView, snapshot);
            // Nu heel grof kijken of finish lijn te dichtbij komt, dan stoppen.
            if (!run || snapshot.height() - linesView.getFinishLine().getDistance() <  70) {
                carInstructionSender.sendMessage("throttle", 0);
                result = new LookoutResult(AutonomousStatus.RACE_FINISHED, snapshot);
                notifyClients(result);
                run = false;
            } else // als er geen linker en rechter pca zijn. stoppen, want dan geen weg meer.
                if (run && linesView.getLeftPca() == null && linesView.getRightPca() == null ) {
                carInstructionSender.sendMessage("throttle", 0);
                result = new LookoutResult(AutonomousStatus.NO_ROAD, snapshot);
                notifyClients(result);
                run = false;
            } else { // Gas op de plank
                carInstructionSender.sendMessage("throttle", 18);
                carInstructionSender.sendMessage("angle", linesView.getAngle());
                result = new LookoutResult(AutonomousStatus.RACING, snapshot);
                notifyClients(result);
            }
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
        Mat result = roiMath.clone();
        view.setRoi(roi);

        // Blur and convert to gray
        Imgproc.GaussianBlur(roiMath, roiMath, new Size(3, 3), 3);
        Imgproc.cvtColor(roiMath, roiMath, Imgproc.COLOR_RGB2GRAY, 0);
        if (settings.getViewType() == ViewType.baw) {
            result = roiMath.clone();
        }

        // Edge detection
        Imgproc.Canny(roiMath, roiMath, settings.getCannyThreshold1(), settings.getCannyThreshold2(),
                      settings.getCannyApertureSize(),
                      false);
        if (settings.getViewType() == ViewType.canny) {
            result = roiMath.clone();
        }

        // Find the lines
        Imgproc.HoughLinesP(roiMath, lines, 1, Math.PI / 180, settings.getLinesThreshold(),
                            settings.getLinesMinLineSize(),
                            settings.getLinesMaxLineGap());


        // Verzamel horizontale en verticale punten
        List<Point> horizontalPoints = new ArrayList<>();
        List<Point> verticalPoints = new ArrayList<>();
        List<Line> horizontalLines = new ArrayList<>();
        List<Line> verticalLines = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1] + roi.y,
                    x2 = vec[2],
                    y2 = vec[3] + roi.y;
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            double angle = toDegrees(atan2(abs(y1 - y2),abs(x1 - x2)));
            // filter vertical/horizontal
            if (angle > 10) {
                verticalPoints.add(start);
                verticalPoints.add(end);
                verticalLines.add(new Line(start, end));
            }
            else if (angle < 80){
                horizontalPoints.add(start);
                horizontalPoints.add(end);
                horizontalLines.add(new Line(start, end));
            }
        }
        view.setRoadLines(verticalLines);
        view.setFinishLines(horizontalLines);

        Double averageX = verticalPoints.stream().map(p -> p.x).collect(Collectors.averagingInt(x -> x.intValue()));
        // Splits de verticale punten in linker en rechter
        List<Point> leftPoints = verticalPoints.stream().sorted((p1, p2) -> compareTo(p1.y, p2.y))
                                             .filter(p -> p.x > averageX.intValue())
                                             .collect(toList());
        List<Point> rightPoints = verticalPoints.stream().sorted((p1, p2) -> compareTo(p1.y, p2.y))
                                              .filter(p -> p.x <= averageX.intValue())
                                              .collect(toList());

        // Bereken trendline voor horizontale punten
        PCA finishPca = calculatePCA(horizontalPoints);
        view.setFinishPca(finishPca);
        // Bereken trendline voor linker punten
        PCA leftPca = calculatePCA(leftPoints);
        view.setLeftPca(leftPca);
        // Bereken trendline voor rechter punten
        PCA rightPca = calculatePCA(rightPoints);
        view.setRightPca(rightPca);

        // Average lijn bepalen aan de hand van de hoeken van de linker en rechter lijnen.
        double angle = leftPca.getAngle() - rightPca.getAngle();
        Point startAverageLine = new Point(averageX, 120);
        Point endAverageLine = new Point(averageX, 240);
        Line averageLine = new Line(startAverageLine, endAverageLine);

        view.setAverageLine(averageLine);
        view.setResult(result);
        return view;
    }

    public PCA calculatePCA(List<Point> points) {
        PCA pca = new PCA();
        double averageX = points.stream().mapToDouble(point -> point.x).average().getAsDouble();
        double averageY = points.stream().mapToDouble(point -> point.y).average().getAsDouble();
        Point center = new Point(averageX, averageY);
        pca.setCenter(center);

        double[][] pointMatrix = points.stream().map(point ->
                         new double[] { point.x - averageX, point.y - averageY }).toArray(double[][] :: new);

        RealMatrix mx = MatrixUtils.createRealMatrix(pointMatrix);
        RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
        EigenDecomposition decomposition = new EigenDecomposition(cov);

        Point axisX = new Point(center.x + (0.02 * decomposition.getRealEigenvalue(0) * decomposition.getV().getEntry(0,0)),
                                center.y + (0.02 * decomposition.getRealEigenvalue(0) * decomposition.getV().getEntry(0,1)));

        Point axisY = new Point(center.x - (0.02 * decomposition.getRealEigenvalue(1) * decomposition.getV().getEntry(1,0)),
                                 center.y + (0.02 * decomposition.getRealEigenvalue(1) * decomposition.getV().getEntry(1,1)));
        pca.setAxisX(axisX);
        pca.setAxisY(axisY);

        double angle = toDegrees(atan(decomposition.getRealEigenvalues()[1]/decomposition.getRealEigenvalues()[0]));
        pca.setAngle(angle);
        return pca;
    }

    private int compareTo(double x, double y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public void addRoadHighlights(LinesView view, Mat snapshot) {
        Rect roi = view.getRoi();
        view.getResult().copyTo(snapshot.submat(roi));

        if (view.getRoadLines().size() > 0) {
            view.getRoadLines().forEach(roadLine -> {
                line(snapshot, roadLine.getStart(), roadLine.getEnd(), new Scalar(0, 255, 0), 2);
            });
            view.getFinishLines().forEach(finishLine -> {
                line(snapshot, finishLine.getStart(), finishLine.getEnd(), new Scalar(255, 255, 0), 2);
            });
            line(snapshot, view.getAverageLine().getStart(), view.getAverageLine().getEnd(), new Scalar(255, 0, 0), 2);
        }

        circle(snapshot, view.getFinishPca().getCenter(), 5, new Scalar(255, 0, 255), 2);
        drawAxis(snapshot, view.getFinishPca().getCenter(), view.getFinishPca().getAxisX(), new Scalar(0, 255, 0), 1);
        drawAxis(snapshot, view.getFinishPca().getCenter(), view.getFinishPca().getAxisY(), new Scalar(255, 255, 0), 5);

        circle(snapshot, view.getLeftPca().getCenter(), 5, new Scalar(255, 0, 255), 2);
        drawAxis(snapshot, view.getLeftPca().getCenter(), view.getLeftPca().getAxisX(), new Scalar(0, 255, 0), 1);
        drawAxis(snapshot, view.getLeftPca().getCenter(), view.getLeftPca().getAxisY(), new Scalar(255, 255, 0), 5);

        circle(snapshot, view.getRightPca().getCenter(), 5, new Scalar(255, 0, 255), 2);
        drawAxis(snapshot, view.getRightPca().getCenter(), view.getRightPca().getAxisX(), new Scalar(0, 255, 0), 1);
        drawAxis(snapshot, view.getRightPca().getCenter(), view.getRightPca().getAxisY(), new Scalar(255, 255, 0), 5);
    }

    public void drawAxis(Mat img, Point p, Point q, Scalar colour, double scale) {
        double angle;
        double hypotenuse;
        angle = atan2(p.y - q.y, p.x - q.x);
        hypotenuse = sqrt((p.y-q.y) * (p.y - q.y) + (p.x - q.x) * (p.x - q.x));
//        logger.debug("Degrees: " + toDegrees(angle));
        // Here we lengthen the arrow by a factor of scale
        q.x = (int) (p.x - scale * hypotenuse * cos(angle));
        q.y = (int) (p.y - scale * hypotenuse * sin(angle));
        line(img, p, q, colour, 1);
        // create the arrow hooks
        p.x = (int) (q.x + 9 * cos(angle + PI / 4));
        p.y = (int) (q.y + 9 * sin(angle + PI / 4));
        line(img, p, q, colour, 1);
        p.x = (int) (q.x + 9 * cos(angle - PI / 4));
        p.y = (int) (q.y + 9 * sin(angle - PI / 4));
        line(img, p, q, colour, 1);
    }

    public void setRoadSettings(RoadSettings settings) {
        this.settings = settings;
    }
}
