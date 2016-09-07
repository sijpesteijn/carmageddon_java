package nl.carmageddon.websocket;

import nl.carmageddon.domain.AutonomousStatus;
import nl.carmageddon.domain.Car;
import nl.carmageddon.guice.CarmageddonWebsocketConfigurator;
import nl.carmageddon.service.AutonomousService;
import org.codehaus.jackson.map.ObjectMapper;
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
@ServerEndpoint(value = "/autonomous/status", configurator = CarmageddonWebsocketConfigurator.class)
public class AutonomousStatusWebsocket implements Observer{
    private static final Logger log = LoggerFactory.getLogger(AutonomousStatusWebsocket.class);
    List<Session> sessions = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();
    private Car car;
    private AutonomousService autonomousService;

    @Inject
    public AutonomousStatusWebsocket(Car car, AutonomousService autonomousService) {
        this.car = car;
        this.autonomousService = autonomousService;
        autonomousService.addObserver(this);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.add(session);
        String json = mapper.writeValueAsString(autonomousService.getStatus());
        session.getAsyncRemote().sendText(json);
        sessions.add(session);
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
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
    }

    @Override
    public void update(Observable o, Object arg) {
        AutonomousStatus status = (AutonomousStatus) arg;
        try {
            String json = mapper.writeValueAsString(status);
            for (Session session : sessions)
                session.getAsyncRemote().sendText(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
