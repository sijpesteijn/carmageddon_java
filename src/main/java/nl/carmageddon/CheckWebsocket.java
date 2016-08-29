package nl.carmageddon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@ServerEndpoint("/check")
public class CheckWebsocket {
    private static final Logger log = LoggerFactory.getLogger(CheckWebsocket.class);

    @OnOpen
    public void onOpen(Session session) {
        log.debug("Open");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.debug("Message");
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        log.debug("Close");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.debug("Error");
    }
}
