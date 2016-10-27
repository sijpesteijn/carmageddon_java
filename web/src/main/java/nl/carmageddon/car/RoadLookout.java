package nl.carmageddon.car;

import javafx.geometry.Orientation;
import nl.carmageddon.domain.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static java.lang.Math.*;
import static nl.carmageddon.MatUtils.getCenterPoint;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class RoadLookout extends Observable implements Lookout<RoadLookoutView> {

    private static final Logger logger = LoggerFactory.getLogger(TrafficLightLookout.class);

    private boolean run;

    private LookoutResult result;

    private RoadSettings settings;

    private Camera camera;

    private CarInstructionSender carInstructionSender;

    private FinishLineHelper finishLineHelper;

    private LeftLineHelper leftLineHelper;

    private RightLineHelper rightLineHelper;

    @Inject
    public RoadLookout(Camera camera, CarInstructionSender carInstructionSender,
            CarmageddonSettings carmageddonSettings) {
        this.camera = camera;
        this.carInstructionSender = carInstructionSender;
        this.settings = carmageddonSettings.getRoadSettings();
        this.finishLineHelper = new FinishLineHelper(carmageddonSettings.getCameraDimension());
        this.leftLineHelper = new LeftLineHelper(carmageddonSettings.getCameraDimension());
        this.rightLineHelper = new RightLineHelper(carmageddonSettings.getCameraDimension());
    }

    @Override
    public LookoutResult start() {
        run = true;
        finishLineHelper.reset();
        while (run) {
            Mat snapshot = this.camera.makeSnapshot();
            RoadLookoutView roadLookoutView = getCurrentView(snapshot.clone());
            addViewToMat(snapshot, roadLookoutView);
            // Als race gestopt is of we zijn de 1e finish line gepasseerd en de 2e is dichterbij dan 40 pixel
            if (!run ||
                finishLineHelper.pastFirstAndCloserThan(roadLookoutView, 10)) {
                breakCar(-18);
                result = new LookoutResult(AutonomousStatus.RACE_FINISHED, snapshot);
                notifyClients(result);
            }
            // als er geen linker en rechter lijn zijn en we zijn nog niet over de 1e finish line -> stoppen, want dan
            // geen weg meer.
            else if (run &&
                     !finishLineHelper.isPastFirstFinsihLine(roadLookoutView) &&
                     roadLookoutView.getLeftLane() == null &&
                     roadLookoutView.getRightLane() == null) {
                carInstructionSender.sendMessage("throttle", 0);
                result = new LookoutResult(AutonomousStatus.NO_ROAD, snapshot);
                notifyClients(result);
                run = false;

            }
            else { // Gas op de plank
                instructCar(roadLookoutView);
                result = new LookoutResult(AutonomousStatus.RACING, snapshot);
                notifyClients(result);
            }
        }
        return result;
    }

    /**
     * Stop de auto
     * @param velocity
     */
    private void breakCar(int velocity) {
        try {
            carInstructionSender.sendMessage("throttle", 0);
            Thread.sleep(200);
            carInstructionSender.sendMessage("throttle", velocity);
            Thread.sleep(800);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            carInstructionSender.sendMessage("throttle", 0);
        }
    }

    @Override
    public void stop() {
        run = false;
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
    }

    int index = 0;
    public RoadLookoutView getCurrentView(Mat snapshot) {
        try {
            RoadLookoutView view = new RoadLookoutView();

            List<Point> vPoints = findPointPairsInMath(snapshot, settings.getLaneLineSettings(),
                                                       settings.getRoiHeight());
            List<Point> verticalPoints = getPoints(Orientation.VERTICAL, vPoints);
            if (verticalPoints.size() > 0) {
                List<Line> verticalLines = createLines(verticalPoints);
                view.setRoadLines(verticalLines);
                Point center = getCenterPoint(verticalPoints);
                view.setLaneCenter(center);
                // Splits de verticale punten in linker en rechter
                Line leftLine = leftLineHelper.findLeftLine(verticalPoints);
                view.setLeftLane(leftLine);

                Line rightLine = rightLineHelper.findRightLine(verticalPoints);
                view.setRightLane(rightLine);
            }

            List<Point> hPoints = findPointPairsInMath(snapshot, settings.getFinishLineSettings(),
                                                       settings.getRoiHeight());
            List<Point> horizontalPoints = getPoints(Orientation.HORIZONTAL, hPoints);
            if (horizontalPoints.size() > 0) {
                List<Line> horizontalLines = createLines(horizontalPoints);
                view.setHorizontalLines(horizontalLines);
                Point center = getCenterPoint(horizontalPoints);
                view.setFinishCenter(center);
                // Zoek finish lijnen.
                List<Line> finishLines = finishLineHelper.findFinishLines(horizontalPoints);
                view.setFinishLines(finishLines);
            }

            return view;
        } catch (Exception e) {
            // TODO logback logt geen opencv
            logger.error("!!!!!!ERROR VIEW: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<Line> createLines(List<Point> points) {
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < points.size(); i = i + 2) {
            lines.add(new Line(points.get(i), points.get(i + 1)));
        }
        return lines;
    }

    private List<Point> getPoints(Orientation orientation, List<Point> allPoints) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < allPoints.size(); i = i + 2) {
            Point start = allPoints.get(i);
            Point end = allPoints.get(i + 1);
            double angle = toDegrees(atan2(abs(start.y - end.y), abs(start.x - end.x)));
            // filter vertical/horizontal
            if (orientation == Orientation.VERTICAL && angle > 10) {
                points.add(start);
                points.add(end);
            }
            else if (orientation == Orientation.HORIZONTAL && angle < 15) {
                points.add(start);
                points.add(end);
            }
        }
        return points;
    }

    private List<Point> findPointPairsInMath(Mat snapshot, LineSettings lineSettings, int roiHeight) {
        // Get region to look at
        Rect roi = new Rect(0, roiHeight, snapshot.width(), snapshot.height() - roiHeight);
        Mat roiMath = new Mat(snapshot, roi);

        // Blur and convert to gray
        Imgproc.GaussianBlur(roiMath, roiMath, new Size(3, 3), 3);
        Imgproc.cvtColor(roiMath, roiMath, Imgproc.COLOR_RGB2GRAY, 0);

        // Edge detection
        Imgproc.Canny(roiMath, roiMath, lineSettings.getCannyThreshold1(), lineSettings.getCannyThreshold2(),
                      lineSettings.getCannyApertureSize(), false);

        // Find the lines
        Mat lines = new Mat();
        Imgproc.HoughLinesP(roiMath, lines, 1, Math.PI / 180, lineSettings.getLinesThreshold(),
                            lineSettings.getLinesMinLineSize(), lineSettings.getLinesMaxLineGap());

        List<Point> points = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1] + roi.y,
                    x2 = vec[2],
                    y2 = vec[3] + roi.y;
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            points.add(start);
            points.add(end);
        }
        return points;
    }

    private void instructCar(RoadLookoutView view) {
        if (view.getLeftLane() == null) {
            carInstructionSender.sendMessage("throttle", 30);
            carInstructionSender.sendMessage("angle", -20);
        }
        else if (view.getRightLane() == null) {
            carInstructionSender.sendMessage("throttle", 30);
            carInstructionSender.sendMessage("angle", 20);
        }
        else {
            carInstructionSender.sendMessage("throttle", 32);
            carInstructionSender.sendMessage("angle", 0);
        }
    }

    public void addViewToMat(Mat snapshot, RoadLookoutView view) {
        try {
            if (this.settings.isShowRoadLines() && view.getRoadLines() != null && view.getRoadLines().size() > 0) {
                circle(snapshot, view.getLaneCenter(), 5, new Scalar(0, 0, 255), 3);
                view.getRoadLines().forEach(roadLine -> {
                    line(snapshot, roadLine.getStart(), roadLine.getEnd(), new Scalar(0, 255, 0), 2);
                });
            }
            if (this.settings.isShowFinishLines() && view.getFinishLines() != null &&
                view.getFinishLines().size() > 0) {
                logger.debug("Finish lines: " + view.getFinishLines().size());
                circle(snapshot, view.getFinishCenter(), 5, new Scalar(255, 0, 255), 3);
                if (view.getHorizontalLines() != null) {
                    view.getHorizontalLines().forEach(line -> line(snapshot, line.getStart(), line.getEnd(), new Scalar
                            (0, 255, 255), 2));
                }
                view.getFinishLines().forEach(finishLine -> {
                    line(snapshot, finishLine.getStart(), finishLine.getEnd(), new Scalar(255, 255, 0), 2);
                });
            }
        } catch (Exception e) {
            // TODO logback logt geen opencv
            logger.error("!!!!!!ERROR ADD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setRoadSettings(RoadSettings settings) {
        this.settings = settings;
    }
}
