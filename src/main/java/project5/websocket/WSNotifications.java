package project5.websocket;

import com.google.gson.Gson;
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
import java.util.Map;

@Singleton
@ServerEndpoint("/websocket/notifications/{token}")
public class WSNotifications {

    HashMap<String, Session> sessions = new HashMap<String, Session>();

    @Inject
    private ChatBean chatBean;

    @EJB
    private UserDao userDao;

    public void send(String username, String notification) {
        Session session = sessions.get(username);
        System.out.println("Username no WSNotifications : " + username);
        System.out.println("Session no WSNotifications : " + session);
        System.out.println("Notification no WSNotifications : " + notification);
        if (session != null) {
            System.out.println("sending via WSNotifications.......... " + notification);
            try {
                session.getBasicRemote().sendText(notification);
                System.out.println("Notification sent via WSNotifications!");
            } catch (IOException e) {
                System.out.println("Something went wrong during send method on WSNotifications!");
            }
        }
    }

    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token) {
        System.out.println("A new WSNotifications session is opened for client with token: " + token);
        UserEntity receiver = userDao.findUserByToken(token);
        String usernameId = receiver.getUsername();
        System.out.println("WSNotifications usernameId: " + usernameId);
        sessions.put(usernameId, session);
        System.out.println("Session added to sessions map on WSNotifications: " + session);
        System.out.println("Sessions map on WSNotifications: " + sessions);
        System.out.println("WSNotifications session get usernameId: " + sessions.get(usernameId));
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason) {
        System.out.println("WSNotifications session is closed with CloseCode: " +
                reason.getCloseCode() + ": " + reason.getReasonPhrase());
        for (String key : sessions.keySet()) {
            if (sessions.get(key) == session) {
                sessions.remove(key);
                break;
            }
        }
    }

    @OnMessage
    public void toDoOnMessage(Session session, String notification, @PathParam("token") String token) {
        System.out.println("Received notification on WSNotifications: " + notification);
    }
}
