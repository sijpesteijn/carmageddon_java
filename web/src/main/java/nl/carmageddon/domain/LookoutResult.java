package nl.carmageddon.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opencv.core.Mat;

/**
 * @author Gijs Sijpesteijn
 */
public class LookoutResult {
    private final AutonomousStatus status;

    @JsonIgnore
    private Mat snapshot;

//    private final byte[] imgBytes;

    public LookoutResult(AutonomousStatus status, Mat snapshot) {
        this.status = status;
//        this.imgBytes = imgBytes;
        this.snapshot = snapshot;
    }

    public AutonomousStatus getStatus() {
        return status;
    }

    public Mat getSnapshot() {
        return snapshot;
    }

//    public byte[] getImgBytes() {
//        return imgBytes;
//    }

}
