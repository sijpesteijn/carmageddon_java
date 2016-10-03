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
                result = new LookoutResult(AutonomousStatus.RACING, this.car.getCamera().getImageBytes(snapshot));
                notifyClients(result);
                try {
                    Thread.sleep(1000);
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
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);


            // filter vertical/horizontal
//            if (Math.abs(x1 - x2) > 10 && Math.abs(y1 - y2) > 10) {
                points.add(start);
                points.add(end);
//                line(canvas, start, end, new Scalar(0, 255, 0), 2);
//            }
        }

        if (points.size() > 0 ) {
            final Double maxX = points.stream().max(getPointComparator()).map(p -> p.x).get();
            final Double minX = points.stream().min(getPointComparator()).map(p -> p.x).get();
//            line(canvas, new Point((maxX - minX) / 2, 0), new Point((maxX - minX) / 2, roiMath.height()),
//                 new Scalar(255, 0, 0), 2);
            view.setMaxX(maxX);
            view.setMinX(minX);
        }
        view.setPoints(points);
        view.setRoi(roi);
        return view;
    }

    public void addRoadHighlights(LinesView view, Mat snapshot) {
        if (view.getPoints().size() > 0) {
            for (int i = 0; i < view.getPoints().size(); i = i + 2) {
                Point start = view.getPoints().get(i);
                start.y = start.y + view.getRoi().y;
                Point end = view.getPoints().get(i + 1);
                end.y = end.y + view.getRoi().y;
                line(snapshot, start, end, new Scalar(0, 255, 0), 2);
            }
            line(snapshot, new Point((view.getMaxX() - view.getMinX()) / 2, view.getRoi().y),
                 new Point((view.getMaxX() - view.getMinX()) / 2, view.getRoi().y + view.getRoi()
                         .height),
                 new Scalar(255, 0, 0), 2);
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
