package Manager;

import History.*;

public final class Managers<T extends TaskManager> {

    private static HistoryManager historyManager = new InMemoryHistoryManager();
    TaskManager newObject;

    public Managers(T newObject) {
        this.newObject = newObject;
    }

    //Возврат объекта InMemoryHistoryManager
    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }

    public TaskManager getDefault() {
        return newObject;
    }

}
