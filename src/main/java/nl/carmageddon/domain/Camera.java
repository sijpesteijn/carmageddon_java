package nl.carmageddon.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Camera {
    private static Logger logger = LoggerFactory.getLogger(Camera.class);
    @JsonIgnore
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
            camera = null;
        } else {
            camera.set(CV_CAP_PROP_FRAME_WIDTH, 640);
            camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
        }
        return camera;
    }

    public Mat makeSnapshot() {
        Mat snapshot = new Mat();
        VideoCapture camera = getCamera();
        camera.read(snapshot);
//        camera.release();
        return snapshot;
    }

    public byte[] makeSnapshotInByteArray() {
        Mat snapshot = makeSnapshot();
        byte[] bytes = getImageBytes(snapshot);
        return bytes;
    }

    public byte[] getImageBytes(Mat mat) {
        // TODO betere manier om image naar byte array om te zetten.
        String fileName = System.getProperty("java.io.tmpdir") + "snapshot_" + System.currentTimeMillis() + ".jpg";
        Imgcodecs.imwrite(fileName, mat);

        byte[] bytes = null;
        File fi = new File(fileName);
        try {
            bytes = Files.readAllBytes(fi.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public boolean isOpened() {
        return camera != null;
    }
}
