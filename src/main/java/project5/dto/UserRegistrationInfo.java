package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;

import java.time.LocalDate;

public class UserRegistrationInfo {
    @XmlElement
    private LocalDate date;
    @XmlElement
    private int totalConfirmedUsers;

    public UserRegistrationInfo(LocalDate date, int totalConfirmedUsers) {
        this.date = date;
        this.totalConfirmedUsers = totalConfirmedUsers;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalConfirmedUsers() {
        return totalConfirmedUsers;
    }

    public void setTotalConfirmedUsers(int totalConfirmedUsers) {
        this.totalConfirmedUsers = totalConfirmedUsers;
    }
}
