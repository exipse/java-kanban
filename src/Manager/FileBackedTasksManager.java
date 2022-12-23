package Manager;

import History.HistoryManager;
import Model.*;
import exception.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private Path newFile;

    public FileBackedTasksManager(Path newFile) {
        this.newFile = newFile;
    }

    public Path getNewFile() {
        return newFile;
    }

    //метод автосохранения
    public void save() {
        List<String> allTasks = new ArrayList<>();
        List<Task> tasks = getTaskList();
        List<Epic> epics = getEpicList();
        List<SubTask> subTasks = getSubTaskList();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(getNewFile().toFile(), StandardCharsets.UTF_8))) {
            String firstLine = "id,type,name,status,description,epic\n";
            if (tasks != null) {
                for (Task task : tasks) {
                    allTasks.add(toString(task));
                }
            }
            if (epics != null) {
                for (Epic epic : epics) {
                    allTasks.add(toString(epic));
                }
            }
            if (subTasks != null) {
                for (SubTask subTask : subTasks) {
                    allTasks.add(toString(subTask));
                }
            }
            bufferedWriter.write(firstLine);
            for (String allTask : allTasks) {
                bufferedWriter.write(allTask);
            }
            bufferedWriter.write("\n");
            bufferedWriter.write(historyToString(getObjectHistory()));

        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Ошибка при работе с файлом");
            } catch (ManagerSaveException ex) {
                ex.printStackTrace();
            }
        }
    }

    //метод сохраения задачи в строку
    public String toString(Task task) {
        return task.fromObjectToString();
    }


    //сохранение менеджера в историю
    static String historyToString(HistoryManager manager) {
        String actualHistory = "";
        List<Task> historys = manager.getHistory();
        for (Task history : historys) {
            if (actualHistory.isBlank()) {
                actualHistory = "" + history.getId();
            } else {
                actualHistory = actualHistory + "," + history.getId();
            }
        }
        return actualHistory;
    }

    //восстановления менеджера из истории
    static List<Integer> historyFromString(String value) {
        List<Integer> numberTasks = new ArrayList<>();
        String[] numbers = value.split(",");
        for (int i = 0; i < numbers.length; i++) {
            numberTasks.add(Integer.valueOf(numbers[i]));
        }
        return numberTasks;
    }

    //метод создания задачи из строки
    public Task fromString(String value) {
        String[] eachLine = value.split(",");
        Task task = null;
        if (eachLine[1].equals(String.valueOf(TypeTask.TASK))) {
            task = createTask(new Task(eachLine[2],
                    eachLine[4], Status.valueOf(eachLine[3])));
        } else if (eachLine[1].equals(String.valueOf(TypeTask.EPIC))) {
            task = createEpic(new Epic(eachLine[2], eachLine[4]));
        } else if (eachLine[1].equals(String.valueOf(TypeTask.SUBTASK))) {
            task = createSubTask(new SubTask(eachLine[2], eachLine[4],
                    Status.valueOf(eachLine[3]), Integer.parseInt(eachLine[5])));
        }
        return task;
    }

    //восстановление данные менеджера из файла при запуске программы
    public static FileBackedTasksManager loadFromFile(File file) {

        Path resultFile = Paths.get("src/files/recoveryTasks.txt");
        FileBackedTasksManager fileBacked = new FileBackedTasksManager(resultFile);

        Path path = file.toPath();
        String stringPath = path.getParent() + "/" + path.getFileName();

        Comparator<Integer> userComparator = new Comparator<>() {
            @Override
            public int compare(Integer number1, Integer number2) {
                return number1.compareTo(number2);
            }
        };
        Map<Integer, String> sortedTasks = new TreeMap<>(userComparator);

        try {
            String newFiles = Files.readString(Path.of(stringPath));
            String[] lines = newFiles.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String[] eachLine = lines[i].split(",");
                if (eachLine[0].isBlank()) {
                    break;
                } else {
                    sortedTasks.put(Integer.valueOf(eachLine[0]), lines[i]);
                }
            }
            for (String sorteredLine : sortedTasks.values()) {
                fileBacked.fromString(sorteredLine);
            }
            String historyInLine = lines[lines.length - 1];
            List<Integer> historyInList = historyFromString(historyInLine);

            for (int i = 0; i < historyInList.size(); i++) {
                fileBacked.findTypeTask(historyInList.get(i));
            }
        } catch (IOException e) {

        }
        System.out.println("Задачи считаны из файла");
//        System.out.println(fileBacked.getSubTaskList());
//        System.out.println(fileBacked.getEpicList());
//        System.out.println(fileBacked.getTaskList());
        return fileBacked;
    }

    public Task findTypeTask(int number) {
        Task task;
        if ((task = getTask(number)) != null) {
            return task;
        } else if ((task = getEpic(number)) != null) {
            return task;
        } else if ((task = getSubTask(number)) != null) {
            return task;
        }
        return null;
    }

    //Наследуемые методы от InMemoryTaskManager
    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask sub) {
        super.createSubTask(sub);
        save();
        return sub;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subtask = super.getSubTask(id);
        save();
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean result = super.deleteTaskById(id);
        save();
        return result;
    }

    @Override
    public boolean deleteEpicById(int id) {
        boolean result = super.deleteEpicById(id);
        save();
        return result;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        boolean result = super.deleteSubTaskById(id);
        save();
        return result;
    }

}