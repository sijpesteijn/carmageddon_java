package nl.carmageddon.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Gijs Sijpesteijn
 */
public class LookoutResult {
    private final AutonomousStatus status;
    @JsonIgnore
    private final byte[] imgBytes;

    public LookoutResult(AutonomousStatus status, byte[] imgBytes) {
        this.status = status;
        this.imgBytes = imgBytes;
    }

    public AutonomousStatus getStatus() {
        return status;
    }

    public byte[] getImgBytes() {
        return imgBytes;
    }

    public boolean sucess() {
        return status == AutonomousStatus.TRAFFIC_LIGHT_FOUND || status == AutonomousStatus.CAR_READY_TO_RACE ||
               status == AutonomousStatus.RACE_START || status == AutonomousStatus.RACE_FINISHED;
    }
}
