package WSockets;

import static org.mockito.Mockito.*;

import jakarta.websocket.CloseReason;
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
import project5.websocket.WSDashboard;

import java.io.IOException;
import java.util.HashMap;

public class WSDashboardTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserBean userBean;

    @InjectMocks
    private WSDashboard wsDashboard;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testToDoOnOpen() {
        // Mocking
        Session session = mock(Session.class);
        HashMap<String, String> pathParameters = new HashMap<>();
        pathParameters.put("token", "valid_token");
        when(session.getPathParameters()).thenReturn(pathParameters);
        UserEntity receiver = new UserEntity();
        when(userDao.findUserByToken("valid_token")).thenReturn(receiver);

        // Testing
        wsDashboard.toDoOnOpen(session, "valid_token");

        // Verification
        verify(userDao).findUserByToken("valid_token");
    }

}

