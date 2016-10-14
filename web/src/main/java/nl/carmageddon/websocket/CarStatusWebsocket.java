package nl.carmageddon.websocket;

import nl.carmageddon.domain.CarmageddonSettings;
import nl.carmageddon.guice.CarmageddonWebsocketConfigurator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.DataInputStream;
import java.io.IOException;
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
@ServerEndpoint(value = "/car/status", configurator = CarmageddonWebsocketConfigurator.class)
public class CarStatusWebsocket {
    private static final Logger log = LoggerFactory.getLogger(CarStatusWebsocket.class);
    private List<Session> sessions = new ArrayList<>();
    private String responseLine;
    private ObjectMapper mapper = new ObjectMapper();
    private final ScheduledExecutorService bbTimer = Executors.newSingleThreadScheduledExecutor();

    @Inject
    public CarStatusWebsocket(CarmageddonSettings settings) throws IOException {
//        this.car = car;
//        this.car.addObserver(this);
//        this.car.getSteer().addObserver(this);
//        this.car.getEngine().addObserver(this);
        Socket socket = new Socket(settings.getBeagleBoneSettings().getBeagleBoneIp(),
                                   settings.getBeagleBoneSettings().getCarStatusPort());
        bbTimer.schedule(() -> {
            try {
                DataInputStream is = new DataInputStream(socket.getInputStream());
                while(true) {
                    while (is != null && (responseLine = is.readLine()) != null) {
                        this.sessions.forEach(session -> {
                            session.getAsyncRemote().sendText(responseLine);
                        });
                    }
                }
            } catch(Exception e) {

            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        log.debug("Session added " + session.toString());
//        String json = mapper.writeValueAsString(this.car);
//        session.getAsyncRemote().sendText(json);
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.equals("ping")) {
            session.getAsyncRemote().sendText("pong");
        }
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

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Session removed " + session.toString());
        sessions.remove(session);
    }

//    @Override
//    public void update(Observable o, Object arg) {
//        try {
//            String json = mapper.writeValueAsString(this.car);
//            for (Session session : sessions)
//                session.getAsyncRemote().sendText(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
