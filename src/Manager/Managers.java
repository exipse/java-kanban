package Manager;

import History.HistoryManager;
import History.InMemoryHistoryManager;

import java.nio.file.Paths;

public final class Managers {

    //Возврат объекта InMemoryHistoryManager
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    //Возрат объекта HttpTaskManager
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8008");
    }
}
