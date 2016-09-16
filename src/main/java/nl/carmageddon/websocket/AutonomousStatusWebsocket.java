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
    List<Session> sessions = new ArrayList<>();
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
        sendImage(session);
    }

    private void sendImage(Session session) throws IOException {
        LookoutResult lookoutResult = autonomousService.getStatus();
        String json = mapper.writeValueAsString(lookoutResult);
        session.getAsyncRemote().sendText(json);
        byte[] imgBytes = lookoutResult.getImgBytes();
        if (imgBytes != null) {
            session.getAsyncRemote().sendText(new String(Base64.encode(imgBytes)));
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        if (message.equals("ping")) {
            session.getAsyncRemote().sendText("pong");
        }
        if (message.equals("status")) {
            sendImage(session);
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

    @Override
    public void update(Observable o, Object arg) {
        try {
            LookoutResult status = (LookoutResult) arg;
            byte[] imgBytes = status.getImgBytes();
//            File fi = new File("./src/main/resources/circles.jpg");
//            imgBytes = Files.readAllBytes(fi.toPath());
            String json = mapper.writeValueAsString(status);
            for (Session session : sessions) {
                session.getAsyncRemote().sendText(json);
                if (imgBytes != null) {
                    session.getAsyncRemote().sendText(new String(Base64.encode(imgBytes)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
