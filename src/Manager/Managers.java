package Manager;

import History.HistoryManager;
import History.InMemoryHistoryManager;

import java.nio.file.Paths;

public final class Managers {

    //Возврат объекта InMemoryHistoryManager
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    //Возврат объекта FileBackedTasksManager
    public static TaskManager getDefault() {
        return new FileBackedTasksManager(Paths.get("src/files/saveFileStatic.txt"));
    }
}
