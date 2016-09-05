package nl.carmageddon;

import nl.carmageddon.domain.Engine;
import nl.carmageddon.domain.Steer;
import nl.carmageddon.guice.CarmageddonWebsocketConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@ServerEndpoint(value = "/status", configurator = CarmageddonWebsocketConfigurator.class)
public class StatusWebsocket implements Observer {
    private static final Logger log = LoggerFactory.getLogger(StatusWebsocket.class);
    private final Steer steer;
    private final Engine engine;
    private List<Session> sessions = new ArrayList<>();

    @Inject
    public StatusWebsocket(Steer steer, Engine engine) {
        this.steer = steer;
        this.engine = engine;
        steer.addObserver(this);
        engine.addObserver(this);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        if (sessions.isEmpty()) {
            steer.wobbleWheels();
        }
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
    }

    // TODO: sessions zijn soms gesloten na een timeout.
    @Override
    public void update(Observable o, Object arg) {
        String key = "throttle";
        if (o instanceof Steer)
            key = "angle";
        for (Session session : sessions)
            session.getAsyncRemote().sendText("{\"" + key + "\":\"" + arg + "\"}");
    }
}
