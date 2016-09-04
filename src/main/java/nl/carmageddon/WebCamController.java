package nl.carmageddon;

import com.github.sarxos.webcam.Webcam;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@Path("webcam")
public class WebCamController {
    private static Logger logger = LoggerFactory.getLogger(CarController.class);
    private Webcam webcam;

    @Inject
    public WebCamController(Configuration configuration) throws IOException {
        webcam = Webcam.getDefault();
        String webcamName = configuration.getString("webcam");
        if (!webcamName.isEmpty()) {
            List<Webcam> webcams = Webcam.getWebcams();
            webcams.forEach(webcam -> {
                if (webcam.getName().startsWith(webcamName)) {
                    this.webcam = webcam;
                }
            });
        }
        logger.debug("Using webcam " + webcam.getName());
        webcam.open();

        // Testing: Capture webcam and save.
        ImageIO.write(webcam.getImage(), "PNG", new File("capture.png"));
    }

    @POST
    @Path(value = "/data/{data}")
    public void setAngle(@PathParam("data") String data) {
        System.out.println("post data: " + data);
    }

    @PUT
    @Path(value = "/data/{data}")
    public void setData(@PathParam("data") String data) {
        System.out.println("put data: " + data);
    }

}
