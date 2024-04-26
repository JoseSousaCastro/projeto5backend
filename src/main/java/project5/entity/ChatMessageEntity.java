package project5.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@NamedQuery(name = "ChatMessage.findChatMessageById", query = "SELECT c FROM ChatMessageEntity c WHERE c.id = :id")
@NamedQuery(name = "ChatMessage.findChatMessageIsRead", query = "SELECT c FROM ChatMessageEntity c WHERE c.sender.username = :sender " +
        "AND c.receiver.username = :receiver AND c.isRead = false")
@NamedQuery(name = "ChatMessage.findAllChatMessagesBetweenUsers", query = "SELECT c FROM ChatMessageEntity c WHERE (c.sender.username = :sender " +
        "AND c.receiver.username = :receiver) OR (c.sender.username = :receiver AND c.receiver.username = :sender) ORDER BY c.sentAt ASC")
@NamedQuery(name = "ChatMessage.findAllUnreadChatMessages", query = "SELECT c FROM ChatMessageEntity c WHERE c.receiver.username = :receiver " +
        "AND c.isRead = false")
@NamedQuery(name = "ChatMessage.markAllChatMessagesAsRead", query = "UPDATE ChatMessageEntity c SET c.isRead = true WHERE c.sender.username = :sender " +
        "AND c.receiver.username = :receiver")
@NamedQuery(name = "ChatMessage.findLatestChatMessage", query = "SELECT c FROM ChatMessageEntity c WHERE (c.sender.username = :sender " +
        "AND c.receiver.username = :receiver) AND c.sentAt = (SELECT MAX(c.sentAt) FROM ChatMessageEntity c WHERE (c.sender.username = :sender " +
        "AND c.receiver.username = :receiver))")
@NamedQuery(name = "ChatMessage.findAllChatMessagesBetweenUsersReceiverIsUser", query = "SELECT c FROM ChatMessageEntity c WHERE c.receiver.username = :receiver " +
        "AND c.sender.username = :sender AND c.isRead = false")
@NamedQuery(name = "ChatMessage.findLatestChatMessageToMarkAsDelivered", query = "SELECT c FROM ChatMessageEntity c WHERE (c.sender.username = :sender " +
        "AND c.receiver.username = :receiver) AND c.sentAt = (SELECT MAX(c.sentAt) FROM ChatMessageEntity c WHERE (c.sender.username = :sender " +
        "AND c.receiver.username = :receiver))")
public class ChatMessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id")
    private UserEntity receiver;

    @Column(name = "message")
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "delivered")
    private boolean delivered;


    public ChatMessageEntity() {
    }

    public ChatMessageEntity(UserEntity sender, UserEntity receiver, String message, LocalDateTime sentAt, boolean isRead, boolean delivered) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    public ChatMessageEntity(UserEntity sender, UserEntity receiver, String message, LocalDateTime sentAt) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.sentAt = sentAt;
        this.isRead = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }


    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getSenderUsername() {
        return sender.getUsername();
    }

    public String getReceiverUsername() {
        return receiver.getUsername();
    }
}


