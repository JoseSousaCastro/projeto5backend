package project5.bean;

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

@Stateless
public class ChatBean {

    @Inject
    private ChatMessageDao messageDao;

    @Inject
    private ChatNotificationDao notificationDao;

    @Inject
    private UserDao userDao;

    public void sendMessage(String senderUsername, String receiverUsername, String messageContent) {
        UserEntity sender = getUserByUsername(senderUsername);
        UserEntity receiver = getUserByUsername(receiverUsername);

        if (sender != null && receiver != null) {
            LocalDateTime sentAt = LocalDateTime.now();
            boolean isRead = false;

            ChatMessageEntity messageEntity = new ChatMessageEntity(sender, receiver, messageContent, sentAt, isRead);
            messageDao.create(messageEntity);

            // Enviar notificação ao receptor
            sendNotification(receiver, senderUsername + " enviou uma mensagem", sentAt);
        }
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
            notifications.add(chatNotificationEntityToDTO(entity));
        }

        return notifications;
    }

    public int countUnreadNotifications(String receiverUsername) {
        return notificationDao.countAllChatNotificationsNotRead(receiverUsername);
    }

    public void markNotificationAsRead(String notificationId) {
        ChatNotificationEntity notification = notificationDao.findChatNotificationById(notificationId);
        if (notification != null && !notification.isRead()) {
            notification.setRead(true);
            notificationDao.update(notification);
        }
    }

    // Métodos auxiliares para conversão de entidade para DTO e vice-versa, e outras operações relacionadas ao chat

    private UserEntity getUserByUsername(String username) {
        if (username != null && !username.isEmpty())
            return userDao.findUserByUsername(username);
        return null;
    }

    private ChatMessage chatMessageEntityToDTO(ChatMessageEntity entity) {
        if (entity != null) {
            return new ChatMessage(entity.getId(), entity.getSender().getUsername(), entity.getReceiver().getUsername(),
                    entity.getMessage(), entity.getSentAt(), entity.isRead());
        } else {
            return null;
        }
    }

    private ChatMessageEntity chatMessageDTOToEntity(ChatMessage message) {
        if (message != null) {
            UserEntity sender = getUserByUsername(message.getSenderUsername());
            UserEntity receiver = getUserByUsername(message.getReceiverUsername());
            return new ChatMessageEntity(sender, receiver, message.getMessage(), message.getSentAt(), message.isRead());
        } else {
            return null;
        }
    }

    private ChatNotification chatNotificationEntityToDTO(ChatNotificationEntity entity) {
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
}
