package project5.management;

import project5.bean.CategoryBean;
import project5.bean.UserBean;
import project5.dto.User;

import java.util.ArrayList;
import org.apache.logging.log4j.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserManagementScheduler {

    private UserBean userBean;
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(CategoryBean.class);

    public UserManagementScheduler(UserBean userBean) {
        this.userBean = userBean;
    }


    // Crie um executor de tarefas agendadas com uma thread
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Método para iniciar a tarefa agendada
    public void startScheduler() {
        // Agende a tarefa para ser executada a cada hora
        scheduler.scheduleAtFixedRate(this::deleteExpiredUsersTask, 0, 1, TimeUnit.HOURS);
        logger.info("UserManagementScheduler started");
    }

    // Método para deletar usuários expirados
    private void deleteExpiredUsersTask() {
        // Obtenha a lista de usuários
        List<User> users = userBean.getUsers(); // Substitua getUsers() pelo método que obtém a lista de usuários
        logger.info("Checking for expired users");
        // Crie uma lista para armazenar os usuários expirados a serem removidos
        List<User> usersToRemove = new ArrayList<>();
        logger.info("Users found: " + users.size());
        // Para cada usuário na lista
        for (User user : users) {
            logger.info("Checking user: " + user.getUsername());
            // Se o usuário estiver expirado
            if (user.getExpirationTime() < System.currentTimeMillis()) {
                logger.info("User expired: " + user.getUsername());
                // Adicione o usuário à lista de usuários a serem removidos
                usersToRemove.add(user);
                logger.info("User added to removal list: " + user.getUsername());
            }
        }
        // Remova os usuários expirados da lista original de usuários
        users.removeAll(usersToRemove);
        logger.info("Users removed: " + usersToRemove.size());
    }



    // Método para parar o agendador
    public void stopScheduler() {
        // Pare o agendador
        scheduler.shutdown();
        logger.info("UserManagementScheduler stopped");
    }

    // Método principal para iniciar o agendador
    public static void main(String[] args) {
        // Crie uma instância do agendador
        UserManagementScheduler scheduler = new UserManagementScheduler(new UserBean());
        // Inicie o agendador
        scheduler.startScheduler();

        // Quando a aplicação for encerrada, pare o agendador
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stopScheduler));
    }
}

