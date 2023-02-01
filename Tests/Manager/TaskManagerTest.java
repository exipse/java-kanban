package Manager;

import Manager.TaskManager;
import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    final Task O_TASK_1 =
            new Task("Таска - 1", "Описание 1...", Status.NEW, LocalDateTime.now(), 10);
    final Epic O_EPIC_1 =
            new Epic("Epic1", "Описание эпика 1");
    final SubTask O_SUB_1 =
            new SubTask("SubTask1", "сабтаска 1-го эпика", Status.NEW,
                    2, LocalDateTime.now().plusDays(5), 2);

    public void setManager(T manager) {
        this.manager = manager;
    }

    @Test
    public void createTaskTest() {
        assertNull(manager.getTaskList());
        manager.createTask(O_TASK_1);
        List<Task> tasks = manager.getTaskList();
        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(10, O_TASK_1.getDuration());
    }

    @Test
    public void createEpicTestWithOutTask() {
        manager.createEpic(O_EPIC_1);
        assertNotNull(O_EPIC_1.getStatus());
        assertEquals(1, manager.getEpicList().size(), "Эпик не сохранился.");
        assertEquals(0, manager.getEpic(O_EPIC_1.getId()).getSubTaskIds().size(),
                "В эпике есть сабтаски");
        assertEquals(Status.NEW, O_EPIC_1.getStatus());
        assertEquals(new ArrayList<>(), O_EPIC_1.getSubTaskIds(),
                "Ошибка. В созданном эпике обнаружились сабтаски");
    }

    @Test
    public void createEpicTestWithTaskInStatusNew() {
        createFastTasks();
        assertNotNull(manager.getEpic(2), "Проверьте наличие эпика по идентификатору");
        Epic epic = manager.getEpic(2);
        System.out.println(epic);
        List<Epic> epics = manager.getEpicList();
        assertEquals(Status.NEW, epic.getStatus(), "Эпик не в Статусе - NEW");
        assertEquals(1, epics.size(), "Эпик не сохранился.");
        assertEquals(1, epic.getSubTaskIds().size(), "В эпике нет сабтасок");
        assertEquals(2, epic.getDuration(), "Неверная продолжительность эпика относительно сабтаски");
    }

    @Test
    public void createEpicTestWithTaskInStatusDone() {
        createFastTasks();
        SubTask newsubTask2 =
                new SubTask("SubTask2", "сабтаска 1-го эпика", Status.DONE,
                        2, LocalDateTime.now().plusDays(20), 77);
        List<Epic> epics = manager.getEpicList();
        assertEquals(1, epics.size(), "Эпик не сохранился.");
        assertEquals(1, manager.getSubTaskList().size(), "Сабтаска не сохранена");
        assertNotNull(manager.getSubTask(3), "Проверьте наличие сабтаски по идентификатору");
        SubTask subTask = manager.getSubTask(3);
        subTask.setStatus(Status.DONE);
        manager.updateSubTask(subTask);
        assertNotNull(manager.getEpic(2), "Проверьте наличие эпика по идентификатору");
        Epic epic = manager.getEpic(2);
        assertEquals(1, epic.getSubTaskIds().size(), "В эпике не 1 задача");
        assertEquals(Status.DONE, epic.getStatus(), "Эпик не в Статусе - Done");
    }

    @Test
    public void createEpicTestWithTasksInStatusNewAndDone() {
        createFastTasks();
        SubTask newsubTask2 =
                new SubTask("SubTask2", "сабтаска 1-го эпика", Status.DONE,
                        2, LocalDateTime.now().plusDays(20), 77);
        manager.createSubTask(newsubTask2);
        List<Epic> epics = manager.getEpicList();
        assertEquals(1, epics.size(), "Эпик не сохранился.");
        assertEquals(2, manager.getSubTaskList().size(), "Сабтасок неверное колличество");
        assertNotNull(manager.getEpic(2), "Проверьте наличие эпика по идентификатору");
        Epic epic = manager.getEpic(2);
        assertEquals(2, epic.getSubTaskIds().size(), "В эпике не 2 задача");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик не в Статусе - InProgress");
        assertEquals(79, epic.getDuration(),
                "Неверная продолжительность эпика относительно сабтасок");
        assertEquals(newsubTask2.getFinishTime(), epic.getFinishTime(),
                "Время конца эпика не совпадает с датом окончания последней задачи ");
    }

    @Test
    public void createEpicTestWithTaskInStatusInProgress() {
        createFastTasks();
        List<Epic> epics = manager.getEpicList();
        assertEquals(1, epics.size(), "Эпик не сохранился.");
        assertEquals(1, manager.getSubTaskList().size(), "Сабтаска не сохранена");
        assertNotNull(manager.getSubTask(3), "Проверьте наличие сабтаски по идентификатору");
        SubTask subTask = manager.getSubTask(3);
        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask);
        assertNotNull(manager.getEpic(2), "Проверьте наличие эпика по идентификатору");
        Epic epic = manager.getEpic(2);
        assertEquals(1, epic.getSubTaskIds().size(), "В эпике не 1 задача");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик не в Статусе - InProgress");
    }

    @Test
    public void createSubTaskWithNoExistEpic() {
        assertNull(manager.getSubTaskList(), "Сабтасок не должно быть");
        assertNull(manager.getEpic(Integer.MAX_VALUE), "Эпик c Id не должен быть создан");
        SubTask newSubTask =
                new SubTask("SubTask2", "сабтаска 1-го эпика", Status.DONE,
                        Integer.MAX_VALUE, LocalDateTime.now().plusDays(20), 77);
        manager.createSubTask(newSubTask);
        assertNull(manager.getSubTaskList(), "Сабтаска не должна быть создана");
    }

    @Test
    public void createSubTaskTest() {
        manager.createEpic(O_EPIC_1);
        manager.createSubTask(O_SUB_1);
        assertNotNull(O_SUB_1.getStatus());
        assertEquals(1, manager.getSubTaskList().size(), "Сабтаска не сохранилась");
        assertEquals(Status.NEW, O_SUB_1.getStatus());
        assertEquals(O_SUB_1.getEpicId(), O_EPIC_1.getId(), "Эпик у сабтаски не найден");
        assertEquals(manager.getEpic(O_SUB_1.getEpicId()).getSubTaskIds().get(0), O_SUB_1.getId(),
                "Сабкаска не привязана к эпику");
    }

    //Метод Обновление Таски, Эпика, Сабтаски
    @Test
    public void updateTaskTest() {
        manager.createTask(O_TASK_1);
        O_TASK_1.setName("Обновленная Таска - 1");
        O_TASK_1.setDescription("Новое Описание 1");
        O_TASK_1.setStatus(Status.IN_PROGRESS);
        O_TASK_1.setDuration(55);
        manager.updateTask(O_TASK_1);
        assertNotNull(O_TASK_1.getStatus());
        assertEquals(55, O_TASK_1.getDuration(), " Продолжительность таски не изменилась");
        assertEquals(Status.IN_PROGRESS, O_TASK_1.getStatus());
    }

    @Test
    public void updateNoExitTaskTest() {
        assertNull(manager.getTaskList(), "Тасок не должно быть");
        O_TASK_1.setName("Несуществующая Таска");
        O_TASK_1.setDescription("Новое Описание 1");
        O_TASK_1.setStatus(Status.IN_PROGRESS);
        O_TASK_1.setDuration(77);
        manager.updateTask(O_TASK_1);
        assertNull(manager.getTaskList(), "Тасок не должно быть");
    }

    @Test
    public void updateEpicTest() {
        manager.createEpic(O_EPIC_1);
        O_EPIC_1.setName("Измененный Эпик 1");
        O_EPIC_1.setDescription("Обновленное Описание");
        manager.updateEpic(O_EPIC_1);
        assertNotNull(O_EPIC_1.getStatus());
        assertEquals(1, manager.getEpicList().size(), "Эпик не сохранился.");
        assertEquals(Status.NEW, O_EPIC_1.getStatus());
        assertEquals(new ArrayList<>(), O_EPIC_1.getSubTaskIds(),
                "Ошибка. В созданном эпике обнаружились сабтаски");
        System.out.println("");
    }

    @Test
    public void updateNoExitEpicTest() {
        assertNull(manager.getEpicList(), "Эпиков не должно быть");
        O_EPIC_1.setName("Несуществующий Эпик");
        manager.updateEpic(O_EPIC_1);
        assertNull(manager.getEpicList(), "Эпиков не должно быть");
    }

    @Test
    public void updateSubTaskTest() {
        manager.createEpic(O_EPIC_1);
        manager.createSubTask(O_SUB_1);
        O_SUB_1.setName("Обновленная Таска - 1");
        O_SUB_1.setDescription("Новое Описание 1");
        O_SUB_1.setStatus(Status.IN_PROGRESS);
        O_SUB_1.setStartTime(O_SUB_1.getStartTime().plusDays(10));
        O_SUB_1.setDuration(60);
        manager.updateSubTask(O_SUB_1);
        assertNotNull(O_SUB_1.getStatus());
        assertEquals(60, O_SUB_1.getDuration(), " Продолжительность сабтаски не поменялась");
        assertEquals(Status.IN_PROGRESS, O_SUB_1.getStatus());
        assertEquals(manager.getEpic(O_SUB_1.getEpicId()).getSubTaskIds().get(0), O_SUB_1.getId(),
                "Сабкаска не привязана к эпику");
        assertEquals(O_SUB_1.getFinishTime(), O_EPIC_1.getFinishTime(),
                "Время конца эпика не совпадает с датом окончания последней задачи ");
    }

    @Test
    public void updateNoExitSubTaskTest() {
        assertNull(manager.getSubTaskList(), "Сабтасок не должно быть");
        O_SUB_1.setName("Несуществующая Сабтаска");
        manager.updateSubTask(O_SUB_1);
        assertNull(manager.getSubTaskList(), "Сабтасок не должно быть");
    }

    //Получение таски/эпика/сабтаска по идентификатору
    @Test
    public void getTaskTest() {
        createFastTasks();
        assertNotNull(manager.getTask(1), "Проверьте наличие таски по идентификатору");
        Task task = manager.getTask(1);
        assertEquals(1, task.getId(), "Id Таски не совпадают");
        assertEquals("Таска - 1", manager.getTask(1).getName(), "Имя задачи не совпадает");
    }

    @Test
    public void getNoExitTaskTest() {
        createNewFastTasks();
        assertFalse(manager.getSubTaskList().contains(O_TASK_1));
        assertNull(manager.getTask(O_TASK_1.getId()), "Сабтаски не должно быть");
    }

    @Test
    public void getEpicTest() {
        createFastTasks();
        assertNotNull(manager.getEpic(2), "Проверьте наличие эпика по идентификатору");
        Epic epic = manager.getEpic(2);
        assertEquals(2, epic.getId(), "Id Эпика не совпадают");
        assertEquals("Epic1", manager.getEpic(2).getName(), "Имя задачи не совпадает");
    }

    @Test
    public void getNoExitEpicTest() {
        createNewFastTasks();
        assertFalse(manager.getEpicList().contains(O_EPIC_1));
        assertNull(manager.getEpic(O_EPIC_1.getId()), "Эпика не должно быть");
    }

    @Test
    public void getSubTaskTest() {
        createFastTasks();
        assertNotNull(manager.getSubTask(3), "Проверьте наличие сабтаски по идентификатору");
        SubTask subTask = manager.getSubTask(3);
        assertEquals(3, subTask.getId(), "Id сабтасок не совпадают");
        assertEquals("SubTask1", manager.getSubTask(3).getName(), "Имя задачи не совпадает");
    }

    @Test
    public void getNoExitSubTaskTest() {
        createNewFastTasks();
        assertFalse(manager.getSubTaskList().contains(O_SUB_1));
        assertNull(manager.getSubTask(O_SUB_1.getId()), "Сабтаски не должно быть");
    }

    //Получение истории по просмотренным таскам/сабтаскам/эпикам
    @Test
    public void getHistoryTest() {
        createFastTasks();
        createNewFastTasks();
        viewHistory();
        assertEquals(6, manager.getHistory().size(), " Неверное количество задач");
    }


    @Test
    public void getObjectHistoryTest() {
        createFastTasks();
        createNewFastTasks();
        viewHistory();
        assertEquals(6, manager.getObjectHistory().getHistory().size(),
                " Неверное количество задач");
    }

    @Test
    public void getEmptyHistoryTest() {
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            manager.getObjectHistory().remove(task.getId());
        }
        createFastTasks();
        createNewFastTasks();
        assertEquals(0, manager.getHistory().size(), "История должна быть пуста");
    }

    @Test
    public void getEmptyObjectHistoryTest() {
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            manager.getObjectHistory().remove(task.getId());
        }
        createFastTasks();
        createNewFastTasks();
        assertEquals(0, manager.getHistory().size(), "История должна быть пуста");
    }

    //Получение списка всех подзадач определённого эпика.
    @Test
    public void getAllSubTaskByEpicTest() {
        createFastTasks();
        assertEquals(1, manager.getEpic(2).getSubTaskIds().size(),
                "Кол-во сабтасок не совпадают");
        Optional<Integer> first = manager.getEpic(2).getSubTaskIds().stream().findFirst();
        String name = manager.getSubTask(first.get()).getName();
        Integer id = manager.getSubTask(first.get()).getId();
        assertNotNull(manager.getEpic(2).getSubTaskIds());
        assertEquals(3, id);
        assertEquals("SubTask1", name);
    }

    @Test
    public void getAllSubTaskInEmptyEpicTest() {
        manager.createEpic(O_EPIC_1);
        int count = manager.getEpic(2).getSubTaskIds().size();
        assertEquals(0, count, "Сабтасок у Эпика не должно быть");
    }


    @Test
    public void getAllSubTaskInNoExitEpicTest() {
        createNewFastTasks();
        Epic epic = O_EPIC_1;
        assertNull(manager.getAllSubTaskByEpic(epic.getId()), "Эпика не должно существовать");
    }

    //Получение списка всех тасок, эпиков, сабтасок
    @Test
    public void getTaskListTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getTaskList().size());
    }

    @Test
    public void getEmptyTaskListTest() {
        assertNull(manager.getTaskList());
    }

    @Test
    public void getEpicListTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getEpicList().size());
    }

    @Test
    public void getEmptyEpicListTest() {
        assertNull(manager.getEpicList());
    }

    @Test
    public void getSubTaskListTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getSubTaskList().size());
    }

    @Test
    public void getEmptySubTaskListTest() {
        assertNull(manager.getSubTaskList());
    }

    //Удаление таски/эпика/сабтаска по идентификатору
    @Test
    public void deleteTaskByIdTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getTaskList().size(), "Вернулось неверное количество тасок");
        assertNotNull(manager.getTask(1), "Проверьте наличие таски по идентификатору");
        manager.deleteTaskById(1);
        assertEquals(1, manager.getTaskList().size(), "Вернулось неверное количество тасок");
    }

    @Test
    public void deleteNoExitTaskByIdTest() {
        createNewFastTasks();
        assertFalse(manager.getTaskList().contains(O_TASK_1), "Таска не должна быть сохранена");
        assertFalse(manager.deleteTaskById(O_TASK_1.getId()), "Таски не должно быть в списке");
    }

    @Test
    public void deleteEpicByIdTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getEpicList().size(), "Вернулось неверное количество эпиков");
        assertNotNull(manager.getEpic(5), "Проверьте наличие эпика по идентификатору");
        manager.deleteEpicById(5);
        assertEquals(1, manager.getEpicList().size(), "Вернулось неверное количество эпиков");
        assertEquals(1, manager.getSubTaskList().size(),
                "Вернулось неверное количество оставшихся сабтасок");
    }

    @Test
    public void deleteNoExitEpicByIdTest() {
        createNewFastTasks();
        assertFalse(manager.getEpicList().contains(O_EPIC_1), "Эпик не должен быть сохранен");
        assertFalse(manager.deleteEpicById(O_EPIC_1.getId()), "Эпика не должно быть в списке");
    }

    @Test
    public void deleteSubTaskByIdTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getSubTaskList().size(), "Вернулось неверное количество эпиков");
        assertNotNull(manager.getSubTask(6), "Проверьте наличие сабтаски по идентификатору");
        manager.deleteSubTaskById(6);
        assertEquals(1, manager.getSubTaskList().size(),
                "Вернулось неверное количество оставшихся сабтасок");
        assertEquals(2, manager.getEpicList().size(),
                "Вернулось неверное количество эпиков после удаления сабтасок");
        assertEquals(O_SUB_1.getFinishTime(), O_EPIC_1.getFinishTime(),
                "Время конца эпика не совпадает с датом окончания последней задачи ");
    }

    @Test
    public void deleteNoExitSubTaskByIdTest() {
        createNewFastTasks();
        assertFalse(manager.getSubTaskList().contains(O_SUB_1),
                "Сабтаска не должена быть сохранена");
        assertFalse(manager.deleteSubTaskById(O_SUB_1.getId()),
                "Сабтаски не должно быть в списке");
    }

    //Удаление всех тасок/эпиков/сабтасок
    @Test
    public void cleanAllTaskTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getTaskList().size(), "Вернулось неверное количество тасок");
        manager.cleanAllTask();
        assertNull(manager.getTaskList(), "Не все таски удалены");
    }

    @Test
    public void cleanAllTaskTestInEmptyList() {
        assertNull(manager.getTaskList(), "В списке присутствуют таски");
        assertFalse(manager.cleanAllTask(), "В списке присутствовали таски");
    }

    @Test
    public void cleanAllEpicTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getEpicList().size(), "Вернулось неверное количество эпиков'");
        manager.cleanAllEpic();
        assertNull(manager.getEpicList(), "Не все эпики удалены");
        assertNull(manager.getSubTaskList(), "Не все сабтаски удалены после удаления эпиков");
    }

    @Test
    public void cleanAllEpicTestInEmptyList() {
        assertNull(manager.getEpicList(), "В списке присутствуют эпики");
        assertFalse(manager.cleanAllEpic(), "В списке присутствовали эпики");
    }

    @Test
    public void cleanAllSubTaskTest() {
        createFastTasks();
        createNewFastTasks();
        assertEquals(2, manager.getSubTaskList().size(), "Вернулось неверное количество сабтасок'");
        manager.cleanAllSubTask();
        assertNull(manager.getSubTaskList(), "Не все эпики удалены");
        assertNull(manager.getEpic(2).getFinishTime(),
                "Продолжительность эпика не сброшена после удаления всех тасок");
        assertNull(O_EPIC_1.getFinishTime(),
                "Время конца эпика не совпадает с датом окончания последней задачи ");
    }

    @Test
    public void cleanAllSubtaskTestInEmptyList() {
        assertNull(manager.getSubTaskList(), "В списке присутствуют сабтаски");
        assertFalse(manager.cleanAllSubTask(), "В списке присутствовали сабтаски");
    }

    private void createFastTasks() {
        manager.createTask(O_TASK_1);
        manager.createEpic(O_EPIC_1);
        manager.createSubTask(O_SUB_1);
    }

    private void createNewFastTasks() {
        manager.createTask(new Task("Новая Таска 2", "Описание, описание, etc...",
                Status.NEW, LocalDateTime.now().plusDays(1), 50));
        manager.createEpic(new Epic("Новый Эпик 2", "Важный description"));
        manager.createSubTask(new SubTask("Сабтаска для Эпика2", "сделать что то",
                Status.IN_PROGRESS, 5, LocalDateTime.now().plusMonths(1), 70));
    }

    private void viewHistory() {
        manager.getEpic(2);
        manager.getEpic(5);
        manager.getTask(1);
        manager.getTask(4);
        manager.getSubTask(3);
        manager.getSubTask(6);
    }
}