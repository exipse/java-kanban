import Model.Epic;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subtasks = new HashMap<>();


    public Task createTask(Task task) {                     // Создание Таски
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {                     // Создание "Эпика"
        epics.put(epic.getId(), epic);
        return epic;

    }

    public SubTask createSubTask(SubTask sub) {              // Создание "Сабтаски"
        if (epics.containsKey(sub.getEpicId()) == false) {
            System.out.println("\nНельзя создать Subtask. Эпика с id = " + sub.getEpicId() + " не существует");
        } else {
            subtasks.put(sub.getId(), sub);
            (epics.get(sub.getEpicId())).getSubTaskId().add(sub.getId());
            // проверки, на изменение статуса у эпика
            if ((sub.getStatus().equals(Task.Status.NEW)) && (epics.get(sub.getEpicId()).getSubTaskId().size() == 1)) {
                (epics.get(sub.getEpicId())).setStatus(Task.Status.NEW);
            } else if ((!(sub.getStatus().equals(Task.Status.NEW)))
                    && (epics.get(sub.getEpicId()).getSubTaskId().size() == 1)) {
                (epics.get(sub.getEpicId())).setStatus(sub.getStatus());
            } else if (isClosedAllSubtaskInEpic(sub.getEpicId()) == true) {
                (epics.get(sub.getEpicId())).setStatus(Task.Status.DONE);
            } else {
                (epics.get(sub.getEpicId())).setStatus(Task.Status.IN_PROGRESS);
            }
        }
        return sub;

    }

    private boolean isClosedAllSubtaskInEpic(int key) {   //метод проверки на завершенность сабтасок в эпике
        Epic epic = epics.get(key);
        boolean result = true;
        for (Integer oneOfsub : epic.getSubTaskId()) {
            if (!(subtasks.get(oneOfsub).getStatus().equals(Task.Status.DONE))) {
                result = false;
                break;
            }
        }
        return result;
    }

    public Task updateTask(Task task) {                     //Обновление Таски
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("\nТаска изменена: " + task);
        } else {
            System.out.println("\nТаску не удалось обновить. Объект не найден");
        }
        return task;
    }

    public Epic updateEpic(Epic epic) {                     //Обновление Таски
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            System.out.println("\nЭпик изменен: " + epic);
        } else {
            System.out.println("\nЭпик не удалось обновить. Эпик не найден");
        }
        return epic;
    }

    public SubTask updateSubTask(SubTask sub) {                //Обновление Сабтасок
        if (subtasks.containsKey(sub.getId())) {
            subtasks.put(sub.getId(), sub);
            System.out.println("\nСабтаска изменена: " + sub);
            if ((sub.getStatus().equals(Task.Status.NEW)) && (epics.get(sub.getEpicId()).getSubTaskId().size() == 1)) {
                (epics.get(sub.getEpicId())).setStatus(Task.Status.NEW);
            } else if ((!(sub.getStatus().equals(Task.Status.NEW)))
                    && (epics.get(sub.getEpicId()).getSubTaskId().size() == 1)) {
                (epics.get(sub.getEpicId())).setStatus(sub.getStatus());
            } else if (isClosedAllSubtaskInEpic(sub.getEpicId()) == true) {
                (epics.get(sub.getEpicId())).setStatus(Task.Status.DONE);
            } else {
                (epics.get(sub.getEpicId())).setStatus(Task.Status.IN_PROGRESS);
            }
        } else {
            System.out.println("\nСабтаску не удалось обновить. Сабтаска не найдена");
        }
        return sub;
    }

    public void getTask(int id) {                            //Получение таски по идентификатору
        if (tasks.containsKey(id) == true) {
            System.out.println("\nПод id = " + id + " найдена следующая таска:\n" + tasks.get(id));
        } else {
            System.out.println("\n" + "Задачи с идентификатором " + id + " не найдено.");
        }
    }

    public void getEpic(int id) {                            //Получение Эпика по идентификатору
        if (epics.containsKey(id) == true) {
            System.out.println("\nПод id = " + id + " найден следующий эпик:\n" + epics.get(id));
        } else {
            System.out.println("\n" + "Эпика с идентификатором " + id + " не найдено.");
        }
    }

    public void getAllSubTaskByEpic(int id) {   //Получение списка всех подзадач определённого эпика.
        if (epics.containsKey(id) == true) {
            System.out.println("\nУ Эпика с id = " + id + " найдены следующие сабтаски:");
            Epic epic = epics.get(id);
            for (Integer subtask : epic.getSubTaskId()) {
                System.out.println(subtasks.get(subtask));
            }

        } else {
            System.out.println("\n" + "Эпика с идентификатором " + id + " не найдено.");
        }
    }

    public void getSubTask(int id) {                            //Получение Сабтасков по идентификатору
        if (subtasks.containsKey(id) == true) {
            System.out.println("\nПод id = " + id + " найден следующий сабтаск:\n" + subtasks.get(id));
        } else {
            System.out.println("\n" + "Саптаски с идентификатором " + id + " не найдено.");
        }
    }

    public void getTaskList() {                               //Получение списка всех тасок
        System.out.println("\nПолучение списка задача:");
        if (tasks.size() == 0) {
            System.out.println("\nЗадач не найдено");
        } else {
            for (Task task : tasks.values()) {
                System.out.println(task);
            }
        }
    }

    public void getEpicList() {                               //Получение списка всех эпиков
        System.out.println("\nПолучение списка эпиков:");
        if (epics.size() == 0) {
            System.out.println("\nЭпиков не найдено");
        } else {
            for (Epic epic : epics.values()) {
                System.out.println(epic);
            }
        }
    }

    public void getSubTaskList() {                               //Получение списка всех сабтасок
        System.out.println("\nПолучение списка сабтасок:");
        if (subtasks.size() == 0) {
            System.out.println("Cабтасок не найдено");
        } else {
            for (SubTask subTask : subtasks.values()) {
                System.out.println(subTask);
            }
        }
    }

    public void deleteTaskById(int id) {                          //Удаление таски по идентификатору
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("\nЗадача под идентификатором № " + id + " удалена");
        } else {
            System.out.println("\nЗадачи с идентификатором № " + id + " не найдено. \nУдаление невозможно.");
        }
    }

    public void deleteEpicById(int id) {                          //Удаление эпика по идентификатору
        if (epics.containsKey(id)) {
            ArrayList<Integer> tasks = epics.get(id).getSubTaskId();
            for (Integer task : tasks) {
                subtasks.remove(task);
            }
            epics.remove(id);
            System.out.println("\nЭпик под идентификатором № " + id + " и все связанные с ним сабтаски удалены");
        } else {
            System.out.println("\nЭпик с идентификатором № " + id + " не найден. \nУдаление невозможно.");
        }
    }

    public void deleteSubTaskById(int id) {                  //Удаление саптаски по идентификатору
        if (subtasks.containsKey(id)) {
            for (Epic epic : epics.values()) {
                if (!((epic.getSubTaskId()).contains(id) == false)) {
                    (epic.getSubTaskId()).remove(epic.getSubTaskId().indexOf(id));
                    if (epic.getSubTaskId().size() == 0) {
                        epic.setStatus(Task.Status.NEW);
                    }
                }
            }
            subtasks.remove(id);
            System.out.println("\nСабтаск под идентификатором № " + id + " удалена");
        } else {
            System.out.println("\nСабтаск с идентификатором № " + id + " не найден. \nУдаление невозможно.");
        }
    }

    public void cleanAllTask() {                             //Удаление всех тасок
        if (tasks.size() == 0) {
            System.out.println("\nСписок задач пуст!");
        } else {
            tasks.clear();
            System.out.println("\nВесь список задач очищен!");
        }

    }

    public void cleanAllEpic() {                             //Удаление всех эпиков
        if (epics.size() == 0) {
            System.out.println("\nСписок эпиков пуст!");
        } else {
            cleanAllSubTask();
            epics.clear();
            System.out.println("\nВесь список эпиков и связанных сабтасок очищен!");
        }

    }

    public void cleanAllSubTask() {                         //Удаление всех сабтасок
        if (subtasks.size() == 0) {
            System.out.println("\nСписок сабтасков пуст!");
        } else {
            for (Epic epic : epics.values()) {
                epic.setStatus(Task.Status.NEW);
                (epic.getSubTaskId()).clear();
            }
            subtasks.clear();
            System.out.println("\nВесь список сабтасок очищен!");
        }

    }

}





