package WSockets;

import static org.mockito.Mockito.*;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import project5.bean.UserBean;
import project5.dao.UserDao;
import project5.entity.UserEntity;
import project5.websocket.WSNotifications;

import java.io.IOException;
import java.util.HashMap;

public class WSNotificationsTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserBean userBean;

    @InjectMocks
    private WSNotifications wsNotifications;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testToDoOnOpen_WithValidToken() {
        // Mocking
        Session session = mock(Session.class);
        HashMap<String, String> pathParameters = new HashMap<>();
        pathParameters.put("token", "valid_token");
        when(session.getPathParameters()).thenReturn(pathParameters);
        UserEntity receiver = new UserEntity();
        when(userDao.findUserByToken("valid_token")).thenReturn(receiver);

        // Testing
        wsNotifications.toDoOnOpen(session, "valid_token");

        // Verification
        verify(userDao).findUserByToken("valid_token");
    }


    @Test
    public void testToDoOnMessage() throws IOException {
        // Mocking
        Session session = mock(Session.class);
        RemoteEndpoint.Basic remoteEndpoint = mock(RemoteEndpoint.Basic.class);
        when(session.getBasicRemote()).thenReturn(remoteEndpoint);

        // Testing
        wsNotifications.toDoOnMessage(session, "Test notification", "token123");

        // Verification
        verifyNoInteractions(remoteEndpoint);
    }


    @Test
    public void testSend_WithNoSession() throws IOException {
        // Testing
        wsNotifications.send("user1", "Test notification");
    }

}
