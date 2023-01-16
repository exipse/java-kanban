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
    Task oTask1 =
            new Task("Таска - 1", "Описание 1...", Status.NEW, LocalDateTime.now(), 10);
    Epic oEpic1 =
            new Epic("Epic1", "Описание эпика 1");
    SubTask oSub1 =
            new SubTask("SubTask1", "сабтаска 1-го эпика", Status.NEW,
                    2, LocalDateTime.now().plusDays(5), 2);

    public void setManager(T manager) {
        this.manager = manager;
    }

    @Test
    public void createTaskTest() {
        Task task = oTask1;
        manager.getTaskList();
        manager.createTask(task);
        List<Task> tasks = manager.getTaskList();
        assertNotNull(manager.getTaskList());
        assertEquals(1, manager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(10, task.getDuration());
    }

    @Test
    public void createEpicTestWithOutTask() {
        Epic epic = oEpic1;
        manager.createEpic(epic);
        assertNotNull(epic.getStatus());
        assertEquals(1, manager.getEpicList().size(), "Эпик не сохранился.");
        assertEquals(0, manager.getEpic(epic.getId()).getSubTaskIds().size(),
                "В эпике есть сабтаски");
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(new ArrayList<>(), epic.getSubTaskIds(),
                "Ошибка. В созданном эпике обнаружились сабтаски");
    }

    @Test
    public void createEpicTestWithTaskInStatusNew() {
        createFastTasks();
        assertNotNull(manager.getEpic(2), "Проверьте наличие эпика по идентификатору");
        Epic epic = manager.getEpic(2);
        List<Epic> epics = manager.getEpicList();
        assertEquals(Status.NEW, epic.getStatus(), "Эпик не в Статусе - NEW");
        assertEquals(1, epics.size(), "Эпик не сохранился.");
        assertEquals(1, epic.getSubTaskIds().size(), "В эпике нет сабтасок");
        assertEquals(2, epic.getDuration(), "Неверная продолжительность эпика относительно сабтаски");
    }

    @Test
    public void createEpicTestWithTaskInStatusDone() {
        createFastTasks();
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
        SubTask newsubTask =
                new SubTask("SubTask2", "сабтаска 1-го эпика", Status.DONE,
                        Integer.MAX_VALUE, LocalDateTime.now().plusDays(20), 77);
        manager.createSubTask(newsubTask);
        assertNull(manager.getSubTaskList(), "Сабтаска не должна быть создана");
    }

    @Test
    public void createSubTaskTest() {
        Epic epic = oEpic1;
        manager.createEpic(epic);
        SubTask subtask = oSub1;
        manager.createSubTask(subtask);
        assertNotNull(subtask.getStatus());
        assertEquals(1, manager.getSubTaskList().size(), "Сабтаска не сохранилась");
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(oSub1.getEpicId(), oEpic1.getId(), "Эпик у сабтаски не найден");
        assertEquals(manager.getEpic(subtask.getEpicId()).getSubTaskIds().get(0), subtask.getId(),
                "Сабкаска не привязана к эпику");
    }

    //Метод Обновление Таски, Эпика, Сабтаски
    @Test
    public void updateTaskTest() {
        Task task = oTask1;
        manager.createTask(task);
        task.setName("Обновленная Таска - 1");
        task.setDescription("Новое Описание 1");
        task.setStatus(Status.IN_PROGRESS);
        task.setDuration(55);
        manager.updateTask(task);
        assertNotNull(task.getStatus());
        assertEquals(55, task.getDuration(), " Продолжительность таски не изменилась");
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void updateNoExitTaskTest() {
        Task task = oTask1;
        assertNull(manager.getTaskList(), "Тасок не должно быть");
        task.setName("Несуществующая Таска");
        task.setDescription("Новое Описание 1");
        task.setStatus(Status.IN_PROGRESS);
        task.setDuration(77);
        manager.updateTask(task);
        assertNull(manager.getTaskList(), "Тасок не должно быть");
    }

    @Test
    public void updateEpicTest() {
        Epic epic = oEpic1;
        manager.createEpic(epic);
        epic.setName("Измененный Эпик 1");
        epic.setDescription("Обновленное Описание");
        manager.updateEpic(epic);
        List<Epic> epics = manager.getEpicList();
        assertNotNull(epic.getStatus());
        assertEquals(1, manager.getEpicList().size(), "Эпик не сохранился.");
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(new ArrayList<>(), epic.getSubTaskIds(),
                "Ошибка. В созданном эпике обнаружились сабтаски");
        System.out.println("");
    }

    @Test
    public void updateNoExitEpicTest() {
        Epic epic = oEpic1;
        assertNull(manager.getEpicList(), "Эпиков не должно быть");
        epic.setName("Несуществующий Эпик");
        manager.updateEpic(epic);
        assertNull(manager.getEpicList(), "Эпиков не должно быть");
    }

    @Test
    public void updateSubTaskTest() {
        manager.createEpic(oEpic1);
        manager.createSubTask(oSub1);
        SubTask subtask = oSub1;
        subtask.setName("Обновленная Таска - 1");
        subtask.setDescription("Новое Описание 1");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setStartTime(subtask.getStartTime().plusDays(10));
        subtask.setDuration(60);
        manager.updateSubTask(subtask);
        assertNotNull(subtask.getStatus());
        assertEquals(60, subtask.getDuration(), " Продолжительность сабтаски не поменялась");
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
        assertEquals(manager.getEpic(subtask.getEpicId()).getSubTaskIds().get(0), subtask.getId(),
                "Сабкаска не привязана к эпику");
        assertEquals(oSub1.getFinishTime(), oEpic1.getFinishTime(),
                "Время конца эпика не совпадает с датом окончания последней задачи ");
    }

    @Test
    public void updateNoExitSubTaskTest() {
        SubTask subtask = oSub1;
        assertNull(manager.getSubTaskList(), "Сабтасок не должно быть");
        subtask.setName("Несуществующая Сабтаска");
        manager.updateSubTask(subtask);
        assertNull(manager.getSubTaskList(), "Сабтасок не должно быть");
    }

    //Получение таски/эпика/сабтаска по идентификатору
    @Test
    public void getTaskTest() {
        createFastTasks();
        assertNotNull(manager.getTask(1), "Проверьте наличие таски по идентификатору");
        Task task = manager.getTask(1);
        assertEquals(1, manager.getTask(1).getId(), "Id Таски не совпадают");
        assertEquals("Таска - 1", manager.getTask(1).getName(), "Имя задачи не совпадает");
    }

    @Test
    public void getNoExitTaskTest() {
        createNewFastTasks();
        Task task = oTask1;
        assertEquals(false, manager.getSubTaskList().contains(task));
        assertNull(manager.getTask(task.getId()), "Сабтаски не должно быть");
    }

    @Test
    public void getEpicTest() {
        createFastTasks();
        assertNotNull(manager.getEpic(2), "Проверьте наличие эпика по идентификатору");
        Epic epic = manager.getEpic(2);
        assertEquals(2, manager.getEpic(2).getId(), "Id Эпика не совпадают");
        assertEquals("Epic1", manager.getEpic(2).getName(), "Имя задачи не совпадает");
    }

    @Test
    public void getNoExitEpicTest() {
        createNewFastTasks();
        Epic epic = oEpic1;
        assertEquals(false, manager.getEpicList().contains(epic));
        assertNull(manager.getEpic(epic.getId()), "Эпика не должно быть");
    }

    @Test
    public void getSubTaskTest() {
        createFastTasks();
        assertNotNull(manager.getSubTask(3), "Проверьте наличие сабтаски по идентификатору");
        SubTask subTask = manager.getSubTask(3);
        assertEquals(3, manager.getSubTask(3).getId(), "Id сабтасок не совпадают");
        assertEquals("SubTask1", manager.getSubTask(3).getName(), "Имя задачи не совпадает");
    }

    @Test
    public void getNoExitSubTaskTest() {
        createNewFastTasks();
        SubTask subTask = oSub1;
        assertEquals(false, manager.getSubTaskList().contains(subTask));
        assertNull(manager.getSubTask(subTask.getId()), "Сабтаски не должно быть");
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
        Epic epic = oEpic1;
        manager.createEpic(epic);
        int count = manager.getEpic(2).getSubTaskIds().size();
        assertEquals(0, count, "Сабтасок у Эпика не должно быть");
    }


    @Test
    public void getAllSubTaskInNoExitEpicTest() {
        createNewFastTasks();
        Epic epic = oEpic1;
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
        Task task = oTask1;
        assertEquals(false, manager.getTaskList().contains(task), "Таска не должна быть сохранена");
        assertEquals(false, manager.deleteTaskById(task.getId()), "Таски не должно быть в списке");
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
        Epic epic = oEpic1;
        assertEquals(false, manager.getEpicList().contains(epic), "Эпик не должен быть сохранен");
        assertEquals(false, manager.deleteEpicById(epic.getId()), "Эпика не должно быть в списке");
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
        assertEquals(oSub1.getFinishTime(), oEpic1.getFinishTime(),
                "Время конца эпика не совпадает с датом окончания последней задачи ");
    }

    @Test
    public void deleteNoExitSubTaskByIdTest() {
        createNewFastTasks();
        SubTask subTask = oSub1;
        assertEquals(false, manager.getSubTaskList().contains(subTask),
                "Сабтаска не должена быть сохранена");
        assertEquals(false, manager.deleteSubTaskById(subTask.getId()),
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
        assertEquals(false, manager.cleanAllTask(), "В списке присутствовали таски");
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
        assertEquals(false, manager.cleanAllEpic(), "В списке присутствовали эпики");
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
        assertNull(oEpic1.getFinishTime(),
                "Время конца эпика не совпадает с датом окончания последней задачи ");
    }

    @Test
    public void cleanAllSubtaskTestInEmptyList() {
        assertNull(manager.getSubTaskList(), "В списке присутствуют сабтаски");
        assertEquals(false, manager.cleanAllSubTask(), "В списке присутствовали сабтаски");
    }

    private void createFastTasks() {
        manager.createTask(oTask1);
        manager.createEpic(oEpic1);
        manager.createSubTask(oSub1);
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