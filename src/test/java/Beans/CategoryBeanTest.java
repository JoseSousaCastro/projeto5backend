package Beans;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import project5.bean.CategoryBean;
import project5.dao.CategoryDao;
import project5.dto.Category;
import project5.entity.CategoryEntity;
import project5.websocket.WSDashboard;

public class CategoryBeanTest {

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private WSDashboard wsDashboard;

    @InjectMocks
    private CategoryBean categoryBean;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNewCategory() {
        // Mocking data
        String categoryName = "Test Category";

        // Testing
        boolean created = categoryBean.newCategory(categoryName);
        assertTrue(created);
        verify(categoryDao, times(1)).persist(any(CategoryEntity.class));
        verify(wsDashboard, times(1)).send("stats have been changed");
    }

    @Test
    public void testCategoryExists() {
        // Mocking data
        String categoryName = "Existing Category";
        when(categoryDao.findCategoryByName(categoryName)).thenReturn(new CategoryEntity());

        // Testing
        boolean exists = categoryBean.categoryExists(categoryName);
        assertTrue(exists);
    }

    @Test
    public void testDeleteCategory() {
        // Mocking data
        String categoryName = "Category to delete";
        when(categoryDao.deleteCategory(categoryName)).thenReturn(true);

        // Testing
        boolean deleted = categoryBean.deleteCategory(categoryName);
        assertTrue(deleted);
        verify(wsDashboard, times(1)).send("stats have been changed");
    }

    @Test
    public void testEditCategory() {
        // Mocking data
        String categoryName = "Category to edit";
        String newCategoryName = "Edited Category";
        when(categoryDao.editCategory(categoryName, newCategoryName)).thenReturn(true);

        // Testing
        boolean edited = categoryBean.editCategory(categoryName, newCategoryName);
        assertTrue(edited);
        verify(wsDashboard, times(1)).send("stats have been changed");
    }

    @Test
    public void testConvertCategoryToEntity() {
        // Mocking data
        String categoryName = "Test Category";

        // Testing
        CategoryEntity categoryEntity = categoryBean.convertCategoryToEntity(categoryName);
        assertNotNull(categoryEntity);
        assertEquals(categoryName, categoryEntity.getName());
    }

}

