package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public enum AutonomousStatus {
    NO_CAMERA,
    READY_TO_RACE,
    NO_TRAFFIC_LIGHT,
    TRAFFIC_LIGHT_ROI_SET,
    TRAFFIC_LIGHT_ON,
    TRAFFIC_LIGHT_OFF,
    CAR_STOPPED,
    RACE_STOPPED,
    RACING,
    NO_ROAD,
    RACE_FINISHED
}
