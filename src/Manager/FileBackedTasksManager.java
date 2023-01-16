package Manager;

import History.HistoryManager;
import Model.*;
import exception.ManagerLoadException;
import exception.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private Path newFile;

    protected FileBackedTasksManager(Path newFile) {
        this.newFile = newFile;
    }

    //метод автосохранения
    private void save() throws ManagerSaveException {
        List<String> allTasks = new ArrayList<>();
        List<Task> tasks = getTaskList();
        List<Epic> epics = getEpicList();
        List<SubTask> subTasks = getSubTaskList();

        try (BufferedWriter buff = new BufferedWriter(new FileWriter(newFile.toFile(), StandardCharsets.UTF_8))) {
            String firstLine = "id,type,name,status,description,startTime,duration,finishTime,epic\n";
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
            buff.write(firstLine);
            for (String allTask : allTasks) {
                buff.write(allTask);
            }
            buff.write("\n");
            buff.write(historyToString(getObjectHistory()));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл");
        }
    }

    //метод сохраения задачи в строку
    private String toString(Task task) {
        return task.fromObjectToString();
    }


    //сохранение менеджера в историю
    private static String historyToString(HistoryManager manager) {
        StringBuilder actualhistory = new StringBuilder();
        List<Task> historys = manager.getHistory();
        for (Task history : historys) {
            if (actualhistory.length() == 0) {
                actualhistory.append(history.getId());
            } else {
                actualhistory.append("," + history.getId());
            }
        }
        String actualHistory = actualhistory.toString();
        return actualHistory;
    }

    //восстановление менеджера из истории
    private static List<Integer> historyFromString(String value) {
        List<Integer> numberTasks = new ArrayList<>();
        String[] numbers = value.split(",");
        for (int i = 0; i < numbers.length; i++) {
            numberTasks.add(Integer.valueOf(numbers[i]));
        }
        return numberTasks;
    }

    //метод создания задачи из строки
    private Task fromString(String value) {
        String[] eachLine = value.split(",");
        Task task = null;
        if (eachLine[1].equals(String.valueOf(TypeTask.TASK))) {
            if (eachLine[5].equals("null")) {
                task = createTask(new Task(eachLine[2],
                        eachLine[4], Status.valueOf(eachLine[3])));
            } else {
                task = createTask(new Task(eachLine[2],
                        eachLine[4], Status.valueOf(eachLine[3]), LocalDateTime.parse(eachLine[5]),
                        Long.valueOf(eachLine[6])));
            }
        } else if (eachLine[1].equals(String.valueOf(TypeTask.EPIC))) {
            task = createEpic(new Epic(eachLine[2], eachLine[4]));
        } else if (eachLine[1].equals(String.valueOf(TypeTask.SUBTASK))) {
            if ((eachLine[5].equals("null"))) {
                task = createSubTask(new SubTask(eachLine[2], eachLine[4],
                        Status.valueOf(eachLine[3]), Integer.parseInt(eachLine[8])));
            } else {
                task = createSubTask(new SubTask(eachLine[2], eachLine[4],
                        Status.valueOf(eachLine[3]), Integer.parseInt(eachLine[8]),
                        LocalDateTime.parse(eachLine[5]), Long.valueOf(eachLine[6])));
            }
        }
        return task;
    }

    //восстановление данные менеджера из файла при запуске программы
    public static FileBackedTasksManager loadFromFile(File file) throws ManagerLoadException {

        Path resultFile = Paths.get("src/files/recoveryTasks.txt");
        FileBackedTasksManager fileBacked = new FileBackedTasksManager(resultFile);

        Path path = file.toPath();
        String stringPath = path.getParent() + "/" + path.getFileName();
        Map<Integer, String> sortedTasks = new TreeMap<>(Comparator.naturalOrder());
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
            if (lines[lines.length - 2].isBlank()) {
                String historyInLine = lines[lines.length - 1];
                List<Integer> historyInList = historyFromString(historyInLine);

                for (int i = 0; i < historyInList.size(); i++) {
                    fileBacked.findTypeTask(historyInList.get(i));
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка чтения из файла");
        }
        System.out.println("Задачи считаны из файла");
        return fileBacked;
    }

    private Task findTypeTask(int number) {
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
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask sub) {
        super.createSubTask(sub);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return sub;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subtask = super.getSubTask(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean result = super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return result;
    }

    @Override
    public boolean deleteEpicById(int id) {
        boolean result = super.deleteEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return result;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        boolean result = super.deleteSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
        return result;
    }

}