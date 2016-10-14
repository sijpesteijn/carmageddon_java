package nl.carmageddon.car.socket;

import com.google.inject.Inject;
import nl.carmageddon.car.domain.Car;
import nl.carmageddon.car.domain.CarInstuction;
import nl.carmageddon.car.domain.CarSettings;
import nl.carmageddon.car.domain.Mode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class CarControlSocket {
    private static Logger logger = LoggerFactory.getLogger(CarControlSocket.class);
    private ObjectMapper mapper = new ObjectMapper();
    private Car car;
    private ServerSocket socket;
    private final ScheduledExecutorService controlTimer = Executors.newSingleThreadScheduledExecutor();

    @Inject
    public CarControlSocket(CarSettings settings, Car car) throws IOException {
        this.car = car;
        this.socket = new ServerSocket(settings.getCarControlPort());
        this.controlTimer.schedule(socketListener, 100, TimeUnit.MILLISECONDS);
    }

    private Runnable socketListener = () -> {
        while(true) {
            try {
                logger.debug("Listening for car control connections.");
                Socket connection = socket.accept();
                logger.debug("Connection received from " + connection.getInetAddress().getHostName() + " : " + connection
                        .getPort());
                new CarControlHandler(connection, car).start();
            } catch (IOException e) {
                logger.error("Could not accept client connection.");
            }
        }
    };

    class CarControlHandler extends Thread {
        private Socket connection;
        private Car car;
        private String line;

        public CarControlHandler(Socket connection, Car car) {
            this.connection = connection;
            this.car = car;
        }

        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(connection.getInputStream());
                while ((line = dis.readLine()) != null) {
                    CarInstuction ci = mapper.readValue(line, CarInstuction.class);
                    logger.debug("Instruction: " + ci.getKey() + " " + ci.getValue());
                    if (ci.getKey().equals("mode")) {
                        this.car.setMode(Mode.valueOf((String) ci.getValue()));
                    }
                    else if (ci.getKey().equals("angle")) {
                        this.car.getSteer().setAngle((Integer) ci.getValue());
                    }
                    else if (ci.getKey().equals("throttle")) {
                        this.car.getEngine().setThrottle((Integer) ci.getValue());
                    }
                    else if (ci.getKey().equals("throttleLimit")) {
                        this.car.getEngine().setThrottleLimit((Integer) ci.getValue());
                    }
                }
            } catch(IOException e) {
                logger.error("Could not read from client.");
            }
        }
    }

}
