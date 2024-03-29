package nl.carmageddon.car.socket;

import com.google.inject.Inject;
import nl.carmageddon.car.domain.Car;
import nl.carmageddon.car.domain.CarSettings;
import nl.carmageddon.car.domain.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.DataInputStream;
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
public class LifeLineSocket {
    private static Logger logger = LoggerFactory.getLogger(LifeLineSocket.class);
    private final ScheduledExecutorService socketListenerTimer = Executors.newSingleThreadScheduledExecutor();
    private final int delay = 10000;
    private ServerSocket socket;
    private Car car;
    private List<Socket> clientConnections;
    private long lastTime;

    @Inject
    public LifeLineSocket(CarSettings settings, Car car, List<Socket> clientConnections) throws IOException {
        this.car = car;
        this.clientConnections = clientConnections;
        this.car.setConnected(false);
        socket = new ServerSocket(settings.lifelinePort());

        this.socketListenerTimer.schedule(() -> {
            while(true) {
                try {
                    logger.debug("Listening for lifeline connections.");
                    Socket connection = socket.accept();
                    logger.debug("Connection received.");
                    new LifeLineHandler(connection).start();
                } catch (IOException e) {
                    logger.error("Could not accept client connection.");
                }
            }
        }, 100, TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (this.car.isConnected() && System.currentTimeMillis() - this.lastTime > this.delay * 2) {
                logger.debug("No ping command after " + (this.delay*2));
                this.clientConnections = new ArrayList<>();
                this.car.setMode(Mode.disabled);
                this.car.setConnected(false);
            }
        }, 0, this.delay, TimeUnit.MILLISECONDS);
    }

    private class LifeLineHandler extends Thread {
        private PrintStream out;
        private DataInputStream dis;

        public LifeLineHandler(Socket connection) throws IOException {
            dis = new DataInputStream(connection.getInputStream());
            out = new PrintStream(connection.getOutputStream());
        }

        @Override
        public void run() {
            String req;
            try {
                while (dis != null && (req = dis.readLine()) != null) {
                    if (!car.isConnected()) {
                        car.setConnected(true);
                    }
                    lastTime = System.currentTimeMillis();
                    out.println("pong");
                    out.flush();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
