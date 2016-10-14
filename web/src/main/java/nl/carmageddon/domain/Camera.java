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
//    private final ScheduledExecutorService frameGrabberTimer;

    @JsonIgnore
    private VideoCapture camera;
    private int id;
    private String url;
    private boolean showVideo;
//    private Mat snapshot;

    @Inject
    public Camera(CarmageddonSettings settings) {
        this.showVideo = settings.isShowVideo();
        this.dimension = settings.getCameraDimension();
        url = "http://" + settings.getBeagleBoneSettings().getBeagleBoneIp() + ":"
                + settings.getBeagleBoneSettings().getStreamPort() + "/?action=stream";
//        this.frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
//        this.frameGrabberTimer.schedule(frameGrabber, 5, TimeUnit.MILLISECONDS);
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

//    private Runnable frameGrabber = () -> {
//        while(this.getCamera() != null) {
//            camera.grab();
//            camera.retrieve(snapshot);
//        }
//    };

    public VideoCapture getCamera() {
        if (camera != null && camera.isOpened()) {
            return camera;
        }
        camera = new VideoCapture(0);
        try {
            Thread.sleep(200);
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

        Mat snapshot = new Mat();
        VideoCapture camera = getCamera();
        camera.grab();
        camera.retrieve(snapshot);
        return snapshot;
//        Mat img = Imgcodecs.imread("/Users/gijs/programming/java/carmageddon/src/main/resources/ws4.jpg");
//        return img;
//        Mat mat = new Mat(320, 240, CvType.CV_8UC3);
//        try {
//            URL url = new URL("http://192.168.0.100/?action=snapshot");
//            mat.put(0, 0, IOUtils.toByteArray(url.openStream()));
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return mat;
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
