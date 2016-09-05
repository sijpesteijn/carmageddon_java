package nl.carmageddon;

import org.apache.commons.configuration.Configuration;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@Path("webcam")
public class WebCamController {
    private static Logger logger = LoggerFactory.getLogger(CarController.class);
    private VideoCapture camera;

    @Inject
    public WebCamController(Configuration configuration) throws IOException {
        String libPath = System.getProperty("java.library.path");
        String opencvPath = "./main/resources/";
        System.setProperty("java.library.path", libPath + ":" + opencvPath);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        openCamera(0);
    }

    @POST
    @Path(value = "/use/{id}")
    public String useWebCam(@PathParam("id") int id) {
        return String.valueOf(openCamera(id));
    }

    private boolean openCamera(int id) {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
        camera = new VideoCapture(id);
        if (!camera.isOpened()) {
            logger.error("No webcam with id " + id + " found!");
            return false;
        }
        return true;
    }

    @GET
    @Path(value = "/snapshot")
    @Produces(MediaType.TEXT_HTML)
    public void makeSnapShot(@Context HttpServletResponse response) throws IOException {
        Mat frame = new Mat();
        camera.read(frame);
        Imgcodecs.imwrite(System.getProperty("java.io.tmpdir") + "/snapshot.png", frame);

        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        BufferedImage image = ImageIO.read(new File(System.getProperty("java.io.tmpdir") + "/snapshot.png"));
        ImageIO.write(image, "jpeg", jpegOutputStream);

        byte[] imgByte = jpegOutputStream.toByteArray();

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(imgByte);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    @PUT
    @Path(value = "/data/{data}")
    public void setData(@PathParam("data") String data) {
        System.out.println("put data: " + data);
    }

}
