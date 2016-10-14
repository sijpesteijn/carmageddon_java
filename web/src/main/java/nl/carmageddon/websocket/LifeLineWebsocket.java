package nl.carmageddon.websocket;

import nl.carmageddon.domain.CarmageddonSettings;
import nl.carmageddon.guice.CarmageddonWebsocketConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@ServerEndpoint(value = "/lifeline", configurator = CarmageddonWebsocketConfigurator.class)
public class LifeLineWebsocket {
    private static final Logger log = LoggerFactory.getLogger(LifeLineWebsocket.class);
    private CarmageddonSettings settings;
    private Socket socket;

    @Inject
    public LifeLineWebsocket(CarmageddonSettings settings) {
        this.settings = settings;
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        if (message.equals("ping")) {
            log.debug("sending ping.");
            PrintStream out = new PrintStream(getSocket().getOutputStream());
            out.println("ping");
            out.flush();
            log.debug("waiting for pong");
            DataInputStream is = new DataInputStream(getSocket().getInputStream());
            String responseLine;
            boolean received = false;
            while (!received && is != null && (responseLine = is.readLine()) != null) {
                if (responseLine.equals("pong")) {
                    log.debug("pong received");
                    received = true;
                }
            }
            session.getAsyncRemote().sendText("pong");
        }
    }

    private Socket getSocket() throws IOException {
        if (this.socket == null || !this.socket.isConnected()) {
            this.socket = new Socket(this.settings.getBeagleBoneSettings().getBeagleBoneIp(),
                          this.settings.getBeagleBoneSettings().getLifeLinePort());
        }
        return this.socket;
    }

}
