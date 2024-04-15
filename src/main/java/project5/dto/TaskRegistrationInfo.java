package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;

import java.time.LocalDate;

public class TaskRegistrationInfo {
    @XmlElement
    private LocalDate date;
    @XmlElement
    private int totalCompleteTasks;

    public TaskRegistrationInfo(LocalDate date, int totalCompleteTasks) {
        this.date = date;
        this.totalCompleteTasks = totalCompleteTasks;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalCompleteTasks() {
        return totalCompleteTasks;
    }

    public void setTotalCompleteTasks(int totalCompleteTasks) {
        this.totalCompleteTasks = totalCompleteTasks;
    }

}