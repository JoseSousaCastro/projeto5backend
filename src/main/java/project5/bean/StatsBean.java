package project5.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import project5.dao.CategoryDao;
import project5.dao.TaskDao;
import project5.dao.UserDao;
import project5.dto.Category;
import project5.dto.RegistInfoCategory;
import project5.dto.RegistInfoTask;
import project5.dto.RegistInfoUser;
import project5.entity.CategoryEntity;
import project5.entity.TaskEntity;
import project5.entity.UserEntity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Stateless
public class StatsBean implements Serializable {

    @EJB
    private TaskDao taskDao;
    @EJB
    private CategoryDao categoryDao;
    @EJB
    private UserDao userDao;
    @EJB
    private UserBean userBean;
    @EJB
    private CategoryBean categoryBean;
    @EJB
    private TaskBean taskBean;


    // Users Stats
    public int getNumberOfUsers() {
        return userDao.findAllUsers().size();
    }

    public int getNumberOfConfirmedUsers() {
        return userDao.findAllUsersByIsConfirmed(true).size();
    }

    public int getNumberOfUnconfirmedUsers() {
        return userDao.findAllUsersByIsConfirmed(false).size();
    }


    // Tasks by user

    public int getNumberOfTasksByUser(String username) {
        return taskDao.findTasksNotDeletedNorErasedByUser(userDao.findUserByUsername(username)).size();
    }

    public int getNumberOfTodoTasksByUser(String username) {
        return taskDao.findTasksNotDeletedeNorErsasedByUserAndStateId(userDao.findUserByUsername(username), 100).size();
    }

    public int getNumberOfDoingTasksByUser(String username) {
        return taskDao.findTasksNotDeletedeNorErsasedByUserAndStateId(userDao.findUserByUsername(username), 200).size();
    }

    public int getNumberOfDoneTasksByUser(String username) {
        return taskDao.findTasksNotDeletedeNorErsasedByUserAndStateId(userDao.findUserByUsername(username), 300).size();
    }


    // Tasks Stats

    public int getNumberOfTasks() {
        return taskDao.findAllTasks().size();
    }

    public int getNumberOfTodoTasks() {
        return taskDao.findAllTasksByStateId(100).size();
    }

    public int getNumberOfDoingTasks() {
        return taskDao.findAllTasksByStateId(200).size();
    }

    public int getNumberOfDoneTasks() {
        return taskDao.findAllTasksByStateId(300).size();
    }


    public double getAverageNumberOfTasksPerUser() {
        int totalTasks = taskDao.findAllTasksNotErased().size();
        int totalUsers = userDao.findAllConfirmedAndNotErasedUsers().size();

        // Verifique se o denominador não é zero para evitar uma divisão por zero
        if (totalUsers == 0) {
            return 0; // Retorna 0 se não houver usuários ativos
        }
        double roundedAverage = Math.round((double) totalTasks / totalUsers * 10.0) / 10.0;

        return roundedAverage;
    }


    public double getAverageOfTaskTimes() {
        ArrayList<TaskEntity> tasks = taskDao.findAllCompletedTasks();
        int totalTasks = tasks.size();
        int sum = 0;

        for (TaskEntity task : tasks) {
            sum += (int) ChronoUnit.DAYS.between(task.getStartDate(), task.getDoneDate());
        }

        if (totalTasks == 0) {
            return 0;
        } else
            return sum / totalTasks;
    }


    // Categories Stats
    public ArrayList<RegistInfoCategory> getNumberOfCategoriesFromMostFrequentToLeast() {
        // Obtenha todas as categorias
        ArrayList<Category> allCategories = categoryBean.findAllCategories();

        // Inicialize um mapa para armazenar a contagem de tarefas para cada categoria
        Map<String, Integer> categoryTasksCount = new HashMap<>();

        // Obtenha todas as tarefas
        List<TaskEntity> tasks = taskDao.findAllTasksNotErased();

        // Contabilize o número de tarefas para cada categoria
        for (TaskEntity task : tasks) {
            String category = task.getCategory().getName();
            categoryTasksCount.put(category, categoryTasksCount.getOrDefault(category, 0) + 1);
        }

        // Inicialize a lista de informações de registro de categoria
        ArrayList<RegistInfoCategory> registInfoCategories = new ArrayList<>();

        // Preencha a lista com todas as categorias e suas contagens de tarefas
        for (Category category : allCategories) {
            String categoryName = category.getName();
            int taskCount = categoryTasksCount.getOrDefault(categoryName, 0);
            registInfoCategories.add(new RegistInfoCategory(categoryName, taskCount));
        }

        // Ordene a lista pelo número de tarefas em ordem decrescente
        registInfoCategories.sort((a, b) -> b.getQuantity() - a.getQuantity());

        return registInfoCategories;
    }

    public ArrayList<RegistInfoUser> getSumOfUsersPerMonth() {
        List<UserEntity> users = userDao.findAllUsers();
        ArrayList<RegistInfoUser> registInfoUsers = new ArrayList<>();

        // Inicializar variável para armazenar a contagem cumulativa
        int cumulativeCount = 0;

        for (UserEntity user : users) {
            LocalDate creationDate = user.getCreationDate();
            int month = creationDate.getMonthValue();
            int year = creationDate.getYear();

            // Incrementar a contagem cumulativa
            cumulativeCount++;

            // Verificar se já existe uma entrada para o mês e ano atual
            boolean found = false;
            for (RegistInfoUser registInfoUser : registInfoUsers) {
                if (registInfoUser.getMonth() == month && registInfoUser.getYear() == year) {
                    // Atualizar a contagem cumulativa
                    registInfoUser.setCount(cumulativeCount);
                    found = true;
                    break;
                }
            }

            // Se não foi encontrada uma entrada, adicionar uma nova com a contagem cumulativa
            if (!found) {
                registInfoUsers.add(new RegistInfoUser(month, year, cumulativeCount));
            }
        }

        // Ordenar os dados pela data
        registInfoUsers.sort((a, b) -> {
            // Ordenar por ano primeiro
            int yearComparison = Integer.compare(a.getYear(), b.getYear());
            if (yearComparison != 0) {
                return yearComparison;
            }
            // Se os anos forem iguais, ordenar por mês
            return Integer.compare(a.getMonth(), b.getMonth());
        });

        return registInfoUsers;
    }


    public ArrayList<RegistInfoTask> getSumOfCompletedTasksPerMonth() {
        List<TaskEntity> tasks = taskDao.findAllCompletedTasks();
        ArrayList<RegistInfoTask> registInfoTasks = new ArrayList<>();

        // Criar um mapa para armazenar o número de tarefas concluídas por mês e ano
        Map<String, Integer> taskCounts = new HashMap<>();

        // Iterar sobre as tarefas concluídas e contabilizar o número de tarefas por mês e ano
        for (TaskEntity task : tasks) {
            LocalDate doneDate = task.getDoneDate();
            String monthYearKey = doneDate.getMonthValue() + "/" + doneDate.getYear();
            taskCounts.put(monthYearKey, taskCounts.getOrDefault(monthYearKey, 0) + 1);
        }

        // Converter o mapa em uma lista de RegistInfoTask
        for (Map.Entry<String, Integer> entry : taskCounts.entrySet()) {
            String[] monthYear = entry.getKey().split("/");
            int month = Integer.parseInt(monthYear[0]);
            int year = Integer.parseInt(monthYear[1]);
            int count = entry.getValue();
            registInfoTasks.add(new RegistInfoTask(month, year, count));
        }

        // Ordenar os dados pela data
        registInfoTasks.sort(new Comparator<RegistInfoTask>() {
            @Override
            public int compare(RegistInfoTask a, RegistInfoTask b) {
                // Ordenar por ano primeiro
                int yearComparison = Integer.compare(a.getYear(), b.getYear());
                if (yearComparison != 0) {
                    return yearComparison;
                }
                // Se os anos forem iguais, ordenar por mês
                return Integer.compare(a.getMonth(), b.getMonth());
            }
        });

        return registInfoTasks;
    }


}
