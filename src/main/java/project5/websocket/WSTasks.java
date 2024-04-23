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

@Singleton
@ServerEndpoint("/websocket/tasks/{token}")
public class WSTasks {

    HashMap<String, Session> sessions = new HashMap<String, Session>();

    @Inject
    private ChatBean chatBean;

    @EJB
    private UserDao userDao;

    public void send(String token, String msg) {
        Session session = sessions.get(token);
        if (session != null) {
            System.out.println("sending.......... " + msg);
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                System.out.println("Something went wrong!");
            }
        }
    }

    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token) {
        System.out.println("A new notifications WebSocket session is opened for client with token: " + token);
        UserEntity receiver = userDao.findUserByToken(token);
        String usernameId = receiver.getUsername();
        System.out.println("usernameId: " + usernameId);
        sessions.put(usernameId, session);
        System.out.println("Session added to sessions map: " + session);
        System.out.println("Sessions map: " + sessions);
        System.out.println("Session get usernameId: " + sessions.get(usernameId));
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason) {
        System.out.println("Notifications Websocket session is closed with CloseCode: " +
                reason.getCloseCode() + ": " + reason.getReasonPhrase());
        for (String key : sessions.keySet()) {
            if (sessions.get(key) == session)
                sessions.remove(key);
        }
    }

    @OnMessage
    public void toDoOnMessage(Session session, String notification, @PathParam("token") String token) {
        System.out.println("Received notification: " + notification);
    }
}
