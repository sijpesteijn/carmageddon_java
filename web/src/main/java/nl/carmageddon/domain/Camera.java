package nl.carmageddon.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Camera {
    private static Logger logger = LoggerFactory.getLogger(Camera.class);
    private final Dimension dimension;
    @JsonIgnore
    private VideoCapture camera;
    private int id;
    private String url;

    @Inject
    public Camera(CarmageddonSettings settings) {
        this.dimension = settings.getCameraDimension();
        url = "http://" + settings.getBeagleBoneSettings().getBeagleBoneIp() + ":"
                + settings.getBeagleBoneSettings().getStreamPort() + "/?action=stream";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        if (camera != null) {
            camera.release();
        }
    }

    public VideoCapture getCamera() {
        if (camera != null && camera.isOpened()) {
            return camera;
        }
        camera = new VideoCapture(url);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!camera.isOpened()) {
            logger.error("No webcam with id " + url + " found!");
            camera = null;
        } else {
            camera.set(CV_CAP_PROP_FRAME_WIDTH, dimension.getWidth());
            camera.set(CV_CAP_PROP_FRAME_HEIGHT, dimension.getHeight());
        }
        return camera;
    }

    public Mat makeSnapshot() {
//        return imread("/Users/gijs/programming/java/carmageddon/web/src/main/resources/ws1.jpg");
        Mat snapshot = new Mat();
        VideoCapture camera = getCamera();
        camera.grab();
        camera.retrieve(snapshot);
        return snapshot;
    }

    public boolean isOpened() {
        return camera != null;
    }

    public Dimension getDimension() {
        return dimension;
    }
}
