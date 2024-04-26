package project5.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import project5.entity.CategoryEntity;
import project5.entity.TaskEntity;
import project5.entity.UserEntity;

import java.util.ArrayList;

import org.apache.logging.log4j.*;

@Stateless
public class TaskDao extends AbstractDao<TaskEntity> {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(TaskDao.class);

    public TaskDao() {
        super(TaskEntity.class);
    }


    public TaskEntity findTaskById(String id) {
        try {
            return (TaskEntity) em.createNamedQuery("Task.findTaskById").setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }

    }

    public ArrayList<TaskEntity> findTasksByUser(UserEntity userEntity) {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findTasksByUser").setParameter("owner", userEntity).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findTasksNotDeletedNorErasedByUser(UserEntity userEntity) {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findTasksNotDeletedNorErasedByUser").setParameter("owner", userEntity).getResultList();
        } catch (Exception e) {
            return null;
        }
    }


    public ArrayList<TaskEntity> findErasedTasks() {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findErasedTasks").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findAllTasks() {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findAllTasks").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean eraseTask(String id) {
        boolean erased = false;
        if (id == null) {
            erased = false;
        } else {
            try {
                TaskEntity taskToErase = findTaskById(id);
                taskToErase.setErased(true);
                merge(taskToErase);
                erased = true;
            } catch (Exception e) {
                logger.error("Task with id " + id + " was not erased");
                erased = false;
            }
            logger.info("Task with id " + id + " was erased");
        }
        return erased;
    }


    public boolean deleteTask(String id) {
        boolean deleted = false;
        if (id == null) {
            deleted = false;
        } else {
            try {
                em.createNamedQuery("DeleteTask").setParameter("id", id).executeUpdate();
                deleted = true;
            } catch (Exception e) {
                logger.error("Task with id " + id + " was not deleted");
                deleted = false;
            }
            logger.info("Task with id " + id + " was deleted");
        }
        return deleted;
    }

    public ArrayList<TaskEntity> findTasksNotDeletedeNorErsasedByUserAndStateId(UserEntity userEntity, int stateId) {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findTasksNotDeletedeNorErsasedByUserAndStateId").setParameter("owner", userEntity).setParameter("stateId", stateId).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findAllTasksByStateId(int stateId) {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findAllTasksByStateId").setParameter("stateId", stateId).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findAllTasksNotErased() {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findAllTasksNotErased").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findAllCompletedTasks() {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findAllCompletedTasks").getResultList();
        } catch (Exception e) {
            return null;
        }
    }


    public ArrayList<CategoryEntity> getCategoriesFromMostFrequentToLeastFrequent() {
        try {
            return (ArrayList<CategoryEntity>) em.createNamedQuery("Task.getCategoriesFromMostFrequentToLeastFrequent").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
