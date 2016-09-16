package nl.carmageddon.domain;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Camera {
    private static Logger logger = LoggerFactory.getLogger(Camera.class);
    private VideoCapture camera;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VideoCapture getCamera() {
        if (camera != null && camera.isOpened()) {
            return camera;
        }
        camera = new VideoCapture(id);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!camera.isOpened()) {
            logger.error("No webcam with id " + id + " found!");
        }
        camera.set(CV_CAP_PROP_FRAME_WIDTH, 640);
        camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
        return camera;
    }

    public Mat mageSnapshot() {
        Mat snapshot = new Mat();
        getCamera().read(snapshot);
        return snapshot;
    }

    public byte[] makeSnapshot() {
        Mat snapshot = mageSnapshot();
        byte[] bytes = new byte[(int) (snapshot.total() * snapshot.channels())];
        snapshot.get(0, 0, bytes);
        return bytes;
    }
}
