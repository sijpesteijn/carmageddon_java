package nl.carmageddon.car.socket;

import com.google.inject.Inject;
import nl.carmageddon.car.domain.Car;
import nl.carmageddon.car.domain.CarSettings;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class CarStatusSocket {
    private static Logger logger = LoggerFactory.getLogger(CarStatusSocket.class);
    private ObjectMapper mapper = new ObjectMapper();
    private String carStatus = "Can't tell";
    private Car car;
    private ServerSocket socket;
    private List<Socket> clientConnections = new ArrayList<>();
    private final ScheduledExecutorService lifelineTimer = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService statusTimer = Executors.newSingleThreadScheduledExecutor();

    private Runnable socketListener = () -> {
        while(true) {
            try {
                logger.debug("Listening for car status connections.");
                Socket connection = socket.accept();
                logger.debug("Connection received.");
                this.clientConnections.add(connection);

            } catch (IOException e) {
                logger.error("Could not accept client connection.");
            }
        }
    };

    private Runnable statusSender = () -> {
        if (this.clientConnections.size() > 0) {
            try {
                carStatus = mapper.writeValueAsString(this.car);
            } catch (IOException e) {
                logger.debug("Could not serialize car.");
            }
            this.clientConnections.forEach(clientConnection -> {
                PrintStream out = null;
                try {
                    out = new PrintStream(clientConnection.getOutputStream());
                    out.println(carStatus);
                    out.flush();
                } catch (IOException e) {
                    logger.debug("Could not send status to client " + clientConnection.getInetAddress());
                }
            });
        }
    };

    @Inject
    public CarStatusSocket(CarSettings settings, Car car) throws IOException {
        this.car = car;
        this.socket = new ServerSocket(settings.carStatusPort());
        this.lifelineTimer.schedule(socketListener, 100, TimeUnit.MILLISECONDS);
        this.statusTimer.scheduleAtFixedRate(statusSender, 0, 500, TimeUnit.MILLISECONDS);
    }

}
