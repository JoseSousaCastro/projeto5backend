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
            System.out.println("sending.......... " + notification);
            try {
                session.getBasicRemote().sendText(notification);
                System.out.println("Notification sent!");
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

        // Obtém o mapa de contagem de notificações não lidas por remetente
        Map<String, Integer> unreadNotificationCounts = chatBean.countUnreadNotificationsBySender(chatBean.getAllNotificationsByReceiver(usernameId));

        // Converte o mapa em uma representação JSON
        Gson gson = new Gson();
        String jsonNotificationCounts = gson.toJson(unreadNotificationCounts);

        // Envia a representação JSON para o cliente através do WebSocket
        try {
            session.getBasicRemote().sendText(jsonNotificationCounts);
        } catch (IOException e) {
            System.out.println("Error sending notification counts to client: " + e.getMessage());
        }
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