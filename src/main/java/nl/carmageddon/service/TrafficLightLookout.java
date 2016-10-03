package nl.carmageddon.service;

import nl.carmageddon.domain.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
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
    private ViewType viewType;
    private long delay;

    @Inject
    public TrafficLightLookout(Car car) {
        this.car = car;
    }

    @Override
    public LookoutResult start() {
        // TODO deze check naar CPU.java
        VideoCapture camera = this.car.getCamera().getCamera();
        LookoutResult result = new LookoutResult(AutonomousStatus.NO_TRAFFIC_LIGHT, null);
        if (camera == null || !camera.isOpened()) {
            result = new LookoutResult(AutonomousStatus.NO_CAMERA, null);
            notifyClients(result);
            return result;
        }
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
                result = new LookoutResult(AutonomousStatus.TRAFFIC_LIGHT_ON, this.car.getCamera().getImageBytes(snapshot));
            }
            else {
                result = new LookoutResult(AutonomousStatus.TRAFFIC_LIGHT_OFF, this.car.getCamera().getImageBytes(snapshot));
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
                result = new TrafficLightLookoutResult(AutonomousStatus.TRAFFIC_LIGHT_FOUND,
                       this.car.getCamera().getImageBytes(snapshot), view.getFoundRectangles());
                notifyClients(result);
                found = true;
            }
            else if (!stop) {
                result = new LookoutResult(AutonomousStatus.NO_TRAFFIC_LIGHT, this.car.getCamera().getImageBytes(snapshot));
                notifyClients(result);
            }
        }
        return result;
    }

    public void addTrafficLightHighlight(TrafficLightView view, Mat snapshot) {
        for(int i=0;i<view.getFoundRectangles().size();i++) {
            Rect rect = view.getFoundRectangles().get(i);
            Imgproc.rectangle(snapshot, new Point(rect.x + view.getRoi().getX(), rect.y + view.getRoi().getY()),
                              new Point(rect.x + rect.width, rect.y + rect.height),
                              new Scalar(103, 255, 255));
        }

    }

    // TODO betere detectie. nu te ruim
    public TrafficLightView getTrafficLightView(Mat snapshot) {
        TrafficLightView view = new TrafficLightView();
        List<MatOfPoint> contours = new ArrayList();
        List<Rect> inRange = new ArrayList();
        Mat lower = new Mat();
        Mat upper = new Mat();

        // Get just a region to look at
        ROI roi = settings.getRoi();
        Rect region = new Rect(roi.getX(), roi.getY(), roi.getWidth(), roi.getHeight());
        Mat roiMath = new Mat(snapshot, region);

        // Blur and convert to HSV
        Imgproc.GaussianBlur(roiMath, roiMath, new Size(3, 3), 0);
        Imgproc.cvtColor(roiMath, roiMath, Imgproc.COLOR_BGR2HSV);

        // Filter lower and upper color.
        Core.inRange(roiMath, buildScalar(settings.getLowerHSVMin()), buildScalar(settings.getLowerHSVMax()), lower);
        Core.inRange(roiMath, buildScalar(settings.getUpperHSVMin()), buildScalar(settings.getUpperHSVMax()), upper);
        Core.addWeighted(lower, 1.0, upper, 1.0, 0.0, roiMath);

        Imgproc.findContours(roiMath, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (contourInRange(rect)) {
//                Imgproc.rectangle(snapshot, new Point(rect.x + roi.getX(), rect.y + roi.getY()), new Point(rect.x + rect
//                                          .width + roi.getWidth(), rect.y + rect.height + roi.getHeight()),
//                                  new Scalar(103, 255, 255));
                inRange.add(rect);
            }
        }
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

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    class TrafficLightLookoutResult extends LookoutResult {

        private final List<Rect> rects;

        public TrafficLightLookoutResult(AutonomousStatus status, byte[] imgBytes, List<Rect> rects) {
            super(status, imgBytes);
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
