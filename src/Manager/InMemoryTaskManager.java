package Manager;

import History.*;
import Model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private final HistoryManager history = new InMemoryHistoryManager();

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubtasks() {
        return subtasks;
    }

    //Метод создание Таски
    @Override
    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    // Метод создание "Эпика"
    @Override
    public Epic createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic;
    }


    // Метод создание "Сабтаски"
    @Override
    public SubTask createSubTask(SubTask sub) {
        if (epics.containsKey(sub.getEpicId())) {
            subtasks.put(sub.getId(), sub);
            (epics.get(sub.getEpicId())).getSubTaskIds().add(sub.getId());
            setEpicStatus(sub);
            return sub;
        }
        return null;
    }


    //Метод Обновление Таски
    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return task;
        }
        return null;
    }

    //Обновление Эпика
    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            return epic;
        }
        return null;
    }

    //Обновление Сабтасок
    @Override
    public SubTask updateSubTask(SubTask sub) {
        if (subtasks.containsKey(sub.getId())) {
            subtasks.put(sub.getId(), sub);
            setEpicStatus(sub);
            return sub;
        }
        return null;
    }


    //Получение таски по идентификатору
    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            history.add(tasks.get(id));
            history.getHistory();
            return tasks.get(id);
        }
        return null;
    }

    //Получение Эпика по идентификатору
    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            history.add(epics.get(id));
            history.getHistory();
            return epics.get(id);
        }
        return null;

    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<SubTask> getAllSubTaskByEpic(int id) {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subtask : epic.getSubTaskIds()) {
                subTasksList.add(subtasks.get(subtask));
            }
            return subTasksList;
        }
        return null;
    }

    //Получение Сабтаска по идентификатору
    @Override
    public SubTask getSubTask(int id) {
        if (subtasks.containsKey(id)) {
            history.add(subtasks.get(id));
            history.getHistory();
            return subtasks.get(id);
        }
        return null;
    }

    //Метод получения истории по последним 10 просмотренным задачам
    @Override
    public List<Task> getHistory() {
        return Managers.getDefaultHistory().getHistory();
    }

    //Получение списка всех тасок
    @Override
    public ArrayList<Task> getTaskList() {
        ArrayList<Task> tasksList = new ArrayList<>();
        if (!(tasks.size() == 0)) {
            for (Task task : tasks.values()) {
                tasksList.add(tasks.get(task.getId()));
            }
            return tasksList;
        }
        return null;
    }

    //Получение списка всех эпиков
    @Override
    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        if (!(epics.size() == 0)) {
            for (Epic epic : epics.values()) {
                epicsList.add(epics.get(epic.getId()));
            }
            return epicsList;
        }
        return null;
    }

    //Получение списка всех сабтасок
    @Override
    public ArrayList<SubTask> getSubTaskList() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        if (!(subtasks.size() == 0)) {
            for (SubTask subTask : subtasks.values()) {
                subTasksList.add(subtasks.get(subTask.getId()));
            }
            return subTasksList;
        }
        return null;
    }

    //Удаление таски по идентификатору
    @Override
    public boolean deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id); //Удаление задачи
            return true;
        } else {
            return false;// задача не найдена
        }
    }

    //Удаление эпика по идентификатору
    @Override
    public boolean deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> tasks = epics.get(id).getSubTaskIds();
            for (Integer task : tasks) {
                subtasks.remove(task);// Удаление всех связанных сабтасок с эпиком
            }
            epics.remove(id); // Удаление эпика
            return true;
        } else {
            return false; //Эпик не найден
        }
    }

    //Удаление саптаски по идентификатору
    @Override
    public boolean deleteSubTaskById(int id) {
        if (subtasks.containsKey(id)) {
            for (Epic epic : epics.values()) {
                if ((epic.getSubTaskIds()).contains(id)) {
                    (epic.getSubTaskIds()).remove(epic.getSubTaskIds().indexOf(id)); //отвезка сабтаски от эпика
                    if (epic.getSubTaskIds().size() == 0) {
                        epic.setStatus(Status.NEW);  //установление статуса эпика - new, если сабтасок больше нет
                    }
                }
            }
            subtasks.remove(id); //удаление сабтаски
            return true;
        } else {
            return false; //Сабтаска не найдена
        }
    }

    //Удаление всех тасок
    @Override
    public boolean cleanAllTask() {
        if (tasks.size() == 0) {
            return false;
        } else {
            tasks.clear(); //удаление всех задач
            return true;
        }
    }

    //Удаление всех эпиков
    @Override
    public boolean cleanAllEpic() {
        if (epics.size() == 0) {
            return false;
        } else {
            cleanAllSubTask(); //удаление всех сабтасок
            epics.clear(); //удаление всех эпиков
            return true;
        }
    }

    //Удаление всех сабтасок
    @Override
    public boolean cleanAllSubTask() {
        if (subtasks.size() == 0) {
            return false;
        } else {
            for (Epic epic : epics.values()) {
                epic.setStatus(Status.NEW); //установление всем эпикам статуса "NEW"
                (epic.getSubTaskIds()).clear(); //удаление всех сабтасок
            }
            subtasks.clear();
            return true;
        }
    }

    //Метод определения и установки статуса эпика в зависимости от статуса сабтасок
    private void setEpicStatus(SubTask sub) {
        int epicIdInSub = sub.getEpicId();
        Epic epic = epics.get(epicIdInSub);
        int countEpicSize = epic.getSubTaskIds().size();
        Status statusSub = sub.getStatus();
        Status nNew = Status.NEW;
        boolean compareSubStatusWithNew = statusSub.equals(nNew);
        Status done = Status.DONE;
        Status inProgress = Status.IN_PROGRESS;

        if (compareSubStatusWithNew && (countEpicSize == 1)) {
            epic.setStatus(nNew);
        } else if ((!(compareSubStatusWithNew)) && (countEpicSize == 1)) {
            epic.setStatus(statusSub);
        } else if (isClosedAllSubtaskInEpic(epicIdInSub) == true) {
            epic.setStatus(done);
        } else {
            epic.setStatus(inProgress);
        }
    }

    //метод проверки на завершенность сабтасок в эпике
    private boolean isClosedAllSubtaskInEpic(int key) {
        Epic epic = epics.get(key);
        boolean result = true;
        for (Integer oneOfsub : epic.getSubTaskIds()) {
            if (!(subtasks.get(oneOfsub).getStatus().equals(Status.DONE))) {
                result = false;
                break;
            }
        }
        return result;
    }

}





