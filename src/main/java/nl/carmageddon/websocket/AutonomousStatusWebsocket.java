package nl.carmageddon.websocket;

import com.sun.jersey.core.util.Base64;
import nl.carmageddon.domain.LookoutResult;
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
    private List<Session> sessions = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();
    private AutonomousService autonomousService;

    @Inject
    public AutonomousStatusWebsocket(AutonomousService autonomousService) {
        this.autonomousService = autonomousService;
        this.autonomousService.addObserver(this);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        log.debug("Session added " + session.toString());
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        if (message.equals("ping")) {
            session.getAsyncRemote().sendText("pong");
        }
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        log.debug("Session removed " + session.toString());
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Session added " + session.toString());
        sessions.remove(session);
    }

    // TODO Bij een reload van jetty watch worden de sessie aangemeld bij de oude websocket.
    // Ik vermoed dat iets verder in de chain het tegenhoudt. Alleen een probleem bij development.
    @Override
    public void update(Observable o, Object arg) {
        try {
            LookoutResult status = (LookoutResult) arg;
            byte[] imgBytes = status.getImgBytes();
            String json = mapper.writeValueAsString(status);
            for (Session session : sessions) {
                session.getAsyncRemote().sendText(json);
                if (imgBytes != null) { // TODO encoding uit de loop
                    session.getAsyncRemote().sendText(new String(Base64.encode(imgBytes)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
