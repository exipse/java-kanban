package History;

import Model.Task;

import java.util.List;

public interface HistoryManager {

    // установка задаче состояния - "Просмотрено"
    abstract public void add(Task task);

    //возврат списка значений
    abstract public List<Task> getHistory();

}
