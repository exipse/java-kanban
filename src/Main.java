import Model.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager man = new Manager();
        Task oTask;
        Epic oEpic;
        SubTask oSub;
        ArrayList<Task> aTask;
        ArrayList<Epic> aEpic;
        ArrayList<SubTask> aSub;
        boolean bEpic;
        boolean bSubtask;

        // Проверка методов Тасок
        //Создание Тасок, Эпиков
        man.createTask(new Task("Таска - 1", "Описание 1...", Task.Status.NEW));
        man.createTask(new Task("Таска - 2", "Описание 2...", Task.Status.IN_PROGRESS));
        man.createEpic(new Epic("Эпик 1", "Эпик с 2 подзадачами"));

        //Создание Сабтасок
        oSub = man.createSubTask(new SubTask("СабТаска 1из2 в эпике 1 ", "Описание сабтаски 1", Task.Status.DONE, 3));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        oSub = man.createSubTask(new SubTask("СабТаска 2из2 в эпике 1 ", "Описание сабтаски 2", Task.Status.IN_PROGRESS, 3));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        //Создание еще одного эпика с сабтаской
        man.createEpic(new Epic("Эпик 2", "Эпик с 1 подзадачей"));
        oSub = man.createSubTask(new SubTask("СабТаска 1 в эпике 2", "Описание сабтаски", Task.Status.IN_PROGRESS, 6));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        //Получение всех сабтасок Эпика по id
        aSub = man.getAllSubTaskByEpic(3);
        if (aSub == null) {
            System.out.println("\nЭпика по идентификатору не найдено");
        } else {
            System.out.println("\nУ Эпика найдены следующие сабтаски: \n" + aSub);
        }

        //Получение таски по идентификатору
        oTask = man.getTask(1);
        {
            if (oTask == null) {
                System.out.println("\nТаски под введенным идентификатором не найдено");
            } else {
                System.out.println("\nНайдена следующая таска: " + oTask);
            }
        }

        //Пролучение списка тасок
        aTask = man.getTaskList();
        if (aTask == null) {
            System.out.println("Тасок не найдено");
        } else {
            System.out.println("Получение списка Тасок:" + aTask);
        }

        // Получение списка эпиков
        aEpic = man.getEpicList();
        if (aEpic == null) {
            System.out.println("Эпиков не найдено");
        } else {
            System.out.println("\nПолучение списка Эпиков:" + aEpic);
        }

        // Получение списка сабтасок
        aSub = man.getSubTaskList();
        if (aSub == null) {
            System.out.println("Сабтасок не найдено");
        } else {
            System.out.println("\nПолучение списка Сабтасок:" + aSub);
        }

        //Обновление таски
        Task task = man.tasks.get(1);
        if (task != null) {
            task.setName("Обновленная Таска - 1");
            task.setDescription("Новое Описание 1");
            task.setStatus(Task.Status.IN_PROGRESS);
            oTask = man.updateTask(task);
            if (oTask == null) {
                System.out.println(("\nТаску не удалось обновить. Объект по идентификатору не создан"));
            } else {
                System.out.println("\nТаска изменена: " + oTask);
            }
        } else {
            System.out.println("\n" + "Таска с запрашиваемым id не найдена. Изменить таску нельзя.");
        }

        //Обновление эпика
        Epic epic = man.epics.get(3);
        if (epic != null) {
            epic.setName("Измененный Эпик 1");
            epic.setDescription("Обновленное Описание");
            oEpic = man.updateEpic(epic);
            if (oEpic == null) {
                System.out.println(("\nЭпик не удалось обновить. Объект по идентификатору не создан"));
            } else {
                System.out.println("Эпик изменен: " + oEpic);
            }
        } else {
            System.out.println("\n" + "Эпик с запрашиваемым id не найден. Изменить эпик нельзя.");
        }

        //Обновление саптаски
        SubTask sub = man.subtasks.get(5);
        if (sub != null) {
            sub.setName("НОВАЯ- СабТаска 2из2 в эпике 1");
            sub.setDescription("НОВОЕ Описание 2");
            sub.setStatus(Task.Status.DONE);
            oSub = man.updateSubTask(sub);
            if (oSub == null) {
                System.out.println(("\nСабтаску не удалось обновить. Объект по идентификатору не создан"));
            } else {
                System.out.println("СабТаска изменена: " + oSub);

            }
        } else {
            System.out.println("\n" + "Сабтаска с запрашиваемым id не найдена. Изменить сабтаску нельзя.");
        }

        //Получение списков: тасок, эпиков, сабтасок
        aTask = man.getTaskList();
        if (aTask == null) {
            System.out.println("\nТасок не найдено");
        } else {
            System.out.println("\nПолучение списка тасок: " + aTask);
        }

        aEpic = man.getEpicList();
        if (aEpic == null) {
            System.out.println("\nЭпиков не найдено");
        } else {
            System.out.println("\nПолучение списка эпиков: " + aEpic);
        }

        aSub = man.getSubTaskList();
        if (aSub == null) {
            System.out.println("\nСабтасок не найдено");
        } else {
            System.out.println("\nПолучение списка сабтасок: " + aSub);
        }

        //Удаление сабтаски и эпика
        bSubtask = man.deleteSubTaskById(7);
        if (bSubtask == true) {
            System.out.println("\nСабтаска удалена");
        } else {
            System.out.println("\nСабтаска по id не найдена");
        }

        bEpic = man.deleteEpicById(3);
        if (bEpic == true) {
            System.out.println("\nЭпик и связанные с ним сабтаски удалены");
        } else {
            System.out.println("\nЭпик не найден");
        }

        //Вывод на экран списков эпиков и списков сабтасок после удаления
        aEpic = man.getEpicList();
        if (aEpic == null) {
            System.out.println("\nЭпиков не найдено");
        } else {
            System.out.println("\nПолучение списка эпиков: " + aEpic);
        }

        aSub = man.getSubTaskList();
        if (aSub == null) {
            System.out.println("\nСабтасок не найдено");
        } else {
            System.out.println("\nПолучение списка сабтасок: " + aSub);
        }

    }
}
