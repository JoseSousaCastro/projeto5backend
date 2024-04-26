package project5.bean;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import project5.dao.ChatMessageDao;
import project5.dao.ChatNotificationDao;
import project5.dao.UserDao;
import project5.dto.ChatMessage;
import project5.dto.ChatNotification;
import project5.entity.ChatMessageEntity;
import project5.entity.ChatNotificationEntity;
import project5.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.logging.log4j.*;


@Stateless
public class ChatBean {

    @Inject
    private ChatMessageDao messageDao;
    @Inject
    private ChatNotificationDao notificationDao;
    @Inject
    private UserDao userDao;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(ChatBean.class);

    public ChatMessage convertChatMessageEntityToDTO(ChatMessageEntity entity) {
        ChatMessage message = new ChatMessage();
        message.setId(entity.getId());
        message.setSenderUsername(entity.getSender().getUsername());
        message.setReceiverUsername(entity.getReceiver().getUsername());
        message.setMessage(entity.getMessage());
        message.setSentAt(entity.getSentAt());
        message.setRead(entity.isRead());
        message.setDelivered(entity.isDelivered());

        return message;
    }

    public String extractMessageText(String message) {
        // Extrai o texto da mensagem JSON
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        String text = jsonObject.get("text").getAsString();
        return text;
    }

    public String extractNotificationText(String notification) {
        // Extrai o texto da notificação JSON
        JsonObject jsonObject = JsonParser.parseString(notification).getAsJsonObject();
        String text = jsonObject.get("text").getAsString();
        return text;
    }

    public void createAndSaveMessage(UserEntity sender, UserEntity receiver, String messageText) {
        if (sender != null && receiver != null) {
            LocalDateTime sentAt = LocalDateTime.now();
            boolean isRead = false;
            boolean delivered = false;

            ChatMessageEntity newMessage = new ChatMessageEntity(sender, receiver, messageText, sentAt, isRead, delivered);
            System.out.println("Saving message: " + newMessage);
            messageDao.create(newMessage);
        } else {
            System.out.println("Sender or receiver not found. Unable to save message.");
        }
    }

    public void createAndSaveNotification(UserEntity sender, UserEntity receiver, String messageText) {
        if (sender != null && receiver != null) {
            LocalDateTime sentAt = LocalDateTime.now();
            boolean isRead = false;

            ChatNotificationEntity newNotification = new ChatNotificationEntity(sender, receiver, messageText, sentAt, isRead);
            System.out.println("Saving notification: " + newNotification);
            notificationDao.create(newNotification);
            findLatestChatMessageToMarkAsDelivered(sender.getUsername(), receiver.getUsername());
        } else {
            System.out.println("Sender or receiver not found. Unable to save notification.");
        }
    }

    public void findLatestChatMessageToMarkAsDelivered(String senderUsername, String receiverUsername) {
        ChatMessageEntity message = messageDao.findLatestChatMessageToMarkAsDelivered(senderUsername, receiverUsername);
        if (message != null) {
            message.setDelivered(true);
            messageDao.update(message);
        }
    }

    public ChatMessage findLatestChatMessage(String senderUsername, String receiverUsername) {
        markChatMessageAsReadAndDelivered(messageDao.findLatestChatMessage(senderUsername, receiverUsername).getId());
        return convertChatMessageEntityToDTO(messageDao.findLatestChatMessage(senderUsername, receiverUsername));
    }

    public ChatNotification findLatestChatNotification(String senderUsername, String receiverUsername) {
        return convertChatNotificationEntityToDTO(notificationDao.findLatestChatNotification(senderUsername, receiverUsername));
    }


    public ArrayList<ChatMessage> getAllChatMessagesBetweenUsers(String senderUsername, String receiverUsername) {
        ArrayList<ChatMessageEntity> messageEntities = messageDao.findAllChatMessagesBetweenUsers(senderUsername, receiverUsername);
        ArrayList<ChatMessage> messages = new ArrayList<>();

        for (ChatMessageEntity entity : messageEntities) {
            messages.add(chatMessageEntityToDTO(entity));
        }

        return messages;
    }

    public ArrayList<ChatNotification> getAllNotificationsByReceiver(String receiverUsername) {
        ArrayList<ChatNotificationEntity> notificationEntities = notificationDao.findAllChatNotificationsByReceiver(receiverUsername);
        ArrayList<ChatNotification> notifications = new ArrayList<>();

        for (ChatNotificationEntity entity : notificationEntities) {
            notifications.add(convertChatNotificationEntityToDTO(entity));
        }

        return notifications;
    }


    public int countUnreadNotifications(String receiverUsername) {
        return notificationDao.countAllChatNotificationsNotRead(receiverUsername);
    }

    public void markChatMessageAsReadAndDelivered(Long messageId) {
        ChatMessageEntity message = messageDao.findChatMessageById(messageId);
        if (message != null && !message.isRead()) {
            message.setRead(true);
            message.setDelivered(true);
            messageDao.update(message);
        }
    }

    public void markAllChatMessagesAsReadAndDelivered(String senderUsername) {
        ArrayList<ChatMessageEntity> arrayMessages = messageDao.findAllUnreadChatMessages(senderUsername);
        for (ChatMessageEntity message : arrayMessages) {
            message.setRead(true);
            message.setDelivered(true);
            messageDao.update(message);
        }
    }

    private UserEntity getUserByUsername(String username) {
        if (username != null && !username.isEmpty())
            return userDao.findUserByUsername(username);
        return null;
    }

    private ChatMessage chatMessageEntityToDTO(ChatMessageEntity entity) {
        if (entity != null) {
            return new ChatMessage(entity.getId(), entity.getSender().getUsername(), entity.getReceiver().getUsername(),
                    entity.getMessage(), entity.getSentAt(), entity.isRead(), entity.isDelivered());
        } else {
            return null;
        }
    }

    private ChatMessageEntity chatMessageDTOToEntity(ChatMessageEntity message) {
        if (message != null) {
            UserEntity sender = getUserByUsername(message.getSenderUsername());
            UserEntity receiver = getUserByUsername(message.getReceiverUsername());
            return new ChatMessageEntity(sender, receiver, message.getMessage(), message.getSentAt(), message.isRead(), message.isDelivered());
        } else {
            return null;
        }
    }

    private ChatNotification convertChatNotificationEntityToDTO(ChatNotificationEntity entity) {
        if (entity != null) {
            return new ChatNotification(entity.getId(), entity.getSender().getUsername(), entity.getReceiver().getUsername(),
                    entity.getMessage(), entity.getSentAt(), entity.isRead());
        } else {
            return null;
        }
    }

    private ChatNotificationEntity chatNotificationDTOToEntity(ChatNotification notification) {
        if (notification != null) {
            UserEntity sender = getUserByUsername(notification.getSenderUsername());
            UserEntity receiver = getUserByUsername(notification.getReceiverUsername());
            return new ChatNotificationEntity(sender, receiver, notification.getMessage(), notification.getSentAt(), notification.isRead());
        } else {
            return null;
        }
    }

    private void sendNotification(UserEntity receiver, String messageContent, LocalDateTime sentAt) {
        if (receiver != null) {
            ChatNotificationEntity notificationEntity = new ChatNotificationEntity(null, receiver, messageContent, sentAt, false);
            notificationDao.create(notificationEntity);
        } else {
            System.out.println("Erro ao enviar notificação: receptor não encontrado");
        }
    }

    public String convertChatMessageToJSON(ChatMessage chatMessage) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("senderUsername", chatMessage.getSenderUsername());
        jsonObject.addProperty("receiverUsername", chatMessage.getReceiverUsername());
        jsonObject.addProperty("message", chatMessage.getMessage());
        jsonObject.addProperty("sentAt", chatMessage.getSentAt().toString());
        jsonObject.addProperty("isRead", chatMessage.isRead());

        return jsonObject.toString();
    }

    public String convertChatNotificationToJSON(ChatNotification chatNotification) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("senderUsername", chatNotification.getSenderUsername());
        jsonObject.addProperty("receiverUsername", chatNotification.getReceiverUsername());
        jsonObject.addProperty("message", chatNotification.getMessage());
        jsonObject.addProperty("sentAt", chatNotification.getSentAt().toString());
        jsonObject.addProperty("isRead", chatNotification.isRead());

        return jsonObject.toString();
    }

    public void setMessagesAsReadFromSenderToUser(String usernameSender, String usernameReceiver) {
        ArrayList<ChatMessageEntity> messages = messageDao.findAllChatMessagesBetweenUsersReceiverIsUser(usernameSender, usernameReceiver);
        for (ChatMessageEntity message : messages) {
            message.setRead(true);
            messageDao.update(message);
        }
    }

    public void setNotificationsAsReadFromSenderToUser(String usernameSender, String usernameReceiver) {
        notificationDao.markAllChatNotificationsAsRead(usernameSender, usernameReceiver);
    }
}
