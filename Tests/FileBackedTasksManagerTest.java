import History.HistoryManager;
import Manager.FileBackedTasksManager;
import Manager.Managers;
import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//для FileBackedTasksManager — проверка работы по сохранению и восстановлению состояния. Граничные условия:
// a. Пустой список задач.
// b. Эпик без подзадач.
// c. Пустой список истории.

class FileBackedTasksManagerTest
        extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void beforeEach() {
        setManager((FileBackedTasksManager) Managers.getDefault());
    }

    @AfterEach
    private void clean() {
        HistoryManager history = manager.getObjectHistory();
        List<Task> tasks = history.getHistory();
        for (Task task : tasks) {
            history.remove(task.getId());
        }
        Task.setCount(0);
    }

    @Test
    public void loadFile() {
        Task.setCount(0);
        manager.createTask(new Task("Таска - 1",
                "Описание 1...", Status.NEW, LocalDateTime.now(), 10));
        manager.createEpic(new Epic("Epic1", "Описание эпика 1"));
        manager.createSubTask(new SubTask("СабТаска 1из3 в эпике 1 ",
                "Описание сабтаски 1", Status.DONE, 2, LocalDateTime.now().plusDays(4), 60));
        manager.createSubTask(new SubTask("СабТаска 1из3 в эпике 1 ",
                "Описание сабтаски 1", Status.DONE, 2));
        manager.createEpic(new Epic("Epic2", "Эпик без сабтасок"));
        manager.createEpic(new Epic("Epic3", "Описание эпика 3"));
        manager.createSubTask(new SubTask("SubTask2", "сабтаска 3-го эпика", Status.DONE, 5));
        manager.createSubTask(new SubTask("SubTask3",
                "еще 1 сабтаска 3-го эпика", Status.DONE, 5));
        manager.createTask(new Task("Task2", "просто Таска2", Status.NEW));
        manager.createSubTask(new SubTask("SubTask4",
                "сабтаска 1-го эпика №2", Status.DONE, 2));
        manager.getEpic(2);
        manager.getSubTask(6);
        manager.getTask(8);
        assertNotNull(manager.getSubTaskList());
        assertNotNull(manager.getEpicList());
        assertNotNull(manager.getTaskList());
        Task.setCount(0);
        FileBackedTasksManager file1 =
                FileBackedTasksManager.loadFromFile((Paths.get("src/files/saveFileStatic.txt")).toFile());
        List<Task> history = file1.getHistory();
        assertNotNull(history, "История не выгрузилась в файл");
    }

    @Test
    public void loadFileWithOutSubTask() {
        Task.setCount(0);
        manager.createTask(new Task("Таска - 1",
                "Описание 1...", Status.NEW, LocalDateTime.now(), 10));
        manager.createEpic(new Epic("Epic1", "Описание эпика 1"));
        manager.createEpic(new Epic("Epic2", "Эпик без сабтасок"));
        manager.createEpic(new Epic("Epic3", "Описание эпика 3"));
        manager.createTask(new Task("Task2", "просто Таска2", Status.NEW));
        manager.getEpic(2);
        manager.getTask(1);
        assertNull(manager.getSubTaskList());
        assertNotNull(manager.getEpicList());
        Task.setCount(0);
        FileBackedTasksManager file1 =
                FileBackedTasksManager.loadFromFile((Paths.get("src/files/saveFileStatic.txt")).toFile());
        List<Task> history = file1.getHistory();
        assertNotNull(history, "История не выгрузилась в файл");
    }

    @Test
    public void emptyFileForRecovery() {
        Task.setCount(0);
        assertNull(manager.getSubTaskList());
        assertNull(manager.getEpicList());
        assertNull(manager.getSubTaskList());
        FileBackedTasksManager file1 =
                FileBackedTasksManager.loadFromFile((Paths.get("src/files/saveFileStatic.txt")).toFile());
        assertNotNull(file1);
    }

    @Test
    public void loadFileWithOutHistory() {
        Task.setCount(0);
        manager.createTask(new Task("Таска - 1",
                "Описание 1...", Status.NEW, LocalDateTime.now(), 10));
        manager.createEpic(new Epic("Epic3", "Описание эпика 3"));
        manager.createSubTask(new SubTask("SubTask4",
                "сабтаска 1-го эпика №2", Status.DONE, 2));
        assertNotNull(manager.getSubTaskList());
        assertNotNull(manager.getEpicList());
        assertNotNull(manager.getTaskList());
        Task.setCount(0);
        FileBackedTasksManager.loadFromFile((Paths.get("src/files/saveFileStatic.txt")).toFile());
        assertEquals(0, manager.getHistory().size());
    }
}