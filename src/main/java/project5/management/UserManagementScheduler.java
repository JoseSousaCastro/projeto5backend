package project5.management;

import project5.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserManagementScheduler {

    private static final long serialVersionUID = 1L;


    // Crie um executor de tarefas agendadas com uma thread
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Método para iniciar a tarefa agendada
    public void startScheduler() {
        // Agende a tarefa para ser executada a cada hora
        scheduler.scheduleAtFixedRate(this::deleteExpiredUsersTask, 0, 1, TimeUnit.HOURS);
    }

    // Método para deletar usuários expirados
    private void deleteExpiredUsersTask() {
        // Obtenha a lista de usuários
        List<User> users = new ArrayList<>();
        // Para cada usuário na lista
        for (User user : users) {
            // Se o usuário estiver expirado
            if (user.getExpirationTime() > System.currentTimeMillis()) {
                // Remova o usuário
                users.remove(user);
            }
        }
    }

    // Método para parar o agendador
    public void stopScheduler() {
        // Pare o agendador
        scheduler.shutdown();
    }

    // Método principal para iniciar o agendador
    public static void main(String[] args) {
        // Crie uma instância do agendador
        UserManagementScheduler scheduler = new UserManagementScheduler();
        // Inicie o agendador
        scheduler.startScheduler();

        // Quando a aplicação for encerrada, pare o agendador
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stopScheduler));
    }
}

