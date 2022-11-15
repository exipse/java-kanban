package History;

import Model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static List<Task> logAllTasks = new ArrayList<>();

    //метод по получению актуальных данных в таблице истории
    @Override
    public List<Task> getHistory() {
        return logAllTasks;
    }

    @Override
    //метод добавления тасок/эпиков/сабтасок в историю
    public void add(Task task) {
        if (logAllTasks.size() == 10) {
            logAllTasks.remove(0);
        }
        logAllTasks.add(task);
    }
}
