package nl.carmageddon.service;

import nl.carmageddon.domain.*;
import nl.carmageddon.domain.Box;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
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
    private static final Logger log = LoggerFactory.getLogger(TrafficLightLookout.class);
    private  byte[] bytes;
    private boolean stop = true;
    private Car car;
    private HSV upperHSVMax;
    private HSV upperHSVMin;
    private HSV lowerHSVMax;
    private HSV lowerHSVMin;
    private LookoutResult result;
    private ViewType viewType;
    private Box minBoxBox;

    private Box maxBoxBox;

    @Inject
    public TrafficLightLookout(Car car) {
        this.car = car;
    }

    @Override
    public LookoutResult start() {
        VideoCapture camera = this.car.getCamera().getCamera();
        LookoutResult result = new LookoutResult(AutonomousStatus.NO_TRAFFIC_LIGHT, null);
        if (camera == null || !camera.isOpened()) {
            result = new LookoutResult(AutonomousStatus.NO_CAMERA, null);
            setChanged();
            notifyObservers(result);
            return result;
        }
        stop = false;
        while(!stop) {
            result = lookForTrafficLight();
            setChanged();
            if (result.getStatus() == AutonomousStatus.TRAFFIC_LIGHT_FOUND) {
                notifyObservers(result);
                result = waitForGo((TrafficLightLookoutResult) result);
                if (result.getStatus() == AutonomousStatus.TRAFFICLIGHT_OFF) {
                    stop = true;
                }
            }
            else {
                notifyObservers(result);
                return result;
            }
        }
        return result;
    }

    private LookoutResult waitForGo(TrafficLightLookoutResult trafficLightLookoutResult) {
        boolean lightsOff = false;
        int index = 0;
        List<MatOfPoint> trafficLightLookoutResultShapes = trafficLightLookoutResult.getShapes();
        result = new LookoutResult(AutonomousStatus.TRAFFICLIGHT_ON, bytes);
        while(!stop && !lightsOff) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<MatOfPoint> shapes = getTrafficLight(index++);
            if (!trafficLightOff(trafficLightLookoutResultShapes, shapes)) {
                result = new LookoutResult(AutonomousStatus.TRAFFICLIGHT_ON, bytes);
            } else {
                result = new LookoutResult(AutonomousStatus.TRAFFICLIGHT_OFF, bytes);
                lightsOff = true;
            }
            setChanged();
            notifyObservers(result);
        }
        return result;
    }

    private boolean trafficLightOff(List<MatOfPoint> trafficLightShapes, List<MatOfPoint> shapes) {
        if (shapes.size() < trafficLightShapes.size())
            return true;
        return false;
    }

    private LookoutResult lookForTrafficLight() {
        boolean found = false;
        while(!stop && !found) {
            List<MatOfPoint> shapes = getTrafficLight(0);
            if (shapes.size() == 1) {
                result = new TrafficLightLookoutResult(AutonomousStatus.TRAFFIC_LIGHT_FOUND, bytes, shapes);
                found = true;
            }
            else {
                result = new LookoutResult(AutonomousStatus.NO_TRAFFIC_LIGHT, bytes);
            }
        }
        return result;
    }

    private List<MatOfPoint> getTrafficLight(int index) {
        String filename = "/Users/gijs/programming/java/carmageddon/src/main/resources/peer.jpg";
        if (index > 20) {
            filename = "/Users/gijs/programming/java/carmageddon/src/main/resources/peer2.jpg";
        }
        Mat frame= Imgcodecs
                .imread(filename, Imgcodecs
                        .CV_LOAD_IMAGE_COLOR);
        Mat original = frame.clone();
        Imgproc.GaussianBlur(frame, frame, new Size(3, 3), 0);
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
        if (this.viewType == ViewType.hue)
            bytes = this.car.getCamera().getImageBytes(frame);
        Core.inRange(frame, buildScalar(lowerHSVMin), buildScalar(lowerHSVMax), frame);
        if (this.viewType == ViewType.baw)
            bytes = this.car.getCamera().getImageBytes(frame);
        Mat frameContours = frame.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        List<MatOfPoint> inRange = new ArrayList<MatOfPoint>();
        Imgproc.findContours(frameContours, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i=0; i< contours.size();i++){
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (contourInRange(rect)){
                Imgproc.rectangle(original, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),
                                  new Scalar(103,255,255));
                inRange.add(contours.get(i));
            }
        }
        if (this.viewType == ViewType.result)
            bytes = this.car.getCamera().getImageBytes(original);

        return inRange;
    }

    private boolean contourInRange(Rect rect) {
        boolean inRange = true;
        int height = this.minBoxBox.getHeight();
        int width  = this.minBoxBox.getWidth();
        if (height != -1 && rect.height < height) {
            inRange &= false;
        }
        if (width != -1 && rect.width < width) {
            inRange &= false;
        }

        height = this.maxBoxBox.getHeight();
        width = this.maxBoxBox.getWidth();
        if (height != -1 && rect.height > height) {
            inRange &= false;
        }
        if (width != -1 && rect.width > width) {
            inRange &= false;
        }

        return inRange;
    }

    public void stop() {
        stop = true;
    }

    @Override
    public LookoutResult getStatus() {
        return result;
    }

    private Scalar buildScalar(HSV HSV) {
        return new Scalar(HSV.getHue(), HSV.getSaturation(), HSV.getBrightness());
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

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public void setMinBoxBox(Box minBoxBox) {
        this.minBoxBox = minBoxBox;
    }

    public void setMaxBoxBox(Box maxBoxBox) {
        this.maxBoxBox = maxBoxBox;
    }

    class TrafficLightLookoutResult extends LookoutResult {

        private final List<MatOfPoint> shapes;

        public TrafficLightLookoutResult(AutonomousStatus status, byte[] imgBytes, List<MatOfPoint> shapes) {
            super(status, imgBytes);
            this.shapes = shapes;
        }

        public List<MatOfPoint> getShapes() {
            return shapes;
        }
    }
}
