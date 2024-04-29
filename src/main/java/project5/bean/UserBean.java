package project5.bean;

import project5.dao.TaskDao;
import project5.dao.TokenExpirationDao;
import project5.dao.UserDao;
import project5.dto.LoggedUser;
import project5.dto.Login;
import project5.dto.Task;
import project5.dto.User;
import project5.entity.TaskEntity;
import project5.entity.TokenExpirationEntity;
import project5.entity.UserEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.mindrot.jbcrypt.BCrypt;
import project5.websocket.WSDashboard;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.logging.log4j.*;

@Stateless
public class UserBean implements Serializable {

    @EJB
    private UserDao userDao;
    @EJB
    private TaskDao taskDao;
    @EJB
    private CategoryBean categoryBean;
    @EJB
    private WSDashboard wsDashboard;
    @EJB
    private TokenExpirationDao tokenExpirationDao;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(UserBean.class);

    //Construtor vazio
    public UserBean() {
    }

    public UserBean(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public void createDefaultUsersIfNotExistent() {
        UserEntity userEntity = userDao.findUserByUsername("admin");
        if (userEntity == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setEmail("admin@admin.com");
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setPhone("123456789");
            admin.setPhotoURL("https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png");
            admin.setVisible(true);
            admin.setConfirmed(true);
            admin.setTypeOfUser(300);
            admin.setCreationDate(LocalDate.of(2024, 1, 1));
            admin.setTokenExpirationTime(0);

            register(admin);
        }

        UserEntity userEntity2 = userDao.findUserByUsername("NOTASSIGNED");
        if (userEntity2 == null) {
            User deletedUser = new User();
            deletedUser.setUsername("NOTASSIGNED");
            deletedUser.setPassword("123");
            deletedUser.setEmail("deleted@user.com");
            deletedUser.setFirstName("Deleted");
            deletedUser.setLastName("User");
            deletedUser.setPhone("123456788");
            deletedUser.setPhotoURL("https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png");
            deletedUser.setTypeOfUser(400);
            deletedUser.setVisible(false);
            deletedUser.setConfirmed(true);
            deletedUser.setCreationDate(LocalDate.of(2024, 1, 1));
            deletedUser.setTokenExpirationTime(0);

            register(deletedUser);
        }
    }

    public void createDefaultTokenExpirationIfNotExistent() {
        TokenExpirationEntity tokenExpirationEntity = tokenExpirationDao.findTokenExpirationEntity();
        if (tokenExpirationEntity == null) {
            TokenExpirationEntity tokenExpiration = new TokenExpirationEntity();
            tokenExpiration.setTokenExpirationTime(24 * 60 * 60 * 1000);
            tokenExpirationDao.saveTokenExpirationTime(tokenExpiration);
        }
    }


    //Permite ao utilizador entrar na app, gera token
    public LoggedUser login(Login user) {
        UserEntity userEntity = userDao.findUserByUsername(user.getUsername());
        if (userEntity != null && userEntity.isVisible()) {
            if (BCrypt.checkpw(user.getPassword(), userEntity.getPassword())) {
                String token = generateNewToken();
                userEntity.setToken(token);
                return convertUserEntitytoLoggedUserDto(userEntity);
            }
        }
        return null;
    }

    //Faz o registo do utilizador, adiciona à base de dados
    public boolean register(User user) {
        if (user != null) {
            if (user.getUsername().equalsIgnoreCase("notAssigned")) {
                user.setUsername(user.getUsername().toUpperCase());
                user.setVisible(false);
                user.setTypeOfUser(User.NOTASSIGNED);

                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);
                userDao.persist(convertUserDtotoUserEntity(user));
                return true;
            } else {
                if (user.getUsername().equals("admin")) {
                    user.setTypeOfUser(300);
                } else {
                    user.setInitialTypeOfUser();
                }
                user.setVisible(true);
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);

                userDao.persist(convertUserDtotoUserEntity(user));
                wsDashboard.send("stats have been changed");
                logger.info("User " + user.getUsername() + " has been registered");
                return true;
            }
        } else {
            return false;
        }
    }


    //Apaga todos os registos do utilizador da base de dados
    //Verificar tarefas!!!!!!!
    public boolean delete(String username) {

        UserEntity u = userDao.findUserByUsername(username);

        if (u != null) {
            ArrayList<TaskEntity> tasks = taskDao.findTasksByUser(u);
            UserEntity notAssigned = userDao.findUserByUsername("NOTASSIGNED");

            notAssigned.addNewTasks(tasks);

            for (TaskEntity t : tasks) {
                t.setOwner(notAssigned);
                taskDao.merge(t);
            }
            userDao.remove(u);
            wsDashboard.send("stats have been changed");
            logger.info("User " + username + " has been deleted");
            return true;
        } else
            return false;
    }


    //Métodos de conversão

    public UserEntity convertUserDtotoUserEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(user.getPassword());
        userEntity.setTypeOfUser(user.getTypeOfUser());
        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPhone(user.getPhone());
        userEntity.setPhotoURL(user.getPhotoURL());
        userEntity.setVisible(user.isVisible());
        userEntity.setExpirationTime(user.getExpirationTime());
        userEntity.setConfirmed(user.isConfirmed());
        userEntity.setCreationDate(user.getCreationDate());

        return userEntity;
    }

    public User convertUserEntitytoUserDto(UserEntity userEntity) {
        User user = new User();
        user.setUsername(userEntity.getUsername());
        user.setPassword(userEntity.getPassword());
        user.setTypeOfUser(userEntity.getTypeOfUser());
        user.setEmail(userEntity.getEmail());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setPhone(userEntity.getPhone());
        user.setPhotoURL(userEntity.getPhotoURL());
        user.setVisible(userEntity.isVisible());
        user.setConfirmed(userEntity.isConfirmed());
        user.setExpirationTime(userEntity.getExpirationTime());
        user.setCreationDate(userEntity.getCreationDate());

        return user;
    }

    public LoggedUser convertUserEntitytoLoggedUserDto(UserEntity userEntity) {
        LoggedUser user = new LoggedUser();
        user.setUsername(userEntity.getUsername());
        user.setPassword(userEntity.getPassword());
        user.setTypeOfUser(userEntity.getTypeOfUser());
        user.setEmail(userEntity.getEmail());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setPhone(userEntity.getPhone());
        user.setPhotoURL(userEntity.getPhotoURL());
        user.setVisible(userEntity.isVisible());
        user.setToken(userEntity.getToken());


        return user;
    }

    public Task convertTaskEntitytoTaskDto(TaskEntity taskEntity) {
        Task t = new Task();
        t.setId(taskEntity.getId());
        t.setOwner(convertUserEntitytoUserDto(taskEntity.getOwner()));
        t.setTitle(taskEntity.getTitle());
        t.setDescription(taskEntity.getDescription());
        t.setStateId(taskEntity.getStateId());
        t.setPriority(taskEntity.getPriority());
        t.setStartDate(taskEntity.getStartDate());
        t.setLimitDate(taskEntity.getLimitDate());
        t.setCategory(categoryBean.convertCategoryEntityToCategoryDto(taskEntity.getCategory()));
        t.setErased(taskEntity.getErased());

        return t;
    }


    //Gerar token
    private String generateNewToken() {
        SecureRandom secureRandom = new SecureRandom(); //threadsafe
        Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        logger.info("Token generated");
        // tokenTimeoutDao.saveTokenTimeout(new TokenTimeoutEntity(base64Encoder.encodeToString(randomBytes), System.currentTimeMillis() + 3600000));
        return base64Encoder.encodeToString(randomBytes);
    }


    //Logout
    public boolean logout(String token) {
        UserEntity u = userDao.findUserByToken(token);
        if (u != null) {
            u.setToken(null);
            logger.info("User " + u.getUsername() + " has logged out");
            return true;
        }
        return false;
    }

    public boolean forcedLogout(User user) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        if (u != null) {
            u.setToken(null);
            logger.info("User " + u.getUsername() + " has logged out");
            return true;
        }
        return false;
    }

    public ArrayList<User> getUsers() {

        ArrayList<UserEntity> userEntities = userDao.findAllUsers();
        if (userEntities != null) {
            ArrayList<User> users = new ArrayList<>();
            for (UserEntity userE : userEntities) {

                if (userE.getTypeOfUser() != 400) {
                    users.add(convertUserEntitytoUserDto(userE));
                }
            }
            return users;
        }
        //Retorna uma lista vazia se não forem encontradas tarefas
        return new ArrayList<>();
    }

    //Receber users pelo tipo de user
    public ArrayList<User> getUsersByType(int typeOfUser) {

        ArrayList<UserEntity> userEntities = userDao.findAllUsersByTypeOfUser(typeOfUser);
        if (userEntities != null) {
            ArrayList<User> users = new ArrayList<>();
            for (UserEntity userE : userEntities) {

                users.add(convertUserEntitytoUserDto(userE));

            }
            return users;
        }
        //Retorna uma lista vazia se não forem encontradas tarefas
        return new ArrayList<>();
    }

    //Receber users pelo tipo de visibilidade
    public ArrayList<User> getUsersByVisibility(boolean visible) {

        ArrayList<UserEntity> userEntities = userDao.findAllUsersByVisibility(visible);
        if (userEntities != null) {
            ArrayList<User> users = new ArrayList<>();
            for (UserEntity userE : userEntities) {

                users.add(convertUserEntitytoUserDto(userE));

            }
            return users;
        }
        //Retorna uma lista vazia se não forem encontradas tarefas
        return new ArrayList<>();
    }

    //Receber users pelo tipo de user e de visibilidade
    public ArrayList<User> getUsersByTypeAndVisibility(int typeOfUser, boolean visible) {

        ArrayList<UserEntity> userEntities = userDao.findAllUsersByTypeOfUserAndVisibility(typeOfUser, visible);
        if (userEntities != null) {
            ArrayList<User> users = new ArrayList<>();
            for (UserEntity userE : userEntities) {

                users.add(convertUserEntitytoUserDto(userE));

            }
            return users;
        }
        //Retorna uma lista vazia se não forem encontradas tarefas
        return new ArrayList<>();
    }

    public User getUser(String username) {

        UserEntity u = userDao.findUserByUsername(username);

        if (u != null) {
            return convertUserEntitytoUserDto(u);
        }

        return null;
    }

    //Coloco username porque no objeto de atualização não está referenciado
    public boolean updateUser(User user, String username) {
        boolean status = false;
        UserEntity u = userDao.findUserByUsername(username);
        if (u != null && u.getUsername().equals(username)) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                u.setEmail(user.getEmail());
                logger.info("User " + u.getUsername() + " has changed his email to " + user.getEmail());
            }
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                u.setPhone(user.getPhone());
                logger.info("User " + u.getUsername() + " has changed his phone to " + user.getPhone());
            }
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                u.setFirstName(user.getFirstName());
                logger.info("User " + u.getUsername() + " has changed his first name to " + user.getFirstName());
            }
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                u.setLastName(user.getLastName());
                logger.info("User " + u.getUsername() + " has changed his last name to " + user.getLastName());
            }
            if (user.getPhotoURL() != null && !user.getPhotoURL().isEmpty()) {
                u.setPhotoURL(user.getPhotoURL());
                logger.info("User " + u.getUsername() + " has changed his photo to " + user.getPhotoURL());
            }
            if (user.getTypeOfUser() != 0) {
                u.setTypeOfUser(user.getTypeOfUser());
                logger.info("User " + u.getUsername() + " has changed his type of user to " + user.getTypeOfUser());
            }
            if (user.getTokenExpirationTime() != 0) {
                u.setTokenExpirationTime(user.getTokenExpirationTime());
                logger.info("User " + u.getUsername() + " has changed his token expiration time to " + user.getTokenExpirationTime());
            }
            try {
                userDao.merge(u); //Atualiza o user na base de dados
                logger.info("User " + u.getUsername() + " has been updated");
                wsDashboard.send("stats have been changed");
                status = true;
            } catch (Exception e) {
                logger.error("User " + u.getUsername() + " could not be updated");
                e.printStackTrace();
                status = false;
            }
        }
        return status;
    }

    public boolean updateUserEntityVisibility(String username) {
        boolean status = false;
        UserEntity u = userDao.findUserByUsername(username);
        if (u != null) {
            u.setVisible(!u.isVisible());
            wsDashboard.send("stats have been changed");
            logger.info("User " + u.getUsername() + " has changed his visibility to " + u.isVisible());
            status = true;
        }
        logger.error("User " + u.getUsername() + " could not be updated");
        return status;
    }

    public boolean updateUserEntityRole(String username, int typeOfUser) {
        boolean status = false;
        UserEntity u = userDao.findUserByUsername(username);
        if (u != null && u.getTypeOfUser() != typeOfUser) {
            u.setTypeOfUser(typeOfUser);
            wsDashboard.send("stats have been changed");
            logger.info("User " + u.getUsername() + " has changed his role to " + u.getTypeOfUser());
            status = true;
        }
        return status;
    }

    public boolean isAuthenticated(String token) {
        boolean validUser = false;
        UserEntity user = userDao.findUserByToken(token);
        if (user != null && user.isVisible()) {
            validUser = true;
        }
        return validUser;
    }


    public boolean isUsernameAvailable(User user) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        boolean status = false;
        if (u == null) {
            status = true;
        }
        return status;
    }

    private boolean isEmailFormatValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public boolean isEmailValid(User user) {
        UserEntity u = userDao.findUserByEmail(user.getEmail());
        // Check if the email format is valid
        if (isEmailFormatValid(user.getEmail()) && u == null) {
            return true;
        }
        return false;
    }

    public boolean isEmailUpdatedValid(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            return true;
        }
        UserEntity u = userDao.findUserByEmail(user.getEmail());
        if ((isEmailFormatValid(user.getEmail()) && u == null) || (u != null && u.getEmail().equals(user.getEmail()))) {
            return true;
        }
        return false;
    }


    public boolean isAnyFieldEmpty(User user) {
        boolean status = false;

        if (user.getUsername().isEmpty() ||
                user.getEmail().isEmpty() ||
                user.getFirstName().isEmpty() ||
                user.getLastName().isEmpty() ||
                user.getPhone().isEmpty() ||
                user.getPhotoURL().isEmpty()) {
            status = true;
        }
        return status;
    }

    public boolean isPhoneNumberValid(User user) {
        boolean status = true;
        int i = 0;

        UserEntity u = userDao.findUserByPhone(user.getPhone());
        while (status && i < user.getPhone().length() - 1) {
            if (user.getPhone().length() == 9) {
                for (; i < user.getPhone().length(); i++) {
                    if (!Character.isDigit(user.getPhone().charAt(i))) {
                        status = false;
                    }
                }
            } else {
                status = false;
            }
        }
        if (u != null) {
            status = false;
        }
        return status;
    }

    public boolean isPhoneNumberUpdatedValid(User user) {
        boolean status = true;

        if (user.getPhone() == null) {
            return true;
        }
        int i = 0;

        UserEntity u = userDao.findUserByPhone(user.getPhone());
        while (status && i < user.getPhone().length() - 1) {
            if (user.getPhone().length() == 9) {
                for (; i < user.getPhone().length(); i++) {
                    if (!Character.isDigit(user.getPhone().charAt(i))) {
                        status = false;
                    }
                }
            } else {
                status = false;
            }
        }
        if (u != null) {
            status = false;
        }
        return status;
    }

    public boolean isImageUrlValid(String url) {
        boolean status = true;

        if (url == null) {
            status = false;
        }
        try {
            BufferedImage img = ImageIO.read(new URL(url));
            if (img == null) {
                status = false;
            }
        } catch (IOException e) {
            status = false;
        }
        return status;
    }

    public boolean isImageUrlUpdatedValid(String url) {
        boolean status = true;

        if (url == null) {
            return true;
        }
        try {
            BufferedImage img = ImageIO.read(new URL(url));
            if (img == null) {
                status = false;
            }
        } catch (IOException e) {
            status = false;
        }
        return status;
    }


    public ArrayList<Task> getUserAndHisTasks(String username) {
        UserEntity u = userDao.findUserByUsername(username);
        if (u != null) {
            ArrayList<TaskEntity> taskEntities = taskDao.findTasksByUser(u);
            if (taskEntities != null) {
                ArrayList<Task> userTasks = new ArrayList<>();
                for (TaskEntity taskEntity : taskEntities) {
                    userTasks.add(convertTaskEntitytoTaskDto(taskEntity));
                }
                return userTasks;
            }
        }
        return new ArrayList<>();
    }

    public boolean userIsTaskOwner(String token, String id) {
        UserEntity userEntity = userDao.findUserByToken(token);
        TaskEntity taskEntity = taskDao.findTaskById(id);
        boolean authorized = false;
        if (userEntity != null) {
            if (taskEntity.getOwner().getUsername().equals(userEntity.getUsername())) {
                authorized = true;
            }
        }
        return authorized;
    }

    public boolean userIsDeveloper(String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        boolean authorized = false;
        if (userEntity != null) {
            if (userEntity.getTypeOfUser() == User.DEVELOPER) {
                authorized = true;
            }
        }
        return authorized;
    }

    public boolean userIsScrumMaster(String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        boolean authorized = false;
        if (userEntity != null) {
            if (userEntity.getTypeOfUser() == User.SCRUMMASTER) {
                authorized = true;
            }
        }
        return authorized;
    }

    public boolean userIsProductOwner(String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        boolean authorized = false;
        if (userEntity != null) {
            if (userEntity.getTypeOfUser() == User.PRODUCTOWNER) {
                authorized = true;
            }
        }
        return authorized;
    }


    public User convertEntityByToken(String token) {

        UserEntity currentUserEntity = userDao.findUserByToken(token);
        User currentUser = convertUserEntitytoUserDto(currentUserEntity);
        if (currentUser != null) {
            return currentUser;
        } else return null;

    }

    //Converte a Entidade com o email "email" para DTO
    public User convertEntityByEmail(String email) {

        UserEntity userEntity = userDao.findUserByEmail(email);
        User user = convertUserEntitytoUserDto(userEntity);
        if (user != null) {
            return user;
        } else return null;

    }

    public boolean thisTokenIsFromThisUsername(String token, String username) {
        if (userDao.findUserByToken(token).getUsername().equals(username)) {
            return true;
        } else return false;

    }

    public boolean verifyOldPassword(String username, String oldPassword) {
        UserEntity user = userDao.findUserByUsername(username);
        if (user != null) {
            return BCrypt.checkpw(oldPassword, user.getPassword());
        }
        return false;
    }

    public boolean updatePassword(String username, String newPassword) {
        UserEntity user = userDao.findUserByUsername(username);
        if (user != null) {
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.setExpirationTime(0);
            user.setConfirmed(true);
            user.setCreationDate(LocalDate.now());
            logger.info("User " + user.getUsername() + " has changed his password");
            return true;
        }
        logger.error("User " + user.getUsername() + " could not change his password");
        return false;
    }

    public boolean confirmUser(UserEntity user) {
        if (!user.isConfirmed()) {
            return false;
        }
        userDao.merge(user);
        return true;
    }

    public User getUserByEmail(String email) {
        UserEntity userEntity = userDao.findUserByEmail(email);
        if (userEntity != null) {
            return convertUserEntitytoUserDto(userEntity);
        }
        return null;
    }

    public User findUserByToken(String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            return convertUserEntitytoUserDto(userEntity);
        }
        return null;
    }

    public void createTokenTimeoutForUser(UserEntity user) {
        TokenExpirationEntity tokenExpirationEntity = tokenExpirationDao.findTokenExpirationEntity();
        long currentTime = System.currentTimeMillis();
        user.setTokenExpirationTime(currentTime + tokenExpirationEntity.getTokenExpirationTime());
        User userDto = convertUserEntitytoUserDto(user);
        updateUser(userDto, user.getUsername());
        logger.info("User " + user.getUsername() + " has created a token expiration time. Token expires in " + tokenExpirationEntity.getTokenExpirationTime() + " milliseconds");
    }

    public void updateTokenExpirationTime(UserEntity user) {
        if (user != null) {
            TokenExpirationEntity tokenExpirationEntity = tokenExpirationDao.findTokenExpirationEntity();
            long currentTime = System.currentTimeMillis();
            user.setTokenExpirationTime(currentTime + tokenExpirationEntity.getTokenExpirationTime());
            User userDto = convertUserEntitytoUserDto(user);
            updateUser(userDto, user.getUsername());
            logger.info("User " + user.getUsername() + " has updated his token expiration time");
        } else {
            logger.error("Not update his token expiration time");
        }
    }

    public boolean isTokenExpired(UserEntity user) {
        if (user != null) {
            long currentTime = System.currentTimeMillis();
            logger.info("User " + user.getUsername() + " has an expired token");
            String token = user.getToken();
            logout(token);
            return currentTime > user.getTokenExpirationTime();
        } else {
            logger.error("User " + user.getUsername() + " user does not have an expired token");
            return false;
        }
    }

}