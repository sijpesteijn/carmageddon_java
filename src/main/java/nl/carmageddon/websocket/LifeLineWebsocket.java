package nl.carmageddon.websocket;

import nl.carmageddon.domain.Car;
import nl.carmageddon.guice.CarmageddonWebsocketConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@ServerEndpoint(value = "/lifeline", configurator = CarmageddonWebsocketConfigurator.class)
public class LifeLineWebsocket {
    private static final Logger log = LoggerFactory.getLogger(LifeLineWebsocket.class);
    List<Session> sessions = new ArrayList<>();
    private Car car;

    @Inject
    public LifeLineWebsocket(Car car) {
        this.car = car;
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        checkConnectedClients();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.equals("ping")) {
            session.getAsyncRemote().sendText("pong");
        }
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        sessions.remove(session);
        checkConnectedClients();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        checkConnectedClients();
    }

    private void checkConnectedClients() {
        if (sessions.isEmpty()) {
            car.setConnected(false);
        } else {
            car.setConnected(true);
        }
    }
}
