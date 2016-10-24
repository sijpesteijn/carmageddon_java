package nl.carmageddon.service;

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
import static nl.carmageddon.service.MatUtils.getCenterPoint;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class RoadLookout extends Observable implements Lookout<LinesView> {

    private static final Logger logger = LoggerFactory.getLogger(TrafficLightLookout.class);

    private boolean run;

    private LookoutResult result;

    private RoadSettings settings;

    private Camera camera;

    private CarInstructionSender carInstructionSender;

    private FinishLineHelper finishLineHelper = new FinishLineHelper();

    private LeftLineHelper leftLineHelper;

    private RightLineHelper rightLineHelper;

    private int prevNrOfFinishLines = 0;

    @Inject
    public RoadLookout(Camera camera, CarInstructionSender carInstructionSender,
            CarmageddonSettings carmageddonSettings) {
        this.camera = camera;
        this.carInstructionSender = carInstructionSender;
        this.settings = carmageddonSettings.getRoadSettings();
        this.leftLineHelper = new LeftLineHelper(carmageddonSettings.getCameraDimension());
        this.rightLineHelper = new RightLineHelper(carmageddonSettings.getCameraDimension());
    }

    @Override
    public LookoutResult start() {
        run = true;
        prevNrOfFinishLines = 0;
        while (run) {
            Mat snapshot = this.camera.makeSnapshot();
            LinesView linesView = getCurrentView(snapshot.clone());
            addViewToMat(snapshot, linesView);
            // Nu heel grof kijken of finish lijn te dichtbij komt, dan stoppen.
            if (!run || linesView.getFinishLines().size() < prevNrOfFinishLines) {
                carInstructionSender.sendMessage("throttle", 0);
                result = new LookoutResult(AutonomousStatus.RACE_FINISHED, snapshot);
                notifyClients(result);
                run = false;
            }
            else // als er geen linker en rechter pca zijn. stoppen, want dan geen weg meer.
                if (run && linesView.getLeftLane() == null && linesView.getRightLane() == null) {
                    carInstructionSender.sendMessage("throttle", 0);
                    result = new LookoutResult(AutonomousStatus.NO_ROAD, snapshot);
                    notifyClients(result);
                    run = false;
                }
                else { // Gas op de plank
                    instructCar(linesView);
                    prevNrOfFinishLines = linesView.getFinishLines().size();
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

    public LinesView getCurrentView(Mat snapshot) {
        try {
            LinesView view = new LinesView();

            // Get region to look at
            Rect roi = new Rect(0, settings.getRoiHeight(), snapshot.width(),
                                snapshot.height() - settings.getRoiHeight());
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
            Mat lines = new Mat();
            Imgproc.HoughLinesP(roiMath, lines, 1, Math.PI / 180, settings.getLinesThreshold(),
                                settings.getLinesMinLineSize(),
                                settings.getLinesMaxLineGap());


            // Verzamel horizontale en verticale punten
            List<Point> horizontalPoints = new ArrayList<>();
            List<Point> verticalPoints = new ArrayList<>();
            List<Line> verticalLines = new ArrayList<>();
            for (int x = 0; x < lines.rows(); x++) {
                double[] vec = lines.get(x, 0);
                double x1 = vec[0],
                        y1 = vec[1] + roi.y,
                        x2 = vec[2],
                        y2 = vec[3] + roi.y;
                Point start = new Point(x1, y1);
                Point end = new Point(x2, y2);

                double angle = toDegrees(atan2(abs(y1 - y2), abs(x1 - x2)));
                // filter vertical/horizontal
                if (angle > 10) {
                    verticalPoints.add(start);
                    verticalPoints.add(end);
                    verticalLines.add(new Line(start, end));
                }
                else if (angle < 80) {
                    horizontalPoints.add(start);
                    horizontalPoints.add(end);
                }
            }
            view.setRoadLines(verticalLines);
            if (verticalPoints.size() > 0) {
                Point center = getCenterPoint(verticalPoints);
                view.setCenter(center);
                circle(snapshot, center, 5, new Scalar(0, 0, 255), 3);
                // Splits de verticale punten in linker en rechter
                Line leftLine = leftLineHelper.findLeftLine(verticalPoints);
                view.setLeftLane(leftLine);

                Line rightLine = rightLineHelper.findRightLine(verticalPoints);
                view.setRightLane(rightLine);
            }

            if (horizontalPoints.size() > 0) {
                // Zoek finish lijnen.
                List<Line> finishLines = finishLineHelper.findFinishLines(horizontalPoints);
                view.setFinishLines(finishLines);
            }

            view.setResult(result);
            return view;
        } catch (Exception e) {
            logger.error("!!!!!!ERROR VIEW: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void instructCar(LinesView view) {
        if (view.getLeftLane() == null) {
            carInstructionSender.sendMessage("throttle", 19);
            carInstructionSender.sendMessage("angle", -20);
        }
        else if (view.getRightLane() == null) {
            carInstructionSender.sendMessage("throttle", 19);
            carInstructionSender.sendMessage("angle", 20);
        }
        else {
            carInstructionSender.sendMessage("throttle", 20);
            carInstructionSender.sendMessage("angle", 0);
        }
    }

    public void addViewToMat(Mat snapshot, LinesView view) {
        try {
            if (this.settings.isShowRoadLines() && view.getRoadLines().size() > 0) {
                view.getRoadLines().forEach(roadLine -> {
                    line(snapshot, roadLine.getStart(), roadLine.getEnd(), new Scalar(0, 255, 0), 2);
                });
            }
            if (this.settings.isShowFinishLine() && view.getFinishLines().size() > 0) {
                logger.debug("Finish lines: " + view.getFinishLines().size());
                view.getFinishLines().forEach(finishLine -> {
                    line(snapshot, finishLine.getStart(), finishLine.getEnd(), new Scalar(255, 255, 0), 2);
                });
            }
        } catch (Exception e) {
            logger.error("!!!!!!ERROR ADD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void drawAxis(Mat img, Point p, Point q, Scalar colour, double scale) {
        double angle;
        double hypotenuse;
        angle = atan2(p.y - q.y, p.x - q.x);
        hypotenuse = sqrt((p.y - q.y) * (p.y - q.y) + (p.x - q.x) * (p.x - q.x));
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
