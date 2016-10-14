package nl.carmageddon.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.opencv.videoio.Videoio.*;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Camera {
    private static Logger logger = LoggerFactory.getLogger(Camera.class);

    private final Dimension dimension;

    private final ScheduledExecutorService frameGrabberTimer;

    @JsonIgnore
    private VideoCapture camera;
    private int id;
    private boolean showVideo;
    private Mat snapshot;

    @Inject
    public Camera(CarmageddonSettings settings) {
        this.showVideo = settings.isShowVideo();
        this.dimension = settings.getCameraDimension();
        this.frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
        this.frameGrabberTimer.schedule(frameGrabber, 2, TimeUnit.MILLISECONDS);
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

    private Runnable frameGrabber = () -> {
        while(this.getCamera() != null) {
            camera.grab();
            camera.retrieve(snapshot);
        }
    };

    public VideoCapture getCamera() {
        if (camera != null && camera.isOpened()) {
            return camera;
        }
        camera = new VideoCapture(id); //"http://192.168.88.200:8090/?action=stream");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!camera.isOpened()) {
            logger.error("No webcam with id " + id + " found!");
            camera = null;
        } else {
            camera.set(CV_CAP_PROP_FRAME_WIDTH, dimension.getWidth());
            camera.set(CV_CAP_PROP_FRAME_HEIGHT, dimension.getHeight());
            double v = camera.get(5);
            logger.debug("FPS " + v);
            camera.set(5, 10);
            v = camera.get(5);
            logger.debug("FPS " + v);
        }
        return camera;
    }

    public Mat makeSnapshot() {
//        Mat snapshot = new Mat();
//        VideoCapture camera = getCamera();
//
//        camera.grab();
//        camera.retrieve(snapshot);
//        Core.flip(snapshot, snapshot, 0);
        return snapshot;
//        Mat img = Imgcodecs.imread("/Users/gijs/programming/java/carmageddon/src/main/resources/ws4.jpg");
//        return img;
    }

//    public byte[] makeSnapshotInByteArray() {
//        Mat snapshot = makeSnapshot();
//        byte[] bytes = getImageBytes(snapshot);
//        return bytes;
//    }

//    public byte[] getImageBytes(Mat mat) {
//        byte[] bytes = new byte[(int) (mat.total() * mat.channels())];
//        mat.get(0,0,bytes);
//
//        byte[] bytes = null;
//        if(showVideo) {
//            // TODO betere manier om image naar byte array om te zetten.
//            String fileName = System.getProperty("java.io.tmpdir") + "/snapshot_" + System.currentTimeMillis() + ".jpg";
//            Imgcodecs.imwrite(fileName, mat);
//            File fi = new File(fileName);
//            try {
//                bytes = Files.readAllBytes(fi.toPath());
//                fi.delete();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return bytes;
//    }

    public boolean isOpened() {
        return camera != null;
    }

    public void setShowVideo(boolean showVideo) {
        this.showVideo = showVideo;
    }
}
