package project5.websocket;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import project5.bean.ChatBean;
import project5.dao.UserDao;
import project5.dto.ChatMessage;
import project5.dto.ChatNotification;
import project5.entity.UserEntity;

import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.*;

@Singleton
@ServerEndpoint("/websocket/chat/{token}/{receiver}")
public class WSChat {

    HashMap<String, Session> sessions = new HashMap<String, Session>();

    @Inject
    private ChatBean chatBean;
    @EJB
    private UserDao userDao;
    @EJB
    WSNotifications WSNotifications;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(WSChat.class);

    public void send(String receiverUsername, String senderUsername, String token) {
        Session session = sessions.get(token);
        String msg = "All messages and notifications from " + receiverUsername + " to " + senderUsername + " are read";
        if (session != null) {
            System.out.println("sending from WSCHat.......... " + msg);
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                System.out.println("Something went wrong during send method on WSCHat!");
            }
        }
    }

    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token, @PathParam("receiver") String receiverUsername) {
        System.out.println("A new WSCHat session is opened for client with token: " + token);
        UserEntity sender = userDao.findUserByToken(token);
        String senderUsername = sender.getUsername();
        String conversationID = senderUsername + receiverUsername;
        System.out.println("Conversation ID from WSCHat: " + conversationID);
        sessions.put(conversationID, session);
        chatBean.markAllChatMessagesAsReadAndDelivered(senderUsername);
        chatBean.setNotificationsAsReadFromSenderToUser(senderUsername, receiverUsername);
        send(receiverUsername, senderUsername, token);
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason) {
        System.out.println("WSCHat session is closed with CloseCode: " +
                reason.getCloseCode() + ": " + reason.getReasonPhrase());
        for (String key : sessions.keySet()) {
            if (sessions.get(key) == session) {
                sessions.remove(key);
                break;
            }
        }
    }

    @OnMessage
    public void toDoOnMessage(Session session, String message, @PathParam("token") String token, @PathParam("receiver") String receiverUsername) {
        System.out.println("Received message on WSCHat: " + message);

        UserEntity sender = userDao.findUserByToken(token);
        UserEntity receiver = userDao.findUserByUsername(receiverUsername);

        if (sender != null && receiver != null) {
            String text = chatBean.extractMessageText(message);
            chatBean.createAndSaveMessage(sender, receiver, text);

            String conversationID = receiverUsername + sender.getUsername();
            Session receiverSession = sessions.get(conversationID);

            String conversationID2 = sender.getUsername() + receiverUsername;
            Session senderSession = sessions.get(conversationID2);

            if (receiverSession != null) {
                System.out.println("Receiver message session found on WSCHat. Sending message to receiver on WSCHat.");
                ChatMessage chatMessage = chatBean.findLatestChatMessage(sender.getUsername(), receiver.getUsername());
                // converte a mensagem para JSON para enviar para o frontend pelo websocket
                String jsonMessage = chatBean.convertChatMessageToJSON(chatMessage);
                try {
                    receiverSession.getBasicRemote().sendText(jsonMessage);
                    send(receiverUsername, sender.getUsername(), token);
                } catch (IOException e) {
                    System.out.println("Something went wrong during onMessage sending back the message on WSCHat!");
                }
            } else {
                chatBean.createAndSaveNotification(sender, receiver, text);
                System.out.println("Receiver message session not found on WSCHat. Message saved and notification created");
                if (receiver.getToken() != null) {
                    // enviar para o frontend através do notifiernotifications
                    System.out.println("WSCHat can confirm that receiver is online. A notification will be generated and sent via WSNotifications.");
                    ChatNotification chatNotification = chatBean.findLatestChatNotification(sender.getUsername(), receiver.getUsername());
                    System.out.println("WSCHat chatNotification: " + chatNotification);
                    System.out.println("WSCHat chatNotification.getSenderUsername(): " + chatNotification.getSenderUsername());
                    System.out.println("WSCHat chatNotification.getReceiverUsername(): " + chatNotification.getReceiverUsername());
                    String jsonNotification = chatBean.convertChatNotificationToJSON(chatNotification);
                    System.out.println("WSCHat jsonNotification: " + jsonNotification);
                    System.out.println("WSCHat receiver username: " + receiver.getUsername());
                    WSNotifications.send(receiver.getUsername(), jsonNotification);
                } else {
                    System.out.println("Receiver session not found on WSCHat and receiver not online. Message and notification saved for load on next login.");
                }
            }
        }
    }
}
