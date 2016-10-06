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
    private int roiHeight;
    private ViewType viewType;

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
        int threshold = 52;
        int minLineSize = 50;
        int lineGap = 50;

        // Get region to look at
        Rect roi = new Rect(0, roiHeight, snapshot.width(), snapshot.height() - roiHeight);
        Mat roiMath = new Mat(snapshot,roi);

        // Blur and convert to gray
        Imgproc.GaussianBlur(roiMath, roiMath, new Size(3, 3), 3);
        Imgproc.cvtColor(roiMath, roiMath, Imgproc.COLOR_RGB2GRAY, 0);

        // Edge detection
        Imgproc.Canny(roiMath, roiMath, 80, 120, 3, false);

        // Find the lines
        Imgproc.HoughLinesP(roiMath, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);
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
            view.setAverageLine(new Line(new Point((maxX - minX) / 2, roi.y), new Point((maxX - minX) / 2, roi.y + roi.height)));
        }
        view.setRoadLines(roadLines);
        view.setFinishLines(finishLines);
        view.setRoi(roi);
        return view;
    }

    public void addRoadHighlights(LinesView view, Mat snapshot) {
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

    public void setRoiHeight(int roiHeight) {
        this.roiHeight = roiHeight;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }
}
