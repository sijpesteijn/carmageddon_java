package nl.carmageddon.controller;

import nl.carmageddon.domain.Car;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
    private File video;
    final int chunk_size = 1024 * 1024; // 1MB chunks
    private Car car;

    @Inject
    public WebCamController(Car car) {
        this.car = car;
    }

    @POST
    @Path(value = "/use/{id}")
    public void useWebCam(@PathParam("id") int id) {
        this.car.getCamera().setId(id);
    }

    @GET
    @Path(value = "/snapshot")
    @Produces(MediaType.TEXT_HTML)
    public String makeSnapShot(@Context HttpServletResponse response) throws IOException {
        Mat frame = car.getCamera().makeSnapshot();
        MatOfByte ofByte = new MatOfByte();
        Imgcodecs.imencode(".png", frame, ofByte);

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(ofByte.toArray());
        responseOutputStream.flush();
        responseOutputStream.close();
        return "";
    }

    @GET
    @Path(value = "/stream")
    @Produces("video/mp4")
    public Response stream(@HeaderParam("range") String range) throws IOException {
        URL url = this.getClass().getResource("/output.mp4");
        video = new File(url.getFile());

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
