package project5.management;

import project5.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserManagementScheduler {

    // Crie um executor de tarefas agendadas com uma thread
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Método para iniciar a tarefa agendada
    public void startScheduler() {
        // Agende a tarefa para ser executada a cada hora
        scheduler.scheduleAtFixedRate(this::deleteExpiredUsersTask, 0, 1, TimeUnit.HOURS);
    }

    // Método que será executado periodicamente para eliminar usuários expirados
    private void deleteExpiredUsersTask() {
        try {
            // Obter a lista de usuários do banco de dados
            List<User> users = userService.getAllUsers();
            // Iterar sobre a lista de usuários expirados e excluí-los
            for (User user : expiredUsers) {
                userService.deleteUser(user.getUsername());
                System.out.println("User with ID " + user.getUsername() + " has been deleted.");
            }

            System.out.println("Scheduled task to delete expired users executed successfully.");
        } catch (Exception e) {
            // Em caso de erro, registre a exceção
            System.err.println("Error executing scheduled task: " + e.getMessage());
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

        // Mantenha a aplicação em execução
        // Você pode adicionar outras lógicas de aplicação aqui, se necessário

        // Quando a aplicação for encerrada, pare o agendador
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stopScheduler));
    }
}

