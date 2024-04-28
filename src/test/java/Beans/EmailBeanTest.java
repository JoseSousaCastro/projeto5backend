package Beans;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import project5.bean.EmailBean;
import project5.bean.UserBean;
import project5.dto.User;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailBeanTest {

    @Mock
    private UserBean userBean;

    @InjectMocks
    private EmailBean emailBean;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendEmail_Success() throws MessagingException {
        // Mocking data
        String email = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Mocking session and transport
        Session session = mock(Session.class);
        Transport transport = mock(Transport.class);
        when(session.getTransport("smtp")).thenReturn(transport);

        // Testing
        assertTrue(emailBean.sendEmail(email, subject, body));
    }

    @Test
    public void testSendConfirmationEmail_Success() {
        // Mocking data
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        // Testing
        assertTrue(emailBean.sendConfirmationEmail(user));
    }


    @Test
    public void testSendPasswordResetEmail_Success() {
        // Mocking data
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        // Testing
        assertTrue(emailBean.sendPasswordResetEmail(user));
    }

}

