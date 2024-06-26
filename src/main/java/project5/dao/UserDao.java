package project5.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import project5.entity.UserEntity;

import java.util.ArrayList;

import org.apache.logging.log4j.*;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(UserDao.class);

    public UserDao() {
        super(UserEntity.class);
    }


    public UserEntity findUserByToken(String token) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByToken").setParameter("token", token)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public UserEntity findUserByUsername(String username) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByUsername").setParameter("username", username)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }


    public UserEntity findUserByEmail(String email) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByEmail").setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public UserEntity findUserByPhone(String phone) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByPhone").setParameter("phone", phone)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public UserEntity findUserByUsernameAndPassword(String username, String password) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByUsernameAndPassword")
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; //Nenhum user foi encontrado com estes dados
        }
    }

    public ArrayList<UserEntity> findAllUsers() {
        try {
            return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsers").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<UserEntity> findAllUsersByTypeOfUser(int typeOfUser) {
        try {
            return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsersByTypeOfUser").setParameter("typeOfUser", typeOfUser).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<UserEntity> findAllUsersByVisibility(boolean visible) {
        try {
            return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsersByVisibility").setParameter("visible", visible).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<UserEntity> findAllUsersByTypeOfUserAndVisibility(int typeOfUser, boolean visible) {
        try {
            return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsersByTypeOfUserByVisibility").setParameter("typeOfUser", typeOfUser)
                    .setParameter("visible", visible).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<UserEntity> findAllUsersByIsConfirmed(boolean confirmed) {
        try {
            return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsersByIsConfirmed").setParameter("confirmed", confirmed).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<UserEntity> findAllConfirmedAndNotErasedUsers() {
        try {
            return (ArrayList<UserEntity>) em.createNamedQuery("User.findAllConfirmedAndNotErasedUsers").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
