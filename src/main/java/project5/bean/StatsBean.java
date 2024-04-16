package project5.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import project5.dao.CategoryDao;
import project5.dao.TaskDao;
import project5.dao.UserDao;
import project5.dto.TaskRegistrationInfo;
import project5.dto.UserRegistrationInfo;
import project5.entity.TaskEntity;

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


    public double getAverageOfTaskTimes () {
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
    public ArrayList getNumberOfCategoriesFromMostFrequentToLeast() {
        return taskDao.getCategoriesFromMostFrequentToLeastFrequent();
    }

    public ArrayList<UserRegistrationInfo> getUsersOverTime() {
        return userDao.getUsersRegisteredOverTime();
    }

    public ArrayList<TaskRegistrationInfo> getTasksCompletedOverTime() {
        return taskDao.getTasksCompletedOverTime();
    }



}
