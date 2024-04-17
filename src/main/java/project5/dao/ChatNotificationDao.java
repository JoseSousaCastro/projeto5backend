package project5.dao;

import jakarta.ejb.Stateless;
import project5.entity.ChatNotificationEntity;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;


@Stateless
public class ChatNotificationDao extends AbstractDao<ChatNotificationEntity> {

    private static final long serialVersionUID = 1L;

    public ChatNotificationDao() {
        super(ChatNotificationEntity.class);
    }

    public ChatNotificationEntity findChatNotificationById(String id) {
        try {
            return (ChatNotificationEntity) em.createNamedQuery("ChatNotification.findChatNotificationById").setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public ChatNotificationEntity findChatNotificationIsRead(String senderUsername, String receiverUsername) {
        try {
            return (ChatNotificationEntity) em.createNamedQuery("ChatNotification.findChatNotificationIsRead")
                    .setParameter("sender", senderUsername)
                    .setParameter("receiver", receiverUsername)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public ArrayList<ChatNotificationEntity> findAllChatNotificationsNotRead(String receiverUsername) {
        try {
            return new ArrayList<>(em.createNamedQuery("ChatNotification.findAllChatNotificationsNotRead", ChatNotificationEntity.class)
                    .setParameter("receiver", receiverUsername)
                    .getResultList());
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public int countAllChatNotificationsNotRead(String receiverUsername) {
        try {
            return ((Number) em.createNamedQuery("ChatNotification.countAllChatNotificationsNotRead")
                    .setParameter("receiver", receiverUsername)
                    .getSingleResult()).intValue();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public void markAllChatNotificationsAsRead(String receiverUsername) {
        ArrayList<ChatNotificationEntity> unreadNotifications = new ArrayList<>(em.createNamedQuery("ChatNotification.findUnreadChatNotifications",
                        ChatNotificationEntity.class)
                .setParameter("receiver", receiverUsername)
                .getResultList());

        if (!unreadNotifications.isEmpty()) {
            // Marca a primeira notificação como lida
            unreadNotifications.get(0).setRead(true);
            em.merge(unreadNotifications.get(0));

            // Marca as notificações restantes como lidas, se houver
            for (int i = 1; i < unreadNotifications.size(); i++) {
                ChatNotificationEntity notification = unreadNotifications.get(i);
                notification.setRead(true);
                em.merge(notification);
            }
        }
    }


    public void markChatNotificationAsRead(String senderUsername, String receiverUsername) {
        em.createNamedQuery("ChatNotification.markChatNotificationAsRead")
                .setParameter("sender", senderUsername)
                .setParameter("receiver", receiverUsername)
                .executeUpdate();
    }

    public void dontShowChatNotification(String senderUsername, String receiverUsername) {
        em.createNamedQuery("ChatNotification.dontShowChatNotification")
                .setParameter("sender", senderUsername)
                .setParameter("receiver", receiverUsername)
                .executeUpdate();
    }

    public ArrayList<ChatNotificationEntity> findAllChatNotificationsByReceiver(String receiverUsername) {
        try {
            return new ArrayList<>(em.createNamedQuery("ChatNotification.findAllChatNotificationsByReceiver", ChatNotificationEntity.class)
                    .setParameter("receiver", receiverUsername)
                    .getResultList());
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public void update(ChatNotificationEntity notification) {
        if (notification != null) {
            em.merge(notification);
        } else {
            System.out.println("Error: ChatNotificationEntity is null");
        }
    }

    public void create(ChatNotificationEntity notificationEntity) {
        if (notificationEntity != null) {
            em.persist(notificationEntity);
        } else {
            System.out.println("Error: ChatNotificationEntity is null");
        }
    }
}
