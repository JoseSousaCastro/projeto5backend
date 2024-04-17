package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement(name = "ChatMessage")
public class ChatMessageDTO {

    @XmlElement(name = "Id")
    private Long id;

    @XmlElement(name = "SenderUsername")
    private String senderUsername;

    @XmlElement(name = "ReceiverUsername")
    private String receiverUsername;

    @XmlElement(name = "Message")
    private String message;

    @XmlElement(name = "SentAt")
    private LocalDateTime sentAt;

    @XmlElement(name = "IsRead")
    private boolean isRead;

    public ChatMessageDTO() {
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
}
