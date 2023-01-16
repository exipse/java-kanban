package Manager;

import History.HistoryManager;
import History.InMemoryHistoryManager;
import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import exception.ValidateException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private final HistoryManager history = new InMemoryHistoryManager();

    Comparator<Task> comparator = (Task o1, Task o2) -> {
        return o1.getStartTime().compareTo(o2.getStartTime());
    };
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    //Возврат список задач и подзадач в заданном порядке
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    //Метод создание Таски
    @Override
    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        if (!(task.getStartTime() == null)) {
            try {
                if (isCorrectValidate(task)) {
                    getPrioritizedTasks().add(task);
                }
            } catch (ValidateException e) {
                System.out.println(e.getMessage());
            }
        }
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
            if (!(sub.getStartTime() == null)) {
                try {
                    if (isCorrectValidate(sub)) {
                        getPrioritizedTasks().add(sub);
                        updateEpicDuring(sub.getEpicId());
                    }
                } catch (ValidateException e) {
                    System.out.println(e.getMessage());
                }
            }
            return sub;
        }
        return null;
    }


    //Метод Обновление Таски
    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task old = tasks.get(task.getId());
            tasks.put(task.getId(), task);
            if (!(task.getStartTime() == null)) {
                try {
                    if (isCorrectValidate(task)) {
                        getPrioritizedTasks().remove(old);
                        getPrioritizedTasks().add(task);
                    }
                } catch (ValidateException e) {
                    System.out.println(e.getMessage());
                }
            }
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
            SubTask old = subtasks.get(sub.getId());
            subtasks.put(sub.getId(), sub);
            setEpicStatus(sub);
            if (!(sub.getStartTime() == null)) {
                try {
                    if (isCorrectValidate(sub)) {
                        getPrioritizedTasks().remove(old);
                        getPrioritizedTasks().add(sub); // добавление в лист с приоритезации /пока без фильтрации
                    }
                } catch (ValidateException e) {
                    System.out.println(e.getMessage());
                }
            }
            updateEpicDuring(sub.getEpicId());
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

    @Override
    public HistoryManager getObjectHistory() {
        return history;
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
            removeTaskInPrioritizedList(id);
            tasks.remove(id); //Удаление задачи
            history.remove(id);
            return true;
        } else {
            return false;// задача не найдена
        }
    }

    //Удаление эпика по идентификатору
    @Override
    public boolean deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subTasks = epics.get(id).getSubTaskIds();
            for (Integer subTask : subTasks) {
                removeSubTaskInPrioritizedList(subTask);
                subtasks.remove(subTask);// Удаление всех связанных сабтасок с эпиком
                history.remove(subTask);
            }
            epics.remove(id); // Удаление эпика
            history.remove(id);
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
            removeSubTaskInPrioritizedList(id);
            updateEpicDuring(subtasks.get(id).getEpicId());
            history.remove(id);
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
            for (Integer integer : tasks.keySet()) {
                removeTaskInPrioritizedList(integer);
            }
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
                List<Integer> list = epic.getSubTaskIds();
                for (Integer subId : list) {
                    removeSubTaskInPrioritizedList(subId);
                }
                list.clear(); //удаление всех сабтасок
                for (Integer integer : epics.keySet()) {
                    updateEpicDuring(integer);
                }
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
        } else if (isClosedAllSubtaskInEpic(epicIdInSub)) {
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

    //Обновление информации по продолжительности эпика.
    private void updateEpicDuring(int epicId) {
        Epic currentEpic = epics.get(epicId);

        List<Integer> epicTasksList = currentEpic.getSubTaskIds();
        List<SubTask> subtasksByEpicNoFilter = new ArrayList<>();
        List<SubTask> subtasksByEpicFilter = new ArrayList<>();


        if (epicTasksList.isEmpty()) {
            getDefaultTimeEpic(epicId);
            return;
        }
        for (Integer id : epicTasksList) {
            subtasksByEpicNoFilter.add(subtasks.get(id));
        }
        for (SubTask noFilter : subtasksByEpicNoFilter) {
            if (!(noFilter.getStartTime() == null)) {
                subtasksByEpicFilter.add(noFilter);
            }
        }
        if (subtasksByEpicFilter.isEmpty()) {
            getDefaultTimeEpic(epicId);
            return;
        }
        LocalDateTime a = LocalDateTime.MIN;
        Task Aa = null;
        LocalDateTime b = LocalDateTime.MAX;
        long sum = 0;
        for (SubTask subTaskbyEpic : subtasksByEpicFilter) {
            if (subTaskbyEpic.getStartTime().isBefore(b)) {
                b = subTaskbyEpic.getStartTime();// 3
            }
            if (subTaskbyEpic.getStartTime().isAfter(a)) {
                a = subTaskbyEpic.getStartTime();
                Aa = subTaskbyEpic;//1232
            }
            sum = sum + subTaskbyEpic.getDuration();
        }
        currentEpic.setStartTime(b);
        currentEpic.setDuration(sum);
        if (subtasksByEpicFilter.size() == 1) {
            currentEpic.setFinishTime(b.plusMinutes(sum));
        } else {
            currentEpic.setFinishTime(Aa.getFinishTime());
        }
    }

    private void getDefaultTimeEpic(int epicId) {
        Epic epic = epics.get(epicId);
        epic.setStartTime(null);
        epic.setDuration(0);
        epic.setFinishTime(null);
    }

    private void removeSubTaskInPrioritizedList(int id) {
        SubTask t = subtasks.get(id);
        getPrioritizedTasks().removeIf(subtask ->
        {
            if (subtask.equals(t)) {
                return getPrioritizedTasks().contains(subtask);
            }
            return false;
        });
    }

    private void removeTaskInPrioritizedList(int id) {
        Task t = tasks.get(id);
        getPrioritizedTasks().removeIf(task ->
        {
            if (task.equals(t)) {
                return getPrioritizedTasks().contains(task);
            }
            return false;
        });
    }

    private boolean isCorrectValidate(Task newTask) throws ValidateException {
        List<Task> tasks = prioritizedTasks.stream().collect(Collectors.toList());
        boolean isValided = true;
        for (Task task : tasks) {
            if (newTask.getId() == task.getId()) { //для обновления пропуск сравнения
                continue;
            }
            // 01.01 - 10.01  &&  05.01 - 15.01
            if (newTask.getStartTime().isBefore(task.getStartTime()) &&
                    newTask.getFinishTime().isAfter(task.getStartTime())) {
                isValided = false;
            }
            // 05.01 - 07.01  &&  01.01 - 10.01
            else if (newTask.getStartTime().isAfter(task.getStartTime()) &&
                    newTask.getFinishTime().isBefore(task.getFinishTime())) {
                isValided = false;
            }
            // 09.01 - 15.01  &&  01.01 - 10.01
            else if ((newTask.getStartTime().isBefore(task.getFinishTime())) &&
                    newTask.getFinishTime().isAfter(task.getFinishTime())) {
                isValided = false;
            }
            // 01.01 - 10.01  &&  05.01 - 06.01
            else if ((newTask.getStartTime().isBefore(task.getStartTime())) &&
                    newTask.getFinishTime().isAfter(task.getFinishTime())) {
                isValided = false;
            }
            if (!(isValided)) {
                throw new ValidateException("Задачу id=" + newTask.getId()
                        + " нельзя добавить в список приоритетов. Пересечение с задачей id=" + task.getId());
            }
        }
        return true;
    }
}





