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

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class TrafficLightLookout extends Observable implements Lookout {

    private static final Logger logger = LoggerFactory.getLogger(TrafficLightLookout.class);
    private boolean stop = true;
    private Car car;
    private TrafficLightSettings settings;
    private LookoutResult result;
    private long delay;

    @Inject
    public TrafficLightLookout(Car car) {
        this.car = car;
    }

    @Override
    public LookoutResult start() {
        stop = false;
        while (!stop) {
            result = lookForTrafficLight();
            if (result.getStatus() == AutonomousStatus.TRAFFIC_LIGHT_FOUND) {
                result = waitForGo((TrafficLightLookoutResult) result);
                if (result.getStatus() == AutonomousStatus.TRAFFIC_LIGHT_OFF) {
                    stop = true;
                }
            }
            else {
                return result;
            }
        }
        return result;
    }

    private LookoutResult waitForGo(TrafficLightLookoutResult trafficLightLookoutResult) {
        boolean lightsOff = false;
        List<Rect> trafficLightLookoutResultShapes = trafficLightLookoutResult.getRects();
        while (!stop && !lightsOff) {
            Mat snapshot = this.car.getCamera().makeSnapshot();
            TrafficLightView view = getTrafficLightView(snapshot.clone());
            addTrafficLightHighlight(view, snapshot);
            if (!trafficLightOff(trafficLightLookoutResultShapes, view.getFoundRectangles())) {
                result = new LookoutResult(AutonomousStatus.TRAFFIC_LIGHT_ON, snapshot);
            }
            else {
                result = new LookoutResult(AutonomousStatus.TRAFFIC_LIGHT_OFF, snapshot);
                lightsOff = true;
            }
            notifyClients(result);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // TODO dit is wel een beetje te radicaal.
    private boolean trafficLightOff(List<Rect> trafficLightShapes, List<Rect> shapes) {
        if (shapes.size() == 0) {
            return true;
        }
        return false;
    }

    private LookoutResult lookForTrafficLight() {
        boolean found = false;
        while (!stop && !found) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                logger.debug("Looking for a traffic light. " + e.getMessage());
            }
            Mat snapshot = this.car.getCamera().makeSnapshot();
            TrafficLightView view = getTrafficLightView(snapshot);
            addTrafficLightHighlight(view, snapshot);
            if (view.getFoundRectangles().size() == 1) {
                result = new TrafficLightLookoutResult(AutonomousStatus.TRAFFIC_LIGHT_FOUND, snapshot, view
                        .getFoundRectangles());
                notifyClients(result);
                found = true;
            }
            else if (!stop) {
                result = new LookoutResult(AutonomousStatus.NO_TRAFFIC_LIGHT, snapshot);
                notifyClients(result);
            }
        }
        return result;
    }

    public void addTrafficLightHighlight(TrafficLightView view, Mat snapshot) {
        ROI roi = view.getRoi();
        view.getResult().copyTo(snapshot.submat(new Rect(roi.getX(), roi.getY(), roi.getWidth(), roi.getHeight())));
        for(int i=0;i<view.getFoundRectangles().size();i++) {
            Rect rect = view.getFoundRectangles().get(i);
            Imgproc.rectangle(snapshot, new Point(rect.x + roi.getX(), rect.y + roi.getY()),
                              new Point(rect.x + rect.width + roi.getX(), rect.y + rect.height + roi.getY()),
                              new Scalar(103, 255, 255));
        }

    }

    // TODO betere detectie. nu te ruim
    public TrafficLightView getTrafficLightView(Mat ofByte) {
        TrafficLightView view = new TrafficLightView();
        List<MatOfPoint> contours = new ArrayList();
        List<Rect> inRange = new ArrayList();
        Mat lower = new Mat();
        Mat upper = new Mat();

        // Get just a region to look at
        ROI roi = settings.getRoi();
        Rect region = new Rect(roi.getX(), roi.getY(), roi.getWidth(), roi.getHeight());
        Mat roiMath = new Mat(ofByte.clone(), region);
        Mat result = roiMath.clone();

        // Convert to HSV
        Imgproc.cvtColor(roiMath, roiMath, Imgproc.COLOR_BGR2HSV);
        if (settings.getViewType() == ViewType.hsv) {
            result = roiMath.clone();
        }

        // Filter lower and upper color.
        Core.inRange(roiMath, buildScalar(settings.getLowerHSVMin()), buildScalar(settings.getLowerHSVMax()), lower);
        Core.inRange(roiMath, buildScalar(settings.getUpperHSVMin()), buildScalar(settings.getUpperHSVMax()), upper);
        Core.addWeighted(lower, 1.0, upper, 1.0, 0.0, roiMath);

        // Blur
        Imgproc.GaussianBlur(roiMath, roiMath, new Size(3, 3), 0);

        // Find contours
        Imgproc.findContours(roiMath, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        if (settings.getViewType() == ViewType.baw) {
            Imgproc.cvtColor(roiMath.clone(), result, Imgproc.COLOR_GRAY2BGR);
        }
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (contourInRange(rect)) {
                inRange.add(rect);
            }
        }
        view.setResult(result);
        view.setFoundRectangles(inRange);
        view.setRoi(roi);
        return view;
    }

    private boolean contourInRange(Rect rect) {
        int height = this.settings.getMinDimension().getHeight();
        int width = this.settings.getMinDimension().getWidth();
        if (height != -1 && rect.height < height) {
            return false;
        }
        if (width != -1 && rect.width < width) {
            return false;
        }

        height = this.settings.getMaxDimension().getHeight();
        width = this.settings.getMaxDimension().getWidth();
        if (height != -1 && rect.height > height) {
            return false;
        }
        if (width != -1 && rect.width > width) {
            return false;
        }

        return true;
    }

    public void stop() {
        stop = true;
    }

    private Scalar buildScalar(HSV HSV) {
        return new Scalar(HSV.getHue(), HSV.getSaturation(), HSV.getBrightness());
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setTrafficLightSettings(TrafficLightSettings settings) {
        this.settings = settings;
    }

    class TrafficLightLookoutResult extends LookoutResult {

        private final List<Rect> rects;

        public TrafficLightLookoutResult(AutonomousStatus status, Mat snapshot, List<Rect> rects) {
            super(status, snapshot);
            this.rects = rects;
        }

        public List<Rect> getRects() {
            return rects;
        }
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
    }

}
