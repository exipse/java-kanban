package History;

import Model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static List<Task> logAllTasks = new ArrayList<>();
    private static final int SIZE_TABLE_HISTORY = 10;

    //метод по получению актуальных данных в таблице истории
    @Override
    public List<Task> getHistory() {
        return logAllTasks;
    }

    @Override
    //метод добавления тасок/эпиков/сабтасок в историю
    public void add(Task task) {
        if (logAllTasks.size() == SIZE_TABLE_HISTORY) {
            logAllTasks.remove(0);
        }
        logAllTasks.add(task);
    }
}
