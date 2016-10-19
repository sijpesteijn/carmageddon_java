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

import static org.opencv.imgproc.Imgproc.line;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class RoadLookout extends Observable implements Lookout {
    private Car car;
    private boolean run;
    private LookoutResult result;
    private long delay;
    private ViewType viewType;
    private int angle = 20;

    private RoadSettings settings;

    @Inject
    public RoadLookout(Car car) {
        this.car = car;
    }

    @Override
    public LookoutResult start() {
        run = true;
        while(run) {
            car.getEngine().setThrottle(20);
            int index = 0;
            while(run && index++ < 10) {
                Mat snapshot = this.car.getCamera().makeSnapshot();
                LinesView linesView = detectLines(snapshot.clone());
                addRoadHighlights(linesView, snapshot);
                this.car.getEngine().setThrottle(20);
                result = new LookoutResult(AutonomousStatus.RACING, this.car.getCamera().getImageBytes(snapshot));
                notifyClients(result);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            car.getEngine().setThrottle(0);
            if (run) {
                result = new LookoutResult(AutonomousStatus.RACE_FINISHED,
                                           this.car.getCamera().makeSnapshotInByteArray());
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
        Rect region = new Rect(0, settings.getRoiHeight(), snapshot.width(), snapshot.height() - settings.getRoiHeight());
        ROI roi = new ROI(0, settings.getRoiHeight(), snapshot.width(), snapshot.height() - settings.getRoiHeight());
        Mat roiMath = new Mat(snapshot,region);
        Mat result = roiMath.clone();

        if (settings.getViewType() == ViewType.hsv) {
            Imgproc.cvtColor(roiMath, result, Imgproc.COLOR_BGR2HSV);
        }
        // Blur and convert to gray
        Imgproc.GaussianBlur(roiMath, roiMath, new Size(3, 3), 3);
        Imgproc.cvtColor(roiMath, roiMath, Imgproc.COLOR_RGB2GRAY, 0);
        Imgproc.equalizeHist(roiMath, roiMath);

        if (settings.getViewType() == ViewType.baw) {
            Imgproc.cvtColor(roiMath.clone(), result, Imgproc.COLOR_GRAY2BGR);
        }

        // Edge detection
        Imgproc.Canny(roiMath, roiMath, settings.getCannyThreshold1(), settings.getCannyThreshold2(), settings.getCannyApertureSize(),
                      false);
        if (settings.getViewType() == ViewType.canny) {
            Imgproc.cvtColor(roiMath.clone(), result, Imgproc.COLOR_GRAY2BGR);
        }

        // Find the lines
        Imgproc.HoughLinesP(roiMath, lines, 1, Math.PI / 180, settings.getLinesThreshold(), settings.getLinesMinLineSize(),
                            settings.getLinesMaxLineGap());

        this.car.getSteer().setAngle(0);
        List<Point> points = new ArrayList<>();
        List<Line> roadLines = new ArrayList<>();
        List<Line> finishLines = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1] + region.y,
                    x2 = vec[2],
                    y2 = vec[3] + region.y;
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);


            // filter vertical/horizontal
            if (Math.abs(x1 - x2) > 10 && Math.abs(y1 - y2) > 10) {
                points.add(start);
                points.add(end);
                roadLines.add(new Line(start, end));
            } else {
                finishLines.add(new Line(start, end));
            }
        }

        if (points.size() > 0 ) {
            final Double maxX = points.stream().max(getPointComparator()).map(p -> p.x).get();
            final Double minX = points.stream().min(getPointComparator()).map(p -> p.x).get();
            view.setAverageLine(new Line(new Point((maxX - minX) / 2, region.y),
                                         new Point((maxX - minX) / 2, region.y + region.height)));
            int step = region.width/80;
            Double center = new Double(region.width/2);
            int angle = (int)((maxX-minX) - center)/step;
            this.car.getSteer().setAngle(angle);
        } else { // Geen average gevonden dus dan maar rechtdoor?
            this.car.getSteer().setAngle(0);
        }
        view.setRoadLines(roadLines);
        view.setFinishLines(finishLines);
        view.setRoi(roi);
        view.setResult(result);
        return view;
    }

    public void addRoadHighlights(LinesView view, Mat snapshot) {
        ROI roi = view.getRoi();
        view.getResult().copyTo(snapshot.submat(new Rect(roi.getX(), roi.getY(), roi.getWidth(), roi.getHeight())));
        if (view.getRoadLines().size() > 0) {
            view.getRoadLines().forEach(roadLine -> {
                line(snapshot, roadLine.getStart(), roadLine.getEnd(), new Scalar(0, 255, 0), 2);
            });
            view.getFinishLines().forEach(finishLine -> {
                line(snapshot, finishLine.getStart(), finishLine.getEnd(), new Scalar(255, 255, 0), 2);
            });
            line(snapshot, view.getAverageLine().getStart(), view.getAverageLine().getEnd(), new Scalar(255,0,0),2);
        }
    }


    private static Comparator<Point> getPointComparator() {
        return (p1, p2) -> p1.x - p2.x == 0 ? 0 : p1.x - p2.x > 0 ? 1 : -1;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public void setRoadSettings(RoadSettings settings) {
        this.settings = settings;
    }
}
