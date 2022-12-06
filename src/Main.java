import Model.*;
import Manager.InMemoryTaskManager;


import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manInMem = new InMemoryTaskManager();
        SubTask oSub;
        List<Task> history; // заменить потом тип листа
        boolean bEpic;
        boolean bSubtask;

        // Проверка методов Тасок
        //Создание двух задач, эпика с тремя подзадачами и эпика без подзадач;
        manInMem.createTask(new Task("Таска - 1", "Описание 1...", Status.NEW));
        manInMem.createTask(new Task("Таска - 2", "Описание 2...", Status.IN_PROGRESS));

        manInMem.createEpic(new Epic("Эпик 1", "Эпик с 3 подзадачами"));

        //Создание Сабтасок
        oSub = manInMem.createSubTask(new SubTask("СабТаска 1из3 в эпике 1 ", "Описание сабтаски 1", Status.DONE, 3));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        oSub = manInMem.createSubTask(new SubTask("СабТаска 2из3 в эпике 1 ", "Описание сабтаски 2", Status.IN_PROGRESS, 3));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        oSub = manInMem.createSubTask(new SubTask("СабТаска 3из3 в эпике 1 ", "Описание сабтаски 3", Status.IN_PROGRESS, 3));
        if (oSub == null) {
            System.out.println(("\nНельзя создать Subtask, т.к Эпика не существует"));
        }

        manInMem.createEpic(new Epic("Эпик 2", "Эпик без подзадач"));

        //Запрос созданных задач несколько раз в разном порядке;
        manInMem.getTask(1);
        manInMem.getTask(2);
        manInMem.getEpic(3);
        manInMem.getSubTask(4);
        manInMem.getSubTask(5);
        manInMem.getSubTask(6);
        manInMem.getEpic(7);
        manInMem.getSubTask(6);
        manInMem.getSubTask(4);
        manInMem.getTask(2);
        manInMem.getEpic(3);

        //Вывод на экран истории просмотра тасок/сабтасок/эпиков
        history = manInMem.getHistory();
        for (Task taskHistory : history) {
            System.out.println(taskHistory);
        }

        bSubtask = manInMem.deleteTaskById(1);
        if (bSubtask == true) {
            System.out.println("\nТаска удалена");
        } else {
            System.out.println("\nТаска по id не найдена");
        }

        bEpic = manInMem.deleteEpicById(3);
        if (bEpic == true) {
            System.out.println("\nЭпик и связанные с ним сабтаски удалены\n");
        } else {
            System.out.println("\nЭпик не найден");
        }

        history = manInMem.getHistory();
        for (Task taskHistory : history) {
            System.out.println(taskHistory);
        }
    }

}
