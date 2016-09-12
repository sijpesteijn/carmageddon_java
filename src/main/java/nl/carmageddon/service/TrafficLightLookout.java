package nl.carmageddon.service;

import nl.carmageddon.domain.AutonoumousEvent;
import nl.carmageddon.domain.AutonoumousEventType;
import nl.carmageddon.domain.Car;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class TrafficLightLookout extends Observable {
    private static final Logger log = LoggerFactory.getLogger(TrafficLightLookout.class);
    private ScheduledExecutorService timer;
    private boolean found = false;
    private Car car;

    @Inject
    public TrafficLightLookout(Car car) {
        this.car = car;
    }

    public void lookForTrafficLight() {

        VideoCapture camera = this.car.getCamera().getCamera();

        if (camera == null || !camera.isOpened()) {
            setChanged();
            notifyObservers("No camera found.");
            return;
        }
        // grab a frame every 33 ms (30 frames/sec)
        Runnable frameGrabber = () -> {
            Mat frame = new Mat();
            camera.read(frame);
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
            Imgcodecs.imwrite(System.getProperty("java.io.tmpdir") + "/traffic.jpeg", frame);
            ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
            try {
                BufferedImage image = ImageIO.read(new File(System.getProperty("java.io.tmpdir") + "/snapshot.jpeg"));
                ImageIO.write(image, "jpeg", jpegOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] imgByte = jpegOutputStream.toByteArray();

            setChanged();
            if (found) {
                notifyObservers(new AutonoumousEvent(AutonoumousEventType.NO_TRAFFIC_LIGHT, imgByte, car.isConnected()));
            } else {
                notifyObservers(new AutonoumousEvent(AutonoumousEventType.TRAFFIC_LIGHT_FOUND, imgByte, car.isConnected()));
            }
            found = !found;
        };

        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

    }

    public void cancel() {
        this.timer.shutdown();
    }
}
