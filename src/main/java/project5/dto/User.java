package project5.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;
import java.util.ArrayList;

@XmlRootElement
public class User {
    @XmlElement
    private String username;
    @XmlElement
    private String password;
    @XmlElement
    private String email;
    @XmlElement
    private String firstName;
    @XmlElement
    private String lastName;
    @XmlElement
    private String phone;
    @XmlElement
    private String photoURL;
    @XmlElement
    private boolean visible;
    @XmlElement
    int typeOfUser;
    @XmlElement
    public static final int DEVELOPER = 100;
    @XmlElement
    public static final int SCRUMMASTER = 200;
    @XmlElement
    public static final int PRODUCTOWNER = 300;
    @XmlElement
    public static final int NOTASSIGNED = 400;
    @XmlElement
    private ArrayList<Task> userTasks = new ArrayList<>(); //ser array de ids das tasks assim as tasks ficavam no json das tasks
    @XmlElement
    private long expirationTime;
    @XmlElement
    private boolean confirmed;
    @XmlElement
    private LocalDate creationDate;

    private static final long serialVersionUID = 1L;


    public User() {
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Task> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(ArrayList<Task> userTasks) {
        this.userTasks = userTasks;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(int typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public void setInitialTypeOfUser() {
        this.typeOfUser = DEVELOPER;
    }

    public void editTypeOfUser(int stateId) {
        if (stateId == SCRUMMASTER) {
            this.typeOfUser = SCRUMMASTER;
        } else if (stateId == PRODUCTOWNER) {
            this.typeOfUser = PRODUCTOWNER;
        } else {
            this.typeOfUser = DEVELOPER;
        }
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public int getCreationMonth() {
        if (creationDate != null) {
            return creationDate.getMonthValue();
        }
        return 0;
    }

    public String getCreationMonthName() {
        if (creationDate != null) {
            int month = creationDate.getMonthValue();
            switch (month) {
                case 1:
                    return "Jan";
                case 2:
                    return "Feb";
                case 3:
                    return "Mar";
                case 4:
                    return "Apr";
                case 5:
                    return "May";
                case 6:
                    return "Jun";
                case 7:
                    return "Jul";
                case 8:
                    return "Aug";
                case 9:
                    return "Sep";
                case 10:
                    return "Oct";
                case 11:
                    return "Nov";
                case 12:
                    return "Dec";
            }
        }
        return "";
    }

    public int getCreationYear() {
        if (creationDate != null) {
            return creationDate.getYear();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", userTasks=" + userTasks +
                '}';
    }
}