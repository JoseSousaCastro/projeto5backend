package project5.websocket;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import project5.bean.ChatBean;
import project5.bean.UserBean;
import project5.dao.UserDao;
import project5.dto.ChatMessage;
import project5.entity.UserEntity;

import java.io.IOException;
import java.util.HashMap;

@Singleton
@ServerEndpoint("/websocket/notifier/{token}/{receiver}")
public class Notifier {

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
    public void toDoOnOpen(Session session, @PathParam("token") String token, @PathParam("receiver") String receiverUsername) {
        System.out.println("A new WebSocket session is opened for client with token: " + token);
        UserEntity sender = userDao.findUserByToken(token);
        String senderUsername = sender.getUsername();
        String conversationID = senderUsername + receiverUsername;
        System.out.println("Conversation ID: " + conversationID);
        sessions.put(conversationID, session);
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason) {
        System.out.println("Websocket session is closed with CloseCode: " +
                reason.getCloseCode() + ": " + reason.getReasonPhrase());
        for (String key : sessions.keySet()) {
            if (sessions.get(key) == session)
                sessions.remove(key);
        }
    }

    @OnMessage
    public void toDoOnMessage(Session session, String message, @PathParam("token") String token, @PathParam("receiver") String receiverUsername) {
        System.out.println("Received message: " + message);

        UserEntity sender = userDao.findUserByToken(token);
        UserEntity receiver = userDao.findUserByUsername(receiverUsername);

        if (sender != null && receiver != null) {
            String text = chatBean.extractMessageText(message);
            chatBean.createAndSaveMessage(sender, receiver, text);

            String conversationID = receiverUsername + sender.getUsername();
            Session receiverSession = sessions.get(conversationID);

            if (receiverSession != null) {
                System.out.println("Receiver is online. Sending message to receiver.");
                ChatMessage chatMessage = chatBean.findLatestChatMessage(sender.getUsername(), receiver.getUsername());
                System.out.println("chatMessage: " + chatMessage.getMessage());
                // converte a mensagem para JSON para enviar para o frontend pelo websocket
                String jsonMessage = chatBean.convertChatMessageToJSON(chatMessage);
                System.out.println("jsonMessage: " + jsonMessage);
                try {
                    receiverSession.getBasicRemote().sendText(jsonMessage);
                } catch (IOException e) {
                    System.out.println("Something went wrong!");
                }
            } else {
                System.out.println("Sender or receiver not found. Unable to save message.");
            }
        }
    }
}
