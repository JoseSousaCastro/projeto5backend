package WSockets;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.websocket.CloseReason;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import project5.bean.ChatBean;
import project5.bean.UserBean;
import project5.dao.UserDao;
import project5.dto.ChatMessage;
import project5.dto.ChatNotification;
import project5.entity.UserEntity;
import project5.websocket.WSChat;
import project5.websocket.WSNotifications;

public class WSChatTest {

    @Mock
    private ChatBean chatBean;

    @Mock
    private UserDao userDao;

    @Mock
    private UserBean userBean;

    @Mock
    private WSNotifications wsNotifications;

    @InjectMocks
    private WSChat wsChat;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testToDoOnClose() {
        // Mocking
        Session session = mock(Session.class);
        CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Test close");

        // Testing
        wsChat.toDoOnClose(session, closeReason);
        // No need to verify anything since no interactions with mocks
    }

    @Test
    public void testToDoOnMessage_SenderNotFound() throws IOException {
        // Mocking
        Session session = mock(Session.class);
        when(session.getPathParameters()).thenReturn(new HashMap<>());
        when(userDao.findUserByToken(anyString())).thenReturn(null);

        // Testing
        wsChat.toDoOnMessage(session, "Test", "token123", "receiver123");
        // No need to verify anything since no interactions with mocks
    }

    @Test
    public void testToDoOnMessage_ReceiverNotFound() throws IOException {
        // Mocking
        Session session = mock(Session.class);
        when(session.getPathParameters()).thenReturn(new HashMap<>());
        UserEntity sender = new UserEntity();
        when(userDao.findUserByToken(anyString())).thenReturn(sender);
        when(userDao.findUserByUsername(anyString())).thenReturn(null);

        // Testing
        wsChat.toDoOnMessage(session, "Test", "token123", "receiver123");
        // No need to verify anything since no interactions with mocks
    }


    @Test
    public void testToDoOnMessage_WithSenderNotFound() throws IOException {
        // Mocking
        Session session = mock(Session.class);
        when(session.getPathParameters()).thenReturn(new HashMap<>());
        when(userDao.findUserByToken(anyString())).thenReturn(null);

        // Testing
        wsChat.toDoOnMessage(session, "Test", "invalid_token", "receiver123");

        // Verification
        verifyNoInteractions(chatBean);
    }

    @Test
    public void testToDoOnMessage_WithReceiverNotFound() throws IOException {
        // Mocking
        Session session = mock(Session.class);
        when(session.getPathParameters()).thenReturn(new HashMap<>());
        UserEntity sender = new UserEntity();
        when(userDao.findUserByToken(anyString())).thenReturn(sender);
        when(userDao.findUserByUsername(anyString())).thenReturn(null);

        // Testing
        wsChat.toDoOnMessage(session, "Test", "token123", "invalid_receiver");

        // Verification
        verifyNoInteractions(chatBean);
    }
}

