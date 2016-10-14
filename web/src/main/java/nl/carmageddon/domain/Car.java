package nl.carmageddon.domain;

import com.google.inject.Inject;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Car extends Observable {
    private static final Logger log = LoggerFactory.getLogger(Car.class);
    private Steer steer = new Steer();
    private Engine engine = new Engine();
    private Mode mode = Mode.disabled;
    private boolean connected = false;
    private final ScheduledExecutorService carStatusTimer = Executors.newSingleThreadScheduledExecutor();
    private ObjectMapper mapper = new ObjectMapper();
    private String responseLine;

    public Car() {
    }

    @Inject
    public Car(CarmageddonSettings settings) throws IOException {
        Socket socket = new Socket(settings.getBeagleBoneSettings().getBeagleBoneIp(),
                                   settings.getBeagleBoneSettings().getCarStatusPort());
        carStatusTimer.schedule(() -> {
            try {
                DataInputStream is = new DataInputStream(socket.getInputStream());
                while(true) {
                    boolean ready = false;
                    while (!ready && (responseLine = is.readLine()) != null) {
                        Map map = mapper.readValue(responseLine, Map.class);
                        this.engine.setThrottle((Integer) ((Map)map.get("engine")).get("throttle"));
                        this.engine.setThrottleLimit((Integer) ((Map)map.get("engine")).get("throttleLimit"));
                        this.steer.setAngle((Integer) ((Map)map.get("steer")).get("angle"));
                        this.setMode(Mode.valueOf((String) map.get("mode")));
                        this.setConnected((Boolean) map.get("connected"));
                        ready = true;
                    }
                }
            } catch(Exception e) {
                log.error("Problem getting car status " + e.getMessage());
            }
        }, 700, TimeUnit.MILLISECONDS);

    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getAngle() {
        return this.steer.getAngle();
    }

    public void setAngle(int angle) {
        this.steer.setAngle(angle);
    }

    public int getThrottle() {
        return this.engine.getThrottle();
    }

    public void setThrottle(int throttle) {
        this.engine.setThrottle(throttle);
    }

    public int getThrottleLimit() {
        return this.engine.getThrottleLimit();
    }

    public void setThrottleLimit(int throttleLimit) {
        this.engine.setThrottleLimit(throttleLimit);
    }

    public boolean isConnected() {
        return connected;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (this.mode == Mode.disabled) {
            this.engine.setThrottle(0);
            this.steer.setAngle(0);
        }
        setChanged();
        notifyObservers();
    }
}
