package project5.bean;


import com.google.gson.JsonObject;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import project5.dao.UserDao;
import project5.dto.ChatNotification;
import project5.entity.UserEntity;
import project5.websocket.WSNotifications;

import java.util.List;



@Singleton
public class TimerBean {
    @Inject
    private UserDao userDao;
    @Inject
    private WSNotifications wsNotifications;
    @EJB
    private UserBean userBean;
    @Schedule(second="*/30", minute="*", hour="*") // this automatic timer is set

    public void automaticTimer(){
        System.out.println("TimerBean is running");
    List< UserEntity > users = userDao.findAllUsers();
    for(UserEntity user : users){
        if (user.getExpirationTime() != 0) {
            if (user.getExpirationTime() < System.currentTimeMillis()) {
                userDao.remove(user);
            }
        }if (user.getTokenExpirationTime() != 0) {
            if (user.getTokenExpirationTime() < System.currentTimeMillis()) {
                userBean.logout(user.getToken());
                ChatNotification chatNotification = new ChatNotification("LogoutRequest");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", chatNotification.getMessage());
                wsNotifications.send(user.getUsername(), jsonObject.toString());
            }
        }
    }

    }
}
