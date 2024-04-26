package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;

@XmlRootElement
public class Stats {

    @XmlElement
    int totalUsers;
    @XmlElement
    int totalConfirmedUsers;
    @XmlElement
    int totalUnconfirmedUsers;
    @XmlElement
    int totalTasks;
    @XmlElement
    int totalToDoTasks;
    @XmlElement
    int totalDoingTasks;
    @XmlElement
    int totalDoneTasks;
    @XmlElement
    double tasksPerUser;
    @XmlElement
    double averageTaskTime;
    @XmlElement
    ArrayList<RegistInfoCategory> categoriesListDesc;
    @XmlElement
    ArrayList<RegistInfoUser> usersOverTime;
    @XmlElement
    ArrayList<RegistInfoTask> tasksCompletedOverTime;

    private static final long serialVersionUID = 1L;


    public Stats() {
    }

    public Stats(int totalUsers, int totalConfirmedUsers, int totalUnconfirmedUsers, int totalTasks, int totalToDoTasks, int totalDoingTasks,
                 int totalDoneTasks, double tasksPerUser, double averageTaskTime, ArrayList<RegistInfoCategory> categoriesListDesc,
                 ArrayList<RegistInfoUser> usersOverTime, ArrayList<RegistInfoTask> tasksCompletedOverTime) {
        this.totalUsers = totalUsers;
        this.totalConfirmedUsers = totalConfirmedUsers;
        this.totalUnconfirmedUsers = totalUnconfirmedUsers;
        this.totalTasks = totalTasks;
        this.totalToDoTasks = totalToDoTasks;
        this.totalDoingTasks = totalDoingTasks;
        this.totalDoneTasks = totalDoneTasks;
        this.tasksPerUser = tasksPerUser;
        this.averageTaskTime = averageTaskTime;
        this.categoriesListDesc = categoriesListDesc;
        this.usersOverTime = usersOverTime;
        this.tasksCompletedOverTime = tasksCompletedOverTime;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalConfirmedUsers() {
        return totalConfirmedUsers;
    }

    public void setTotalConfirmedUsers(int totalConfirmedUsers) {
        this.totalConfirmedUsers = totalConfirmedUsers;
    }

    public int getTotalUnconfirmedUsers() {
        return totalUnconfirmedUsers;
    }

    public void setTotalUnconfirmedUsers(int totalUnconfirmedUsers) {
        this.totalUnconfirmedUsers = totalUnconfirmedUsers;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getTotalToDoTasks() {
        return totalToDoTasks;
    }

    public void setTotalToDoTasks(int totalToDoTasks) {
        this.totalToDoTasks = totalToDoTasks;
    }

    public int getTotalDoingTasks() {
        return totalDoingTasks;
    }

    public void setTotalDoingTasks(int totalDoingTasks) {
        this.totalDoingTasks = totalDoingTasks;
    }

    public int getTotalDoneTasks() {
        return totalDoneTasks;
    }

    public void setTotalDoneTasks(int totalDoneTasks) {
        this.totalDoneTasks = totalDoneTasks;
    }

    public double getTasksPerUser() {
        return tasksPerUser;
    }

    public void setTasksPerUser(double tasksPerUser) {
        this.tasksPerUser = tasksPerUser;
    }

    public double getAverageTaskTime() {
        return averageTaskTime;
    }

    public void setAverageTaskTime(double averageTaskTime) {
        this.averageTaskTime = averageTaskTime;
    }

    public ArrayList<RegistInfoCategory> getCategoriesListDesc() {
        return categoriesListDesc;
    }

    public void setCategoriesListDesc(ArrayList<RegistInfoCategory> categoriesListDesc) {
        this.categoriesListDesc = categoriesListDesc;
    }

    public ArrayList<RegistInfoUser> getUsersOverTime() {
        return usersOverTime;
    }

    public void setUsersOverTime(ArrayList<RegistInfoUser> usersOverTime) {
        this.usersOverTime = usersOverTime;
    }

    public ArrayList<RegistInfoTask> getTasksCompletedOverTime() {
        return tasksCompletedOverTime;
    }

    public void setTasksCompletedOverTime(ArrayList<RegistInfoTask> tasksCompletedOverTime) {
        this.tasksCompletedOverTime = tasksCompletedOverTime;
    }
}
