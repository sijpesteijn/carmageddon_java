package nl.carmageddon;

import org.apache.commons.configuration.Configuration;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@Path("webcam")
public class WebCamController {
    private static Logger logger = LoggerFactory.getLogger(CarController.class);
    private VideoCapture camera;
    private File video;
    final int chunk_size = 1024 * 1024; // 1MB chunks

    @Inject
    public WebCamController(Configuration configuration) throws IOException {
        if (!System.getProperty("os.arch").contains("arm")) {
            String libPath = System.getProperty("java.library.path");
            String opencvPath = "./main/resources/";
            System.setProperty("java.library.path", libPath + ":" + opencvPath);
        }
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        openCamera(0);
        URL url = this.getClass().getResource("/output.mp4");
        video = new File(url.getFile());
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

    @POST
    @Path(value = "/capture")
    public void startCapture() {

    }

    @GET
    @Path(value = "/snapshot")
    @Produces(MediaType.TEXT_HTML)
    public void makeSnapShot(@Context HttpServletResponse response) throws IOException {
        int H_MIN = 0;
        int H_MAX = 256;
        int S_MIN = 0;
        int S_MAX = 256;
        int V_MIN = 0;
        int V_MAX = 256;
        Mat frame = new Mat();
        camera.read(frame);

        Mat hsv = new Mat();
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        Mat threshold = new Mat();
        Core.inRange(hsv, new Scalar(H_MIN, S_MIN, V_MIN,1), new Scalar(H_MAX,S_MAX,V_MAX,1), threshold);

        //      Imgproc.GaussianBlur(threshold, threshold,new Size(3,3), 1.5);

        Mat circles = new Mat();

        Imgproc.HoughCircles(threshold, circles, Imgproc.CV_HOUGH_GRADIENT, 2, threshold.height() / 4, 100, 50, 10, 400);

//            /*Drawing image to frame*/
//        MatOfByte imgBytes = new MatOfByte();
//
//        Highgui.imencode(".png", threshold, imgBytes);
//
//        byte[] byteData = imgBytes.toArray();
//
//        InputStream in = new ByteArrayInputStream(byteData);
//
//        display = scale(ImageIO.read(in), this.getWidth(), this.getHeight());
//
//        for (int i = 0; i < circles.cols(); i++){
//
//            double[] vecCircle = circles.get(0, i);
//
//            int x = (int) vecCircle[0];
//            int y = (int) vecCircle[1];
//            int r = (int) vecCircle[2];
//
//            drawCircle(display.createGraphics(), x, y, r);
//
//        }


        Imgcodecs.imwrite(System.getProperty("java.io.tmpdir") + "/snapshot.jpeg", frame);

        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        BufferedImage image = ImageIO.read(new File(System.getProperty("java.io.tmpdir") + "/snapshot.jpeg"));
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

    @GET
    @Path(value = "/stream")
    @Produces("video/mp4")
    public Response stream(@HeaderParam("range") String range) throws IOException {
        if (range == null) {
            StreamingOutput streamer = output -> {

                final FileChannel inputChannel = new FileInputStream(video).getChannel();
                final WritableByteChannel outputChannel = Channels.newChannel(output);
                try {
                    inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                } finally {
                    // closing the channels
                    inputChannel.close();
                    outputChannel.close();
                }
            };
            return Response.ok(streamer).status(200).header(HttpHeaders.CONTENT_LENGTH, video.length()).build();
        }
        String[] ranges = range.split("-");
        final int from = Integer.parseInt(ranges[0]);
        /**
         * Chunk media if the range upper bound is unspecified. Chrome sends "bytes=0-"
         */
        int to = chunk_size + from;
        if (to >= video.length()) {
            to = (int) (video.length() - 1);
        }
        if (ranges.length == 2) {
            to = Integer.parseInt(ranges[1]);
        }

        final String responseRange = String.format("bytes %d-%d/%d", from, to, video.length());
        final RandomAccessFile raf = new RandomAccessFile(video, "r");
        raf.seek(from);

        final int len = to - from + 1;
        final MediaStreamer streamer = new MediaStreamer(len, raf);
        Response.ResponseBuilder res = Response.ok(streamer).status(206)
                .header("Accept-Ranges", "bytes")
                .header("Content-Range", responseRange)
                .header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
                .header(HttpHeaders.LAST_MODIFIED, new Date(video.lastModified()));
        return res.build();
    }

    @PUT
    @Path(value = "/data/{data}")
    public void setData(@PathParam("data") String data) {
        System.out.println("put data: " + data);
    }

    public class MediaStreamer implements StreamingOutput {

        private int length;
        private RandomAccessFile raf;
        final byte[] buf = new byte[4096];

        public MediaStreamer(int length, RandomAccessFile raf) {
            this.length = length;
            this.raf = raf;
        }

        @Override
        public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            try {
                while( length != 0) {
                    int read = raf.read(buf, 0, buf.length > length ? length : buf.length);
                    outputStream.write(buf, 0, read);
                    length -= read;
                }
            } finally {
                raf.close();
            }
        }

        public int getLenth() {
            return length;
        }
    }
}
