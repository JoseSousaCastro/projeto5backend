package project5.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.io.Serializable;
import project5.dao.CategoryDao;
import project5.dao.TaskDao;
import project5.dao.UserDao;

@Stateless
public class StatsBean implements Serializable{

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

    public int getNumberOfUsersByMonth(int month, int year) {
        return userDao.findAllUsersByMonthAndYear(month, year).size();
    }


    // Tasks by user

    public int getNumberOfTasksByUser(String username) {
        return taskDao.findTasksByUser(userDao.findUserByUsername(username)).size();
    }

    public int getNumberOfTodoTasksByUser(String username) {
        return taskDao.findTasksByUserAndStateId(userDao.findUserByUsername(username), 100).size();
    }

    public int getNumberOfDoingTasksByUser(String username) {
        return taskDao.findTasksByUserAndStateId(userDao.findUserByUsername(username), 200).size();
    }

    public int getNumberOfDoneTasksByUser(String username) {
        return taskDao.findTasksByUserAndStateId(userDao.findUserByUsername(username), 300).size();
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

    public int getCumulativeNumberOfDoneTasksByMonth(int month) {
        return taskDao.getCumulativeNumberOfDoneTasksByMonth(month);
    }

    public int getAverageNumberOfTasksPerUserInPercentage() {
        int totalTasks = taskDao.findAllTasksNotErased().size();
        int totalUsers = userDao.findAllConfirmedAndNotErasedUsers().size();

        // Verifique se o denominador não é zero para evitar uma divisão por zero
        if (totalUsers == 0) {
            return 0; // Retorna 0 se não houver usuários ativos
        }

        double averagePercentage = ((double) totalTasks / totalUsers) * 100;
        // Use Math.round() para arredondar o resultado para o inteiro mais próximo
        return (int) Math.round(averagePercentage);
    }


    public double getAverageTimeToCompleteTask() {
        double averageTime = taskDao.getAverageTimeToCompleteTask(getNumberOfDoneTasks(), taskDao.getSumOfDaysFromStartDateToDoneDate());

        // Use Math.round() para arredondar o resultado para a décima mais próxima
        double roundedAverage = Math.round(averageTime * 10.0) / 10.0;

        return roundedAverage;
    }


    // Categories Stats
    public int getNumberOfCategoriesFromMostFrequentToLeast() {
        return categoryDao.findAllCategoriesFromMostFrequentToLeastFrequent().size();
    }
}
