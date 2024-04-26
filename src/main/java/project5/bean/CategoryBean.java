package project5.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import project5.dao.CategoryDao;
import project5.dto.Category;
import project5.entity.CategoryEntity;
import project5.websocket.WSDashboard;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.logging.log4j.*;

@Stateless
public class CategoryBean implements Serializable {
    @EJB
    private CategoryDao categoryDao;
    @EJB
    private WSDashboard wsDashboard;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(CategoryBean.class);

    public boolean newCategory(String name) {
        boolean created = false;
        if (name != null) {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setName(name);
            categoryDao.persist(categoryEntity);
            created = true;
            wsDashboard.send("stats have been changed");
        }
        return created;
    }

    public boolean categoryExists(String name) {
        boolean exists = false;
        if (name != null) {
            CategoryEntity categoryEntity = categoryDao.findCategoryByName(name);
            if (categoryEntity != null) {
                exists = true;
            }
        }
        return exists;
    }

    public ArrayList<Category> findAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<CategoryEntity> categoryEntities = categoryDao.findAllCategories();
        for (CategoryEntity categoryEntity : categoryEntities) {
            categories.add(convertCategoryEntityToCategoryDto(categoryEntity));
        }
        return categories;
    }

    public boolean deleteCategory(String name) {
        boolean deleted = false;
        System.out.println();
        if (name != null) {
            deleted = categoryDao.deleteCategory(name);
        }
        wsDashboard.send("stats have been changed");
        return deleted;
    }

    public boolean editCategory(String name, String newName) {
        boolean edited = false;
        if (name != null && newName != null) {
            edited = categoryDao.editCategory(name, newName);
        }
        wsDashboard.send("stats have been changed");
        return edited;
    }

    public CategoryEntity convertCategoryToEntity(String name) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(name);
        return categoryEntity;
    }

    public Category convertCategoryEntityToCategoryDto(CategoryEntity categoryEntity) {
        Category category = new Category();
        category.setName(categoryEntity.getName());
        return category;
    }
}

