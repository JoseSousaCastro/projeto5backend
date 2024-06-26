package project5.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name = "user")
@NamedQuery(name = "User.findAllUsers", query = "SELECT u FROM UserEntity u WHERE u.username NOT IN ('admin', 'NOTASSIGNED')")
@NamedQuery(name = "User.findAllUsersByTypeOfUser", query = "SELECT u FROM UserEntity u WHERE u.typeOfUser = :typeOfUser AND u.username NOT IN ('admin', 'NOTASSIGNED')")
@NamedQuery(name = "User.findAllUsersByVisibility", query = "SELECT u FROM UserEntity u WHERE u.visible = :visible AND u.username NOT IN ('admin', 'NOTASSIGNED')")
@NamedQuery(name = "User.findAllUsersByTypeOfUserByVisibility", query = "SELECT u FROM UserEntity u WHERE u.typeOfUser = :typeOfUser AND u.visible = :visible AND u.username NOT IN ('admin', 'NOTASSIGNED')")
@NamedQuery(name = "User.findUserByUsername", query = "SELECT u FROM UserEntity u WHERE u.username = :username")
@NamedQuery(name = "User.findUserByEmail", query = "SELECT u FROM UserEntity u WHERE u.email = :email")
@NamedQuery(name = "User.findUserByPhone", query = "SELECT  u FROM UserEntity u WHERE u.phone = :phone")
@NamedQuery(name = "User.findUserByToken", query = "SELECT DISTINCT u FROM UserEntity u WHERE u.token = :token")
@NamedQuery(name = "User.findUserByUsernameAndPassword", query = "SELECT u FROM UserEntity u WHERE u.username = :username AND u.password = :password")
@NamedQuery(name = "User.findAllUsersByIsConfirmed", query = "SELECT u FROM UserEntity u WHERE u.confirmed = :confirmed AND u.username NOT IN ('admin', 'NOTASSIGNED')")
@NamedQuery(name = "User.findAllConfirmedAndNotErasedUsers", query = "SELECT u FROM UserEntity u WHERE u.confirmed = true AND u.visible = true AND u.username NOT IN ('admin', 'NOTASSIGNED')")
@NamedQuery(name = "User.findUsersRegisteredOnDate", query = "SELECT COUNT(u) FROM UserEntity u WHERE u.creationDate = :creationDate AND u.confirmed = true AND u.username NOT IN ('admin', 'NOTASSIGNED')")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "username", nullable = false, unique = true, updatable = false)
    private String username;

    @Column(name = "password", nullable = true, unique = false, updatable = true)
    private String password;

    @Column(name = "type_of_user", nullable = false, unique = false, updatable = true)
    private int typeOfUser;

    @Column(name = "email", nullable = false, unique = true, updatable = true)
    private String email;

    @Column(name = "first_name", nullable = false, unique = false, updatable = true)
    private String firstName;

    @Column(name = "last_name", nullable = false, unique = false, updatable = true)
    private String lastName;

    @Column(name = "phone", nullable = false, unique = true, updatable = true)
    private String phone;

    @Column(name = "photo_url", nullable = false, unique = false, updatable = true)
    private String photoURL;

    @Column(name = "token", nullable = true, unique = true, updatable = true)
    private String token;

    @Column(name = "visible", nullable = false, unique = false, updatable = true)
    private boolean visible;

    @Column(name = "expiration_time", nullable = true, unique = false, updatable = true)
    private long expirationTime;

    @Column(name = "confirmed", nullable = false, unique = false, updatable = true)
    private boolean confirmed;

    @Column(name = "creation_date", nullable = false, unique = false, updatable = true)
    private LocalDate creationDate;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<TaskEntity> userTasks;

    @Column(name = "token_expiration_time", nullable = true, unique = false, updatable = true)
    private long tokenExpirationTime;


    //default empty constructor
    public UserEntity() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(int typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<TaskEntity> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(Set<TaskEntity> userTasks) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visivel) {
        this.visible = visivel;
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

    public long getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(long tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public String getCreationMonth() {
        return String.valueOf(creationDate.getMonthValue());
    }

    public String getCreationYear() {
        return String.valueOf(creationDate.getYear());
    }

    public void addNewTasks(ArrayList<TaskEntity> tasks) {
        for (TaskEntity task : tasks) {
            userTasks.add(task);
        }
    }
}
