import HTTP.HttpTaskServer;
import HTTP.KVServer;
import Manager.Managers;
import Manager.TaskManager;
import Model.Status;
import Model.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager manager = Managers.getDefault();
        manager.createTask(new Task("Таска - 1",
                "Описание 1...", Status.NEW, LocalDateTime.now(), 10));

        System.out.println("\n" + manager.getTaskList());
        kvServer.stop();
    }
}
