package project5.bean;

import project5.dao.TaskDao;
import project5.dao.UserDao;
import project5.dto.LoggedUser;
import project5.dto.Login;
import project5.dto.Task;
import project5.dto.User;
import project5.entity.TaskEntity;
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

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(UserBean.class);

    //Construtor vazio
    public UserBean() {
    }

    public UserBean(UserDao userDao) {
        this.userDao = userDao;
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

            register(deletedUser);
        }
        UserEntity userEntity3 = userDao.findUserByUsername("Comendador");
        if (userEntity3 == null) {
            User Comendador = new User();
            Comendador.setUsername("Comendador");
            Comendador.setPassword("pass123");
            Comendador.setEmail("josemscastro@gmail.com");
            Comendador.setFirstName("Comendador");
            Comendador.setLastName("Tuca");
            Comendador.setPhone("917896541");
            Comendador.setPhotoURL("https://static.wixstatic.com/media/8d11e2_4c9abf55944042c8b26565c72d5e0afb~mv2.png/v1/crop/x_0,y_23,w_245,h_200/fill/w_245,h_200,al_c,q_85,enc_auto/Comendador%20Tuca.png");
            Comendador.setTypeOfUser(300);
            Comendador.setVisible(true);
            Comendador.setConfirmed(true);
            Comendador.setCreationDate(LocalDate.of(2024, 1, 1));

            register(Comendador);
        }
        UserEntity userEntity4 = userDao.findUserByUsername("Conde");
        if (userEntity4 == null) {
            User Conde = new User();
            Conde.setUsername("Conde");
            Conde.setPassword("pass123");
            Conde.setEmail("mail.do.castro@gmail.com");
            Conde.setFirstName("Conde");
            Conde.setLastName("Sottomayor");
            Conde.setPhone("901234567");
            Conde.setPhotoURL("https://static.wixstatic.com/media/8d11e2_5e87201b41064678a5ba5391efb549da.png/v1/fill/w_159,h_190,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/8d11e2_5e87201b41064678a5ba5391efb549da.png");
            Conde.setTypeOfUser(200);
            Conde.setVisible(true);
            Conde.setConfirmed(true);
            Conde.setCreationDate(LocalDate.of(2024, 2, 1));

            register(Conde);
        }
        UserEntity userEntity5 = userDao.findUserByUsername("Juvenal");
        if (userEntity5 == null) {
            User Juvenal = new User();
            Juvenal.setUsername("pass123");
            Juvenal.setPassword("123");
            Juvenal.setEmail("xepik21871@rartg.com");
            Juvenal.setFirstName("Juvenal");
            Juvenal.setLastName("Anao");
            Juvenal.setPhone("937418520");
            Juvenal.setPhotoURL("https://static.wixstatic.com/media/8d11e2_426a9fa64bfe4502a9729c87a99cf500.png/v1/fill/w_200,h_168,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/8d11e2_426a9fa64bfe4502a9729c87a99cf500.png");
            Juvenal.setTypeOfUser(100);
            Juvenal.setVisible(true);
            Juvenal.setConfirmed(true);
            Juvenal.setCreationDate(LocalDate.of(2024, 4, 1));

            register(Juvenal);
        }
        UserEntity userEntity6 = userDao.findUserByUsername("Professor");
        if (userEntity6 == null) {
            User Professor = new User();
            Professor.setUsername("Professor");
            Professor.setPassword("pass123");
            Professor.setEmail("jennica52@ynmerchant.com");
            Professor.setFirstName("Professor");
            Professor.setLastName("Mutumbu");
            Professor.setPhone("931478520");
            Professor.setPhotoURL("https://static.wixstatic.com/media/8d11e2_984ac291b0ba488692aafb79ae4163fb.png/v1/fill/w_185,h_190,al_c,q_85,enc_auto/8d11e2_984ac291b0ba488692aafb79ae4163fb.png");
            Professor.setTypeOfUser(100);
            Professor.setVisible(true);
            Professor.setConfirmed(true);
            Professor.setCreationDate(LocalDate.of(2024, 4, 1));

            register(Professor);
        }
        UserEntity userEntity7 = userDao.findUserByUsername("Rodrigues");
        if (userEntity7 == null) {
            User Rodrigues = new User();
            Rodrigues.setUsername("Rodrigues");
            Rodrigues.setPassword("pass123");
            Rodrigues.setEmail("jose.sousacastro@gmail.com");
            Rodrigues.setFirstName("Rodrigues");
            Rodrigues.setLastName("Fotografo");
            Rodrigues.setPhone("969638520");
            Rodrigues.setPhotoURL("https://static.wixstatic.com/media/8d11e2_532c2a0fd60e471caef67ab4b89d8737~mv2.png/v1/crop/x_45,y_1,w_285,h_190/fill/w_254,h_170,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/8d11e2_532c2a0fd60e471caef67ab4b89d8737~mv2.png");
            Rodrigues.setTypeOfUser(100);
            Rodrigues.setVisible(true);
            Rodrigues.setConfirmed(true);
            Rodrigues.setCreationDate(LocalDate.of(2024, 4, 1));

            register(Rodrigues);
        }
        UserEntity userEntity8 = userDao.findUserByUsername("Svetlana");
        if (userEntity8 == null) {
            User Svetlana = new User();
            Svetlana.setUsername("Svetlana");
            Svetlana.setPassword("pass123");
            Svetlana.setEmail("tucatucatuca.pt@gmail.com");
            Svetlana.setFirstName("Svetlana");
            Svetlana.setLastName("Kondeixanova");
            Svetlana.setPhone("967418520");
            Svetlana.setPhotoURL("https://static.wixstatic.com/media/8d11e2_1cbe099d2bfe4a54a6de5b5438ed804a~mv2.png/v1/crop/x_88,y_14,w_475,h_451/fill/w_221,h_210,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/Svetlana%20Kondeixanova%20-%20sem%20fundo.png");
            Svetlana.setTypeOfUser(200);
            Svetlana.setVisible(true);
            Svetlana.setConfirmed(true);
            Svetlana.setCreationDate(LocalDate.of(2024, 2, 1));

            register(Svetlana);
        }
        UserEntity userEntity9 = userDao.findUserByUsername("Tobedeleted");
        if (userEntity9 == null) {
            User Tobedeleted = new User();
            Tobedeleted.setUsername("Tobedeleted");
            Tobedeleted.setPassword("pass123");
            Tobedeleted.setEmail("ufekevy@mailto.plus");
            Tobedeleted.setFirstName("Tobe");
            Tobedeleted.setLastName("Deleted");
            Tobedeleted.setPhone("907894561");
            Tobedeleted.setPhotoURL("https://images.freeimages.com/images/large-previews/023/geek-avatar-1632962.jpg");
            Tobedeleted.setTypeOfUser(100);
            Tobedeleted.setVisible(true);
            Tobedeleted.setConfirmed(true);
            Tobedeleted.setCreationDate(LocalDate.of(2024, 3, 1));

            register(Tobedeleted);
        }
    }

    //Permite ao utilizador entrar na app, gera token
    public LoggedUser login(Login user) {
        UserEntity userEntity = userDao.findUserByUsername(user.getUsername());
        if (userEntity != null && userEntity.isVisible()) {
            //Verifica se a password coincide com a password encriptada
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

                //Encripta a password usando BCrypt
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

                //Define a password encriptada
                user.setPassword(hashedPassword);

                //Persist o user
                userDao.persist(convertUserDtotoUserEntity(user));

                return true;
            } else {
                if (user.getUsername().equals("admin")) {
                    user.setTypeOfUser(300);
                } else {
                    user.setInitialTypeOfUser();
                }

                user.setVisible(true);

                //Encripta a password usando BCrypt
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

                //Define a password encriptada
                user.setPassword(hashedPassword);

                //Persist o user
                userDao.persist(convertUserDtotoUserEntity(user));
                wsDashboard.send("stats have been changed");
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
        return base64Encoder.encodeToString(randomBytes);
    }


    //Logout
    public boolean logout(String token) {
        UserEntity u = userDao.findUserByToken(token);

        if (u != null) {
            u.setToken(null);
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

    /*public boolean addUser(User user) {

        boolean status = false;
        if (users.add(user)) {
            status = true;
        }
        writeIntoJsonFile();
        return status;
    }*/

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

        // Busca o user pelo username
        UserEntity u = userDao.findUserByUsername(username);

        if (u != null && u.getUsername().equals(username)) {

            // Verifica se o email no objeto User é nulo ou vazio
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                // Se não for nulo nem vazio, atualiza o email
                u.setEmail(user.getEmail());
            }

            // Verifica se o contacto no objeto User é nulo ou vazio
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                // Se não for nulo nem vazio, atualiza o contacto
                u.setPhone(user.getPhone());
            }

            // Verifica se o primeiro nome no objeto User é nulo ou vazio
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                // Se não for nulo nem vazio, atualiza o primeiro nome
                u.setFirstName(user.getFirstName());
            }

            // Verifica se o apelido no objeto User é nulo ou vazio
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                // Se não for nulo nem vazio, atualiza o apelido
                u.setLastName(user.getLastName());
            }

            // Verifica se a foto no objeto User é nulo ou vazio
            if (user.getPhotoURL() != null && !user.getPhotoURL().isEmpty()) {
                // Se não for nulo nem vazio, atualiza a foto
                u.setPhotoURL(user.getPhotoURL());
            }

            // Verifica se o typeOfUser no objeto User é nulo ou vazio
            if (user.getTypeOfUser() != 0) {
                // Se não for nulo nem vazio, atualiza a foto
                u.setTypeOfUser(user.getTypeOfUser());
            }

            try {
                userDao.merge(u); //Atualiza o user na base de dados
                wsDashboard.send("stats have been changed");
                status = true;
            } catch (Exception e) {
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

            status = true;
        }

        return status;
    }

    public boolean updateUserEntityRole(String username, int typeOfUser) {
        boolean status = false;

        UserEntity u = userDao.findUserByUsername(username);

        if (u != null && u.getTypeOfUser() != typeOfUser) {

            u.setTypeOfUser(typeOfUser);
            wsDashboard.send("stats have been changed");

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
        // Use a regular expression to perform email format validation
        // This regex is a basic example and may need to be adjusted
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

        //Se for null é porque não houve nenhuma atualização
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            return true;
        }

        UserEntity u = userDao.findUserByEmail(user.getEmail());
        // Check if the email format is valid
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

        //Se existir contacto na base de dados retorna false
        if (u != null) {
            status = false;
        }

        return status;
    }

    public boolean isPhoneNumberUpdatedValid(User user) {
        boolean status = true;

        //Se for null é porque não houve nenhuma atualização
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

        //Se existir contacto na base de dados retorna false
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

        //Se for null é porque não houve nenhuma alteração
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
        //Retorna uma lista vazia se não forem encontradas tarefas
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

/*    public boolean addTaskToUser(String username, Task temporaryTask) {
        TaskBean taskBean = new TaskBean();
        boolean done = taskBean.newTask(temporaryTask);
        if (done) {
            getUserAndHisTasks(username).add(temporaryTask);
        }
        return done;
    }*/

    /*public boolean updateTask(String username, Task task) {
        TaskBean taskBean = new TaskBean();
        boolean updated = false;

        if (taskBean.editTask(task, getUserAndHisTasks(username))) {
            //writeIntoJsonFile();
            updated = true;
        }
        return updated;
    }*/

    //Chamar método no Bean


    //Converte a Entidade com o token "token" para DTO
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
            //Encripta a password usando BCrypt
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            //Define a password encriptada
            user.setPassword(hashedPassword);
            user.setExpirationTime(0);
            user.setConfirmed(true);
            user.setCreationDate(LocalDate.now());
            return true;
        }
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
}