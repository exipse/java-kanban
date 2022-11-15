package History;

import Model.Task;

import java.util.List;

public interface HistoryManager {

    // установка задаче состояния - "Просмотрено"
    void add(Task task);

    //возврат списка значений
    List<Task> getHistory();

}
