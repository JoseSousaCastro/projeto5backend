package project5.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import project5.entity.ChatMessageEntity;

import java.util.ArrayList;

import org.apache.logging.log4j.*;

@Stateless
public class ChatMessageDao extends AbstractDao<ChatMessageEntity> {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(ChatMessageDao.class);

    public ChatMessageDao() {
        super(ChatMessageEntity.class);
    }

    public ChatMessageEntity findChatMessageById(Long id) {
        try {
            return em.createNamedQuery("ChatMessage.findChatMessageById", ChatMessageEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Retorna null se não houver resultado
        }
    }

    public ChatMessageEntity findChatMessageIsRead(String senderUsername, String receiverUsername) {
        try {
            return (ChatMessageEntity) em.createNamedQuery("ChatMessage.findChatMessageIsRead")
                    .setParameter("sender", senderUsername)
                    .setParameter("receiver", receiverUsername)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public ArrayList<ChatMessageEntity> findAllChatMessagesBetweenUsers(String senderUsername, String receiverUsername) {
        try {
            return (ArrayList<ChatMessageEntity>) em.createNamedQuery("ChatMessage.findAllChatMessagesBetweenUsers", ChatMessageEntity.class)
                    .setParameter("sender", senderUsername)
                    .setParameter("receiver", receiverUsername)
                    .getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>(); // Retorna uma lista vazia se não houver resultados
        }
    }


    public ArrayList<ChatMessageEntity> findAllUnreadChatMessages(String senderUsername) {
        try {
            return new ArrayList<>(em.createNamedQuery("ChatMessage.findAllUnreadChatMessages", ChatMessageEntity.class)
                    .setParameter("receiver", senderUsername)
                    .getResultList());
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public void markAllChatMessagesAsRead(String senderUsername, String receiverUsername) {
        try {
            // Encontra a mensagem mais recente
            ChatMessageEntity latestMessage = findLatestChatMessage(senderUsername, receiverUsername);

            if (latestMessage != null) {
                // Marca todas as mensagens anteriores como lidas
                em.createQuery("UPDATE ChatMessageEntity c SET c.isRead = true, c.delivered = true " +
                                "WHERE c.sender = :senderUsername " +
                                "AND c.receiver = :receiverUsername " +
                                "AND c.sentAt < :latestSentAt")
                        .setParameter("senderUsername", senderUsername)
                        .setParameter("receiverUsername", receiverUsername)
                        .setParameter("latestSentAt", latestMessage.getSentAt())
                        .executeUpdate();
            }
        } catch (NoResultException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public ChatMessageEntity findLatestChatMessage(String senderUsername, String receiverUsername) {
        try {
            return (ChatMessageEntity) em.createNamedQuery("ChatMessage.findLatestChatMessage")
                    .setParameter("sender", senderUsername)
                    .setParameter("receiver", receiverUsername)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void create(ChatMessageEntity messageEntity) {
        System.out.println("messageEntity" + messageEntity);
        if (messageEntity != null) {
            em.persist(messageEntity);
            logger.info("ChatMessageEntity created: " + messageEntity);
        } else {
            logger.error("Error: messageEntity is null");
            System.out.println("Error: messageEntity is null");
        }
    }

    public void update(ChatMessageEntity messageEntity) {
        if (messageEntity != null) {
            em.merge(messageEntity);
            logger.info("ChatMessageEntity updated: " + messageEntity);
        } else {
            logger.error("Error: messageEntity is null");
            System.out.println("Error: messageEntity is null");
        }
    }

    public ArrayList<ChatMessageEntity> findAllChatMessagesBetweenUsersReceiverIsUser(String usernameSender, String usernameReceiver) {
        try {
            return new ArrayList<>(em.createNamedQuery("ChatMessage.findAllChatMessagesBetweenUsersReceiverIsUser", ChatMessageEntity.class)
                    .setParameter("receiver", usernameSender)
                    .setParameter("sender", usernameReceiver)
                    .getResultList());
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public ChatMessageEntity findLatestChatMessageToMarkAsDelivered(String senderUsername, String receiverUsername) {
        try {
            return (ChatMessageEntity) em.createNamedQuery("ChatMessage.findLatestChatMessageToMarkAsDelivered")
                    .setParameter("sender", senderUsername)
                    .setParameter("receiver", receiverUsername)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
