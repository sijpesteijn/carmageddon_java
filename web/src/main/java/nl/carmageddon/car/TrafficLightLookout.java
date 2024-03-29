package nl.carmageddon.car;

import nl.carmageddon.domain.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Observable;

import static org.opencv.core.CvType.CV_8UC1;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class TrafficLightLookout extends Observable implements Lookout<TrafficLightView> {
    private static final Logger logger = LoggerFactory.getLogger(TrafficLightLookout.class);
    private boolean run = false;
    private TrafficLightSettings settings;
    private LookoutResult result;
    private Camera camera;

    @Inject
    public TrafficLightLookout(Camera camera) {
        this.camera = camera;
    }

    @Override
    public LookoutResult start() {
        run = true;
        while (run) {
            Mat snapshot = this.camera.makeSnapshot();
            TrafficLightView view = getCurrentView(snapshot);
            addViewToMat(snapshot, view);
            notifyClients(new TrafficLightLookoutResult(AutonomousStatus.TRAFFIC_LIGHT_ROI_SET, snapshot, view));
            result = waitForGo(view);
            if (result.getStatus() == AutonomousStatus.TRAFFIC_LIGHT_OFF) {
                run = false;
            }
        }
        return result;
    }

    public TrafficLightView getCurrentView(Mat snapshot) {
        TrafficLightView view = new TrafficLightView();
        try {

            // Get just a region to look at
            ROI roi = settings.getRoi();
            Rect region = new Rect(roi.getX(), roi.getY(), roi.getWidth(), roi.getHeight());
            Mat roiMat = new Mat(snapshot.clone(), region);

            view.setRoiMat(roiMat);
        } catch(Exception e) {
            logger.error("Get current view: " + e.getMessage());
        }
        return view;
    }

    private LookoutResult waitForGo(TrafficLightView viewWithLightOn) {
        LookoutResult result = null;
        try {
        boolean lightsOff = false;
        while (run && !lightsOff) {
            Mat snapshot = this.camera.makeSnapshot();
            TrafficLightView currentTrafficLightArea = getCurrentView(snapshot);
            addViewToMat(snapshot, currentTrafficLightArea);
            if (pixelDifferencePercentage(viewWithLightOn, currentTrafficLightArea) < 40) {
                result = new LookoutResult(AutonomousStatus.TRAFFIC_LIGHT_ON, snapshot);
            } else {
                result = new LookoutResult(AutonomousStatus.TRAFFIC_LIGHT_OFF, snapshot);
                lightsOff = true;
            }
            notifyClients(result);
        }
        } catch(Exception e) {
            logger.error("Wait for go: " + e.getMessage());
        }
        return result;
    }

    private int pixelDifferencePercentage(TrafficLightView viewWithLightOn, TrafficLightView
            currentTrafficLightArea) {
        try {
        int count = 0;
        Mat lightOn = viewWithLightOn.getRoiMat();
        Mat current = currentTrafficLightArea.getRoiMat();
        Mat result = new Mat(lightOn.rows(), lightOn.cols(), CV_8UC1);
        double threshold = 30;
        Core.absdiff(lightOn, current, result);
        Mat bw = new Mat(result.rows(), result.cols(), CV_8UC1);
        for (int j = 0; j < result.rows(); ++j) {
            for (int i = 0; i < result.cols(); ++i) {
                double[] pix = result.get(j, i);
                double dist = (pix[0] * pix[0] + pix[1] * pix[1] + pix[2] * pix[2]);
                dist = Math.sqrt(dist);
                if (dist > threshold) {
                    bw.put(j, i, dist);
                    count++;
                }

            }
        }
        return (int) (count*100/result.total());

        } catch(Exception e) {
            logger.error("Pixel difference percentage: " + e.getMessage());
        }
        return 0;
    }

    public void stop() {
        run = true;
    }

    public void setTrafficLightSettings(TrafficLightSettings settings) {
        this.settings = settings;
    }

    public void addViewToMat(Mat snapshot, TrafficLightView view) {
        // We kunnen niks toevoegen, we kijken naar het verschil in aantal pixels.
    }

    class TrafficLightLookoutResult extends LookoutResult {
        private TrafficLightView viewWithLightOn;

        public TrafficLightLookoutResult(AutonomousStatus status, Mat snapshot, TrafficLightView viewWithLightOn) {
            super(status, snapshot);
            this.viewWithLightOn = viewWithLightOn;
        }

        public TrafficLightView getViewWithLightOn() {
            return viewWithLightOn;
        }

        public void setViewWithLightOn(TrafficLightView viewWithLightOn) {
            this.viewWithLightOn = viewWithLightOn;
        }
    }

    private void notifyClients(LookoutResult event) {
        setChanged();
        notifyObservers(event);
    }

}
