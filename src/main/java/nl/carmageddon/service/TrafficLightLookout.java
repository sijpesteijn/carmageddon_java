package nl.carmageddon.service;

import nl.carmageddon.domain.AutonomousStatus;
import nl.carmageddon.domain.Car;
import nl.carmageddon.domain.HSV;
import nl.carmageddon.domain.LookoutResult;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class TrafficLightLookout extends Observable implements Lookout {
    private static final Logger log = LoggerFactory.getLogger(TrafficLightLookout.class);
    private long timeout = 10000; // 10 sec
    private boolean stop = true;
    private Car car;
    private HSV upperHSVMax;
    private HSV upperHSVMin;
    private HSV lowerHSVMax;
    private HSV lowerHSVMin;
    private LookoutResult result;

    @Inject
    public TrafficLightLookout(Car car) {
        this.car = car;
    }

    @Override
    public LookoutResult start() {
        VideoCapture camera = this.car.getCamera().getCamera();
        LookoutResult result;
        if (camera == null || !camera.isOpened()) {
            result = new LookoutResult(AutonomousStatus.NO_CAMERA, null);
            setChanged();
            notifyObservers(result);
            return result;
        }
        stop = false;
        result = lookForTrafficLight();
        setChanged();
        if (result.getStatus() == AutonomousStatus.TRAFFIC_LIGHT_FOUND) {
            notifyObservers(result);
            return waitForGo();
        }
        else {
            notifyObservers(result);
            return result;
        }
    }

    // TODO wachten tot rode circle weg is.
    private LookoutResult waitForGo() {
        LookoutResult result = new LookoutResult(AutonomousStatus.RACE_START, this.car.getCamera().makeSnapshotInByteArray());
        setChanged();
        notifyObservers(result);
        return result;
    }

    // TODO loopen met een echt webcam beeld tot stoplicht gevonden of timeout.
    private LookoutResult lookForTrafficLight() {
        boolean found = false;
        while(!stop && !found) {
//        Mat frame= Imgcodecs
//                .imread("/Users/gijs/programming/java/carmageddon/src/main/resources/circles.jpg", Imgcodecs
//                        .CV_LOAD_IMAGE_COLOR);
            Mat frame = this.car.getCamera().makeSnapshot();
            Mat original = frame.clone();
            // Filter noise
            Imgproc.medianBlur(frame, frame, 3);
            frame = onlyRedObjects(frame);
            Imgproc.GaussianBlur(frame, frame, new Size(9, 9), 2, 2);
            Mat circles = new Mat();
            Imgproc.HoughCircles(frame, circles, Imgproc.CV_HOUGH_GRADIENT, 1, frame.rows() / 8, 10, 22, 0, 0);
            for (int i = 0; i < circles.cols(); i++) {
                Point center = new Point(circles.get(0, i)[0], circles.get(0, i)[1]);
                int radius = (int) Math.round(circles.get(0, i)[2]);
                Imgproc.circle(original, center, radius, new Scalar(0, 255, 0), 5);
            }
            byte[] bytes = this.car.getCamera().getImageBytes(original);
            if (circles.total() == 1) { // TODO dit is waarschijnlijk juist fout.
                result = new LookoutResult(AutonomousStatus.TRAFFIC_LIGHT_FOUND, bytes);
                found = true;
            }
            else {
                result = new LookoutResult(AutonomousStatus.NO_TRAFFIC_LIGHT, bytes);
            }
        }
        return result;
    }

    public void stop() {
        stop = true;
    }

    @Override
    public LookoutResult getStatus() {
        return result;
    }

    private Mat onlyRedObjects(Mat frame) {
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
        Imgcodecs.imwrite("hsv.jpg", frame);
        Scalar minValues = buildScalar(lowerHSVMin);
        Scalar maxValues = buildScalar(lowerHSVMax);
        Mat lower = new Mat();
        Core.inRange(frame, minValues, maxValues, lower);
        minValues = buildScalar(upperHSVMin);
        maxValues = buildScalar(upperHSVMax);
        Mat upper = new Mat();
        Core.inRange(frame, minValues, maxValues, upper);
        Core.addWeighted(lower, 1.0, upper, 1.0, 0.0, frame);
        Imgcodecs.imwrite("red.jpg", frame);
        return frame;
    }

    private Scalar buildScalar(HSV HSV) {
        return new Scalar(HSV.getHue(), HSV.getSaturation(), HSV.getValue());
    }

    public void setUpperHSVMax(HSV upperHSVMax) {
        this.upperHSVMax = upperHSVMax;
    }

    public void setUpperHSVMin(HSV upperHSVMin) {
        this.upperHSVMin = upperHSVMin;
    }

    public void setLowerHSVMax(HSV lowerHSVMax) {
        this.lowerHSVMax = lowerHSVMax;
    }

    public void setLowerHSVMin(HSV lowerHSVMin) {
        this.lowerHSVMin = lowerHSVMin;
    }
}
