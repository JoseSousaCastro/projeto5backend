package project5.dao;

import project5.entity.CategoryEntity;
import project5.entity.TaskEntity;
import jakarta.ejb.Stateless;

import java.util.ArrayList;

import org.apache.logging.log4j.*;

@Stateless
public class CategoryDao extends AbstractDao<CategoryEntity> {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(CategoryDao.class);

    public CategoryDao() {
        super(CategoryEntity.class);
    }

    public ArrayList<TaskEntity> findTasksByCategory(String name) {
        try {
            return (ArrayList<TaskEntity>) em.createNamedQuery("Category.findTasksByCategory").setParameter("name", name).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<CategoryEntity> findAllCategories() {
        try {
            return (ArrayList<CategoryEntity>) em.createNamedQuery("Category.findCategories").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public CategoryEntity findCategoryByName(String name) {
        try {
            return (CategoryEntity) em.createNamedQuery("Category.findCategoryByName").setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean deleteCategory(String name) {
        boolean deleted = false;
        if (name == null) {
            deleted = false;
        } else {
            try {
                ArrayList<TaskEntity> categoryTasks = (ArrayList<TaskEntity>) em.createNamedQuery("Category.findTasksByCategory").setParameter("name", name).getResultList();
                System.out.println("*/*/*/*/*/ SIZE " + categoryTasks.size());
                if (categoryTasks.isEmpty()) {
                    em.createNamedQuery("Category.deleteCategory").setParameter("name", name).executeUpdate();
                    deleted = true;
                }
            } catch (Exception e) {
                deleted = false;
            }
        }
        logger.info("Category " + name + " deleted: " + deleted);
        return deleted;
    }

    public boolean editCategory(String name, String newName) {
        boolean edited = false;
        if (name == null || newName == null) {
            edited = false;
        } else {
            try {
                CategoryEntity categoryEntity = findCategoryByName(name);
                if (categoryEntity != null) {
                    categoryEntity.setName(newName);
                    merge(categoryEntity);
                    edited = true;
                }
            } catch (Exception e) {
                edited = false;
            }
        }
        logger.info("Category " + name + " edited: " + edited);
        return edited;
    }

}