package project5.dao;

import jakarta.persistence.Query;
import project5.dto.TaskRegistrationInfo;
import project5.entity.CategoryEntity;
import project5.entity.TaskEntity;
import project5.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Timestamp;

@Stateless
public class TaskDao extends AbstractDao<TaskEntity> {

    private static final long serialVersionUID = 1L;

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
                erased = false;
            }
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
                deleted = false;
            }
        }
        return deleted;
    }

    public ArrayList<TaskEntity> findTasksByUserAndStateId(UserEntity userEntity, int stateId) {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Task.findTasksByUserAndStateId").setParameter("owner", userEntity).setParameter("stateId", stateId).getResultList();
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


    public ArrayList<TaskRegistrationInfo> getTasksCompletedOverTime() {
        ArrayList<TaskRegistrationInfo> completedInfoList = new ArrayList<>();

        // Obtenha a data mínima e máxima das tasks completed
        Timestamp minTimestamp = (Timestamp) em.createQuery("SELECT MIN(a.doneDate) FROM TaskEntity a WHERE a.stateId = 300").getSingleResult();
        Timestamp maxTimestamp = (Timestamp) em.createQuery("SELECT MAX(a.doneDate) FROM TaskEntity a WHERE a.stateId = 300").getSingleResult();

        // Verifique se os resultados não são nulos antes de converter para LocalDateTime
        if (minTimestamp != null && maxTimestamp != null) {
            LocalDate minDate = minTimestamp.toLocalDateTime().toLocalDate();
            LocalDate maxDate = maxTimestamp.toLocalDateTime().toLocalDate();

            // Inicialize o total acumulado
            int accumulatedTotal = 0;

            // Loop através de cada dia entre minDate e maxDate
            for (LocalDate date = minDate; date.isBefore(maxDate.plusDays(1)); date = date.plusDays(1)) {
                // Consulta para obter o número de tasks completed nesse dia
                Query query = em.createNamedQuery("Task.findTasksCompletedOnDate");
                query.setParameter("doneDate", date);
                int count = query.getResultList().size();

                // Adicione o número de tasks completed nesse dia ao total acumulado
                accumulatedTotal += count;

                // Adicione o total acumulado para este dia
                completedInfoList.add(new TaskRegistrationInfo(date, accumulatedTotal));
            }
        }

        return completedInfoList;
    }


    public ArrayList<CategoryEntity> getCategoriesFromMostFrequentToLeastFrequent() {
        try {
            return (ArrayList<CategoryEntity>) em.createNamedQuery("Task.getCategoriesFromMostFrequentToLeastFrequent").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
