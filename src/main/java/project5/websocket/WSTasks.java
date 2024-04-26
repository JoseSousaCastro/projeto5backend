package project5.websocket;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import project5.bean.ChatBean;
import project5.dao.UserDao;
import project5.entity.UserEntity;

import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.*;

@Singleton
@ServerEndpoint("/websocket/tasks/{token}")
public class WSTasks {

    HashMap<String, Session> sessions = new HashMap<String, Session>();

    @Inject
    private ChatBean chatBean;
    @EJB
    private UserDao userDao;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(WSTasks.class);

    public void send(String msg) {
        System.out.println("Sending message via WSTasks: " + msg);
        for (Session session : sessions.values()) {
            System.out.println("Session on WSTasks: " + session);
            if (session.isOpen()) {
                System.out.println("Sending message via WSTasks: " + msg);
                try {
                    session.getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    System.out.println("Error sending message to session via WSTasks: " + e.getMessage());
                }
            }
        }
    }

    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token) {
        System.out.println("A new WSTasks session is opened for client with token: " + token);
        UserEntity receiver = userDao.findUserByToken(token);
        String usernameId = receiver.getUsername();
        System.out.println("WSTasks usernameId: " + usernameId);
        sessions.put(usernameId, session);
        System.out.println("Session added to sessions map on WSTasks: " + session);
        System.out.println("Sessions map on WSTasks: " + sessions);
        System.out.println("WSTasks session get usernameId: " + sessions.get(usernameId));
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason) {
        System.out.println("WSTasks session is closed with CloseCode: " +
                reason.getCloseCode() + ": " + reason.getReasonPhrase());
        for (String key : sessions.keySet()) {
            if (sessions.get(key) == session) {
                sessions.remove(key);
                break;
            }
        }
    }

    @OnMessage
    public void toDoOnMessage(Session session, String message, @PathParam("token") String token) {
        System.out.println("Received message on WSTasks: " + message);
    }
}
