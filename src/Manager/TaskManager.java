package Manager;

import Model.Epic;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    //Метод создание Таски, Эпика, Сабтаски
    public Task createTask(Task task);

    public Epic createEpic(Epic epic);

    public SubTask createSubTask(SubTask sub);

    //Метод Обновление Таски, Эпика, Сабтаски
    public Task updateTask(Task task);

    public Epic updateEpic(Epic epic);

    public SubTask updateSubTask(SubTask sub);

    //Получение таски/эпика/сабтаска по идентификатору
    public Task getTask(int id);

    public Epic getEpic(int id);

    public SubTask getSubTask(int id);

    //Получение истории по просмотренным таскам/сабтаскам/эпикам
    public List<Task> getHistory();

    //Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getAllSubTaskByEpic(int id);

    //Получение списка всех тасок, эпиков, сабтасок
    public ArrayList<Task> getTaskList();

    public ArrayList<Epic> getEpicList();

    public ArrayList<SubTask> getSubTaskList();

    //Удаление таски/эпика/сабтаска по идентификатору
    public boolean deleteTaskById(int id);

    public boolean deleteEpicById(int id);

    public boolean deleteSubTaskById(int id);

    //Удаление всех тасок/эпиков/сабтасок
    public boolean cleanAllTask();

    public boolean cleanAllEpic();

    public boolean cleanAllSubTask();

}



