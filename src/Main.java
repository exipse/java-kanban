import Model.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manInMem = new InMemoryTaskManager();
        Task oTask;
        Epic oEpic;
        SubTask oSub;
        ArrayList<Task> aTask;
        ArrayList<Epic> aEpic;
        ArrayList<SubTask> aSub;
        List<Task> history;
        boolean bEpic;
        boolean bSubtask;

        // Проверка методов Тасок
        //Создание Тасок, Эпиков
        manInMem.createTask(new Task("Таска - 1", "Описание 1...", Status.NEW));
        manInMem.createTask(new Task("Таска - 2", "Описание 2...", Status.IN_PROGRESS));
        manInMem.createEpic(new Epic("Эпик 1", "Эпик с 2 подзадачами"));

        //Создание Сабтасок
        oSub = manInMem.createSubTask(new SubTask("СабТаска 1из2 в эпике 1 ", "Описание сабтаски 1", Status.DONE, 3));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        oSub = manInMem.createSubTask(new SubTask("СабТаска 2из2 в эпике 1 ", "Описание сабтаски 2", Status.IN_PROGRESS, 3));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        //Создание еще одного эпика с сабтаской
        manInMem.createEpic(new Epic("Эпик 2", "Эпик с 1 подзадачей"));
        oSub = manInMem.createSubTask(new SubTask("СабТаска 1 в эпике 2", "Описание сабтаски", Status.IN_PROGRESS, 6));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        //Получение всех сабтасок Эпика по id
        aSub = manInMem.getAllSubTaskByEpic(3);
        if (aSub == null) {
            System.out.println("\nЭпика по идентификатору не найдено");
        } else {
            System.out.println("\nУ Эпика найдены следующие сабтаски: \n" + aSub);
        }

        //Получение таски по идентификатору
        oTask = manInMem.getTask(1);
        {
            if (oTask == null) {
                System.out.println("\nТаски под введенным идентификатором не найдено");
            } else {
                System.out.println("\nНайдена следующая таска: " + oTask);
            }
        }

        manInMem.getTask(2);
        manInMem.getEpic(3);
        manInMem.getEpic(3);
        manInMem.getSubTask(5);
        manInMem.getTask(1);
        manInMem.getTask(2);
        manInMem.getEpic(3);
        manInMem.getEpic(3);
        manInMem.getSubTask(5);
        manInMem.getSubTask(5);

        //Пролучение списка тасок
        aTask = manInMem.getTaskList();
        if (aTask == null) {
            System.out.println("Тасок не найдено");
        } else {
            System.out.println("Получение списка Тасок:" + aTask);
        }

        // Получение списка эпиков
        aEpic = manInMem.getEpicList();
        if (aEpic == null) {
            System.out.println("Эпиков не найдено");
        } else {
            System.out.println("\nПолучение списка Эпиков:" + aEpic);
        }

        // Получение списка сабтасок
        aSub = manInMem.getSubTaskList();
        if (aSub == null) {
            System.out.println("Сабтасок не найдено");
        } else {
            System.out.println("\nПолучение списка Сабтасок:" + aSub);
        }

        //Обновление таски
        Task task = (Task) manInMem.tasks.get(1);
        if (task != null) {
            task.setName("Обновленная Таска - 1");
            task.setDescription("Новое Описание 1");
            task.setStatus(Status.IN_PROGRESS);
            oTask = manInMem.updateTask(task);
            if (oTask == null) {
                System.out.println(("\nТаску не удалось обновить. Объект по идентификатору не создан"));
            } else {
                System.out.println("\nТаска изменена: " + oTask);
            }
        } else {
            System.out.println("\n" + "Таска с запрашиваемым id не найдена. Изменить таску нельзя.");
        }

        //Обновление эпика
        Epic epic = (Epic) manInMem.epics.get(3);
        if (epic != null) {
            epic.setName("Измененный Эпик 1");
            epic.setDescription("Обновленное Описание");
            oEpic = manInMem.updateEpic(epic);
            if (oEpic == null) {
                System.out.println(("\nЭпик не удалось обновить. Объект по идентификатору не создан"));
            } else {
                System.out.println("Эпик изменен: " + oEpic);
            }
        } else {
            System.out.println("\n" + "Эпик с запрашиваемым id не найден. Изменить эпик нельзя.");
        }

        //Обновление саптаски
        SubTask sub = (SubTask) manInMem.subtasks.get(5);
        if (sub != null) {
            sub.setName("НОВАЯ- СабТаска 2из2 в эпике 1");
            sub.setDescription("НОВОЕ Описание 2");
            sub.setStatus(Status.DONE);
            oSub = manInMem.updateSubTask(sub);
            if (oSub == null) {
                System.out.println(("\nСабтаску не удалось обновить. Объект по идентификатору не создан"));
            } else {
                System.out.println("СабТаска изменена: " + oSub);

            }
        } else {
            System.out.println("\n" + "Сабтаска с запрашиваемым id не найдена. Изменить сабтаску нельзя.");
        }

        //Получение списков: тасок, эпиков, сабтасок
        aTask = manInMem.getTaskList();
        if (aTask == null) {
            System.out.println("\nТасок не найдено");
        } else {
            System.out.println("\nПолучение списка тасок: " + aTask);
        }

        aEpic = manInMem.getEpicList();
        if (aEpic == null) {
            System.out.println("\nЭпиков не найдено");
        } else {
            System.out.println("\nПолучение списка эпиков: " + aEpic);
        }

        aSub = manInMem.getSubTaskList();
        if (aSub == null) {
            System.out.println("\nСабтасок не найдено");
        } else {
            System.out.println("\nПолучение списка сабтасок: " + aSub);
        }

        //Удаление сабтаски и эпика
        bSubtask = manInMem.deleteSubTaskById(7);
        if (bSubtask == true) {
            System.out.println("\nСабтаска удалена");
        } else {
            System.out.println("\nСабтаска по id не найдена");
        }

        bEpic = manInMem.deleteEpicById(3);
        if (bEpic == true) {
            System.out.println("\nЭпик и связанные с ним сабтаски удалены");
        } else {
            System.out.println("\nЭпик не найден");
        }

        //Вывод на экран списков эпиков и списков сабтасок после удаления
        aEpic = manInMem.getEpicList();
        if (aEpic == null) {
            System.out.println("\nЭпиков не найдено");
        } else {
            System.out.println("\nПолучение списка эпиков: " + aEpic);
        }

        aSub = manInMem.getSubTaskList();
        if (aSub == null) {
            System.out.println("\nСабтасок не найдено");
        } else {
            System.out.println("\nПолучение списка сабтасок: " + aSub);
        }

       history = manInMem.getHistory();
        for (Task taskHistory : history) {
            System.out.println(taskHistory);
        }
    }
}
