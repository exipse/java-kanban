package History;

import History.HistoryManager;
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

    //метод поддержания актуальности таблицы истории
    public  void actualTableHistory() {
        if (logAllTasks.size() == 10) {
            logAllTasks.remove(0);
        }
    }

//метод который будет помечать задачи как просмотренные
    @Override
    public void add(Task task) {
        //что то будет
    }

}
