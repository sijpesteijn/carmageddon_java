package nl.carmageddon.service;

import nl.carmageddon.domain.AutonomousStatus;
import nl.carmageddon.domain.Car;
import nl.carmageddon.domain.LookoutResult;
import nl.carmageddon.domain.HSV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class TrafficLightLookout extends Observable implements Lookout {
    private static final Logger log = LoggerFactory.getLogger(TrafficLightLookout.class);
    private long timeout = 10000; // 10 sec
    private Car car;
    private HSV upperHSVMax;
    private HSV upperHSVMin;
    private HSV lowerHSVMax;
    private HSV lowerHSVMin;

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
        result = trafficLightFound(camera);
        setChanged();
        if (result.getStatus() == AutonomousStatus.TRAFFIC_LIGHT_FOUND) {
            notifyObservers(result);
            return waitForGo(camera);
        }
        else {
            notifyObservers(result);
            return result;
        }
    }

    // TODO wachten tot rode circle weg is.
    private LookoutResult waitForGo(VideoCapture camera) {
        LookoutResult result = new LookoutResult(AutonomousStatus.RACE_START, null);
        setChanged();
        notifyObservers(result);
        return result;
    }

    // TODO loopen met een echt webcam beeld tot stoplicht gevonden of timeout.
    private LookoutResult trafficLightFound(VideoCapture camera) {
//        Mat frame= Imgcodecs
//                .imread("/Users/gijs/programming/java/carmageddon/src/main/resources/circles.jpg", Imgcodecs
//                        .CV_LOAD_IMAGE_COLOR);
        Mat frame = new Mat();
        camera.read(frame);
        Mat original = frame.clone();
        // Filter noise
        Imgproc.medianBlur(frame, frame, 3);
        frame = onlyRedCircles(frame);
        Mat circles = new Mat();
        Imgproc.HoughCircles(frame, circles, Imgproc.CV_HOUGH_GRADIENT, 1, frame.rows()/8, 10, 20, 0, 0);
        for( int i = 0; i < circles.cols(); i++ ) {
            Point center = new Point(circles.get(0, i)[0], circles.get(0, i)[1]);
            int radius = (int) Math.round(circles.get(0, i)[2]);
            Imgproc.circle(original, center, radius, new Scalar(0, 255, 0), 5);
        }
        Imgcodecs.imwrite("capture.jpg", original);

        // TODO betere manier om image naar byte array om te zetten.
        byte[] bytes = new byte[original.cols() * original.rows() * original.channels()];
        original.get(0, 0, bytes);
        File fi = new File("capture.jpg");
        try {
            bytes = Files.readAllBytes(fi.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (circles.total() > 1) { // TODO dit is waarschijnlijk juist fout.
            return new LookoutResult(AutonomousStatus.TRAFFIC_LIGHT_FOUND, bytes);
        } else {
            return new LookoutResult(AutonomousStatus.NO_TRAFFIC_LIGHT, bytes);
        }
    }

    public void stop() {

    }

    private Mat onlyRedCircles(Mat frame) {
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
        Imgcodecs.imwrite("hsv.jpg", frame);
        Scalar minValues = getScalar(lowerHSVMin); // new Scalar(10, 100, 100);
        Scalar maxValues = getScalar(lowerHSVMax); //new Scalar(12, 255, 255);
        Mat lower = new Mat();
        Core.inRange(frame, minValues, maxValues, lower);
        minValues = getScalar(upperHSVMin); // new Scalar(0, 100, 100);
        maxValues = getScalar(upperHSVMax); // new Scalar(9, 255, 255);
        Mat upper = new Mat();
        Core.inRange(frame, minValues, maxValues, upper);
        Core.addWeighted(lower, 1.0, upper, 1.0, 0.0, frame);
        Imgcodecs.imwrite("red.jpg", frame);
        return frame;
    }

    private Scalar getScalar(HSV HSV) {
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
