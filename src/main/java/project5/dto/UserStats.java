package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;

public class UserStats {
    @XmlElement
    int totalUserTasks;
    @XmlElement
    int totalUserToDoTasks;
    @XmlElement
    int totalUserDoingTasks;
    @XmlElement
    int totalUserDoneTasks;

    public UserStats() {
    }

    public UserStats(int totalUserTasks, int totalUserToDoTasks, int totalUserDoingTasks, int totalUserDoneTasks) {
        this.totalUserTasks = totalUserTasks;
        this.totalUserToDoTasks = totalUserToDoTasks;
        this.totalUserDoingTasks = totalUserDoingTasks;
        this.totalUserDoneTasks = totalUserDoneTasks;
    }

    public int getTotalUserTasks() {
        return totalUserTasks;
    }

    public void setTotalUserTasks(int totalUserTasks) {
        this.totalUserTasks = totalUserTasks;
    }

    public int getTotalUserToDoTasks() {
        return totalUserToDoTasks;
    }

    public void setTotalUserToDoTasks(int totalUserToDoTasks) {
        this.totalUserToDoTasks = totalUserToDoTasks;
    }

    public int getTotalUserDoingTasks() {
        return totalUserDoingTasks;
    }

    public void setTotalUserDoingTasks(int totalUserDoingTasks) {
        this.totalUserDoingTasks = totalUserDoingTasks;
    }

    public int getTotalUserDoneTasks() {
        return totalUserDoneTasks;
    }

    public void setTotalUserDoneTasks(int totalUserDoneTasks) {
        this.totalUserDoneTasks = totalUserDoneTasks;
    }
}
