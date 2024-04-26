package project5.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import project5.dto.User;

import java.util.Properties;

import org.apache.logging.log4j.*;


@Stateless
public class EmailBean {
    @EJB
    private UserBean userBean;
    private final String username = "mail.do.castro@gmail.com"; // Seu endereço de e-mail
    private final String password = "xktz ownn favj ldum"; // Sua senha de e-mail
    private final String host = "smtp.gmail.com";
    private final String port = "587";

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(EmailBean.class);


    public EmailBean() {
    }

    public boolean sendEmail(String email, String subject, String body) {
        boolean sent = false;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            msg.setSubject(subject);
            msg.setText(body);

            Transport.send(msg);
            sent = true;
        } catch (MessagingException e) {
            sent = false;
            e.printStackTrace();
        }
        return sent;
    }

    public boolean sendConfirmationEmail(User user) {
        boolean sent = false;
        String userEmail = user.getEmail();
        String subject = "Account confirmation for Agile Scrum Tool";
        String confirmationLink = "http://localhost:3000/register-confirmation/" + user.getUsername();
        String body = "Hello " + user.getUsername() + ",\n\n" +
                "Please click the link below to confirm your account:\n" +
                confirmationLink + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Thank you,\n" +
                "Agile Scrum Tool Team";
        if (sendEmail(userEmail, subject, body)) {
            sent = true;
        } else {
            userBean.delete(user.getUsername());
        }
        return sent;
    }

    public boolean sendPasswordResetEmail(User user) {
        boolean sent = false;
        String userEmail = user.getEmail();
        String subject = "Password reset for Agile Scrum Tool";
        String resetLink = "http://localhost:3000/reset-password/" + user.getUsername();
        String body = "Hello " + user.getUsername() + ",\n\n" +
                "Please click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Thank you,\n" +
                "Agile Scrum Tool Team";
        if (sendEmail(userEmail, subject, body)) {
            sent = true;
        }
        return sent;
    }
}