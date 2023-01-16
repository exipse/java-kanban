package History;

import Model.Epic;
import Model.Status;
import Model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Для HistoryManager — тесты для всех методов интерфейса. Граничные условия:
// a. Пустая история задач.
// b. Дублирование.
// с. Удаление из истории: начало, середина, конец.

class InMemoryHistoryManagerTest {
    HistoryManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @AfterEach
    private void clean() {
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            manager.remove(task.getId());
        }
        Task.setCount(0);
    }

    @Test
    public void addInHistory() {
        createTasks();
        manager.getHistory();
        System.out.println();
        assertEquals(6, manager.getHistory().size());
    }

    @Test
    public void getEmptyHistory() {
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            manager.remove(task.getId());
        }
        assertEquals(0, manager.getHistory().size(), "История должна быть пуста");
    }


    @Test
    public void addDuplicationInHistory() {
        createTasks();
        List<Task> tests = getTasks();
        assertEquals(6, manager.getHistory().size());
        for (Task test : tests) {
            manager.add(test);
        }
        assertEquals(6, manager.getHistory().size());
        assertEquals(tests, getTasks(), "Не равны");
    }


    @Test
    public void deleteFirstInHistory() {
        createTasks();
        assertEquals(6, manager.getHistory().size(), "В истории не 6 записей");
        List<Task> tests = manager.getHistory();
        int checkTaskId = tests.get(1).getId();
        assertEquals(checkTaskId, manager.getHistory().get(1).getId());
        manager.remove(1);
        assertEquals(5, manager.getHistory().size(), "Удаление элемента не произошло");
        assertEquals(checkTaskId, manager.getHistory().get(0).getId(), "Первый элемент не удалился");
    }

    @Test
    public void deleteInTheMiddleInHistory() {
        createTasks();
        List<Task> tests = manager.getHistory();
        assertEquals(6, tests.size(), "В истории не 6 записей");

        int checkTaskId = tests.get(4).getId();
        assertEquals(checkTaskId, manager.getHistory().get(4).getId());
        manager.remove(4);
        assertEquals(5, manager.getHistory().size(), "Удаление элемента не произошло");
        assertEquals(checkTaskId, manager.getHistory().get(3).getId(), "Элемент из середины не удалился");
    }

    @Test
    public void deleteLastTaskInHistory() {
        createTasks();
        List<Task> tests = manager.getHistory();
        assertEquals(6, tests.size(), "В истории не 6 записей");

        int checkTaskId = tests.get(5).getId();
        assertEquals(checkTaskId, manager.getHistory().get(5).getId());
        manager.remove(5);
        assertEquals(5, manager.getHistory().size(), "Удаление элемента не произошло");
        assertEquals(checkTaskId, manager.getHistory().get(4).getId(), "Последний элемент не удалился");
    }


    private void createTasks() {
        manager.add(new Task("Таска1", "Описание, описание, etc...",
                Status.NEW, LocalDateTime.now().plusDays(1), 50));
        manager.add(new Task("Таска2", "Описание, описание, etc...",
                Status.NEW, LocalDateTime.now().plusDays(2), 50));
        manager.add(new Task("Таска3", "Описание, описание, etc...",
                Status.NEW, LocalDateTime.now().plusDays(3), 50));
        manager.add(new Epic("Эпик1", "Важный description"));
        manager.add(new Epic("Эпик2", "Важный description"));
        manager.add(new Epic("Эпик3", "Важный description"));
    }

    private List<Task> getTasks() {
        return manager.getHistory();
    }


}