package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDateTime;

@XmlRootElement
public class ChatMessage {

    @XmlElement
    private Long id;
    @XmlElement
    private String senderUsername;
    @XmlElement
    private String receiverUsername;
    @XmlElement
    private String message;
    @XmlElement
    private LocalDateTime sentAt;
    @XmlElement
    private boolean isRead;
    @XmlElement
    private boolean delivered;

    private static final long serialVersionUID = 1L;


    public ChatMessage() {
    }

    public ChatMessage(Long id, String senderUsername, String receiverUsername, String message, LocalDateTime sentAt, boolean isRead, boolean delivered) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.message = message;
        this.sentAt = sentAt;
        this.isRead = isRead;
        this.delivered = delivered;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
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

}
