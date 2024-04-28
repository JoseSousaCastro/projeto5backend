package Beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import project5.bean.TaskBean;
import project5.dao.CategoryDao;
import project5.dao.TaskDao;
import project5.dao.UserDao;
import project5.dto.Task;
import project5.entity.TaskEntity;
import project5.entity.UserEntity;
import project5.websocket.WSDashboard;
import project5.websocket.WSTasks;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


public class TaskBeanTest {

    @Mock
    private TaskDao taskDao;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private UserDao userDao;

    @Mock
    private WSDashboard wsDashboard;

    @Mock
    private WSTasks wsTasks;

    @InjectMocks
    private TaskBean taskBean;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testPermanentlyDeleteTask() {
        // Mocking data
        String taskId = "taskId";
        TaskEntity taskEntity = new TaskEntity();

        when(taskDao.findTaskById(taskId)).thenReturn(taskEntity);
        when(taskDao.eraseTask(taskId)).thenReturn(true);

        // Testing
        assertTrue(taskBean.permanentlyDeleteTask(taskId));
    }


    @Test
    public void testValidateTask() {
        // Mocking data
        Task task = new Task();

        // Testing
        assertFalse(taskBean.validateTask(task));
    }


    @Test
    public void testPermanentlyDeleteTaskWhenErased() {
        // Mocking data
        String taskId = "taskId";
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setErased(true);

        when(taskDao.findTaskById(taskId)).thenReturn(taskEntity);
        when(taskDao.deleteTask(taskId)).thenReturn(true);

        // Testing
        assertTrue(taskBean.permanentlyDeleteTask(taskId));
    }

    @Test
    public void testPermanentlyDeleteTaskWhenNotErased() {
        // Mocking data
        String taskId = "taskId";
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setErased(false);

        when(taskDao.findTaskById(taskId)).thenReturn(taskEntity);
        when(taskDao.eraseTask(taskId)).thenReturn(true);

        // Testing
        assertTrue(taskBean.permanentlyDeleteTask(taskId));
    }

    @Test
    public void testEraseAllTasksFromUserWithTasks() {
        // Mocking data
        String username = "testUser";
        UserEntity userEntity = new UserEntity();
        ArrayList<TaskEntity> userTasks = new ArrayList<>();
        userTasks.add(new TaskEntity());

        when(userDao.findUserByUsername(username)).thenReturn(userEntity);
        when(taskDao.findTasksByUser(userEntity)).thenReturn(userTasks);

        // Testing
        assertTrue(taskBean.eraseAllTasksFromUser(username));
    }


    @Test
    public void testGetAllTasksWithNullEntityTasks() {
        // Mocking data
        String token = "validToken";
        UserEntity userEntity = new UserEntity();
        ArrayList<TaskEntity> entityTasks = null;

        when(userDao.findUserByToken(token)).thenReturn(userEntity);
        when(taskDao.findAllTasks()).thenReturn(entityTasks);

        // Testing
        assertEquals(0, taskBean.getAllTasks(token).size());
    }

    @Test
    public void testGetTasksByCategoryWithNullEntityTasks() {
        // Mocking data
        String category = "TestCategory";
        ArrayList<TaskEntity> entityTasks = null;

        when(categoryDao.findTasksByCategory(category)).thenReturn(entityTasks);

        // Testing
        assertEquals(0, taskBean.getTasksByCategory(category).size());
    }

}