package Beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import project5.bean.UserBean;
import project5.dao.TaskDao;
import project5.dao.TokenExpirationDao;
import project5.dao.UserDao;
import project5.dto.LoggedUser;
import project5.dto.Login;
import project5.dto.User;
import project5.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserBeanTest {

    @Mock
    private UserDao userDao;

    @Mock
    private TaskDao taskDao;

    @Mock
    private TokenExpirationDao tokenExpirationDao;

    @InjectMocks
    private UserBean userBean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userBean.setTaskDao(taskDao);
    }

    // Teste para login com usuário inválido
    @Test
    void testLoginWithInvalidUser() {
        // Mocking data
        Login login = new Login();
        login.setUsername("invalidUsername");
        login.setPassword("validPassword");

        when(userDao.findUserByUsername("invalidUsername")).thenReturn(null);

        // Testing
        LoggedUser loggedUser = userBean.login(login);
        assertNull(loggedUser);
    }

    // Teste para login com senha inválida
    @Test
    void testLoginWithInvalidPassword() {
        // Mocking data
        Login login = new Login();
        login.setUsername("validUsername");
        login.setPassword("invalidPassword");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("validUsername");
        userEntity.setPassword("$2a$10$GAYUhBnZ3Mw6Mm3cCRR/JeMBAY28K41CqHM1Gj6gEiKvXUpmY0T6q");

        when(userDao.findUserByUsername("validUsername")).thenReturn(userEntity);

        // Testing
        LoggedUser loggedUser = userBean.login(login);
        assertNull(loggedUser);
    }

    // Teste para registro bem-sucedido
    @Test
    void testUpdateUserWithInvalidInformation() {
        // Mocking data
        User user = new User();
        user.setUsername("validUsername");
        user.setEmail("existingEmail@example.com");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("validUsername");

        UserEntity existingUserEntity = new UserEntity();
        existingUserEntity.setUsername("existingUsername");
        existingUserEntity.setEmail("existingEmail@example.com");

        when(userDao.findUserByUsername("validUsername")).thenReturn(userEntity);
        when(userDao.findUserByEmail("existingEmail@example.com")).thenReturn(existingUserEntity);

        // Testing
        assertFalse(userBean.updateUser(user, "validUsername"));
    }

    // Teste para logout bem-sucedido
    @Test
    void testLogoutSuccessful() {
        // Mocking data
        UserEntity userEntity = new UserEntity();
        userEntity.setToken("validToken");

        when(userDao.findUserByToken("validToken")).thenReturn(userEntity);

        // Testing
        assertTrue(userBean.logout("validToken"));
    }

}