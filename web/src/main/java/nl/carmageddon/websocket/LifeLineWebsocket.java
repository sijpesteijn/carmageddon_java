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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@ServerEndpoint(value = "/lifeline", configurator = CarmageddonWebsocketConfigurator.class)
public class LifeLineWebsocket {
    private static final Logger log = LoggerFactory.getLogger(LifeLineWebsocket.class);
    private CarmageddonSettings settings;
    private PrintStream out = null;
    private DataInputStream is = null;
    private Socket socket;
    private List<Session> sessions = new ArrayList<>();

    @Inject
    public LifeLineWebsocket(CarmageddonSettings settings) throws IOException {
        this.settings = settings;
        socket = new Socket(this.settings.getBeagleBoneSettings().getBeagleBoneIp(),
                            this.settings.getBeagleBoneSettings().getLifeLinePort());
        try {
            out = new PrintStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            log.error("Can't get streams. " + e.getMessage());
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (sessions.size() > 0) {
//                log.debug("sending ping.");
                out.println("ping");
                out.flush();
//                log.debug("waiting for pong");
                String responseLine;
                boolean received = false;
                try {
                    while (!received && is != null && (responseLine = is.readLine()) != null) {
                        if (responseLine.equals("pong")) {
                            log.debug("pong received");
                            received = true;
                        }
                    }
                    if (received) {
                        notifyClients();
                    }
                } catch (IOException e) {

                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void notifyClients() {
        this.sessions.forEach(session -> {
            session.getAsyncRemote().sendText("pong");
        });
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Session removed " + session.toString());
        sessions.remove(session);
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) throws IOException {
        log.debug("Session removed " + session.toString());
        try {
            sessions.remove(session);
            session.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

//    @OnMessage
//    public void onMessage(String message, Session session) throws IOException {
//        if (message.equals("ping")) {
//            log.debug("sending ping. " + new Date());
//            PrintStream out = new PrintStream(getSocket().getOutputStream());
//            out.println("ping");
//            out.flush();
//            log.debug("waiting for pong");
//            DataInputStream is = new DataInputStream(getSocket().getInputStream());
//            String responseLine;
//            boolean received = false;
//            while (!received && is != null && (responseLine = is.readLine()) != null) {
//                if (responseLine.equals("pong")) {
//                    log.debug("pong received");
//                    received = true;
//                }
//            }
//            session.getAsyncRemote().sendText("pong");
//        }
//    }

}
