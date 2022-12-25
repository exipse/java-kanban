import Manager.FileBackedTasksManager;
import Manager.Managers;
import Manager.TaskManager;
import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import exception.ManagerLoadException;

import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Task> history;

        TaskManager file = Managers.getDefault();
        Managers.getDefault();
        file.createTask(new Task("Task1", "просто Таска1", Status.NEW));
        file.createEpic(new Epic("Epic1", "Описание эпика 1"));
        file.createSubTask(new SubTask("SubTask1", "сабтаска 1-го эпика №1 ", Status.DONE, 2));
        file.createEpic(new Epic("Epic2", "Эпик без сабтасок"));
        file.createEpic(new Epic("Epic3", "Описание эпика 3"));
        file.createSubTask(new SubTask("SubTask2", "сабтаска 3-го эпика", Status.DONE, 5));
        file.createSubTask(new SubTask("SubTask3", "еще 1 сабтаска 3-го эпика", Status.DONE, 5));
        file.createTask(new Task("Task2", "просто Таска2", Status.NEW));
        file.createSubTask(new SubTask("SubTask4", "сабтаска 1-го эпика №2", Status.DONE, 2));
        file.getEpic(2);
        file.getSubTask(6);
        file.getTask(8);

//        try {
//            FileBackedTasksManager file1 = FileBackedTasksManager.loadFromFile((Paths.get("src/files/saveFileStatic.txt")).toFile());
//            System.out.println("\nВосстановленная история просмотра из файла:");
//            history = file1.getHistory();
//            for (Task taskHistory : history) {
//                System.out.println(taskHistory);
//            }
//        } catch (ManagerLoadException e) {
//            e.getMessage();
//        }
    }

}
