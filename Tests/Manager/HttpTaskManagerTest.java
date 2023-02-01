package Manager;

import HTTP.KVServer;
import HTTP.KVTaskClient;
import Manager.HttpTaskManager;
import Manager.Managers;
import Model.Status;
import Model.Task;
import adapter.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    @BeforeEach
    public void beforeEach() {
        setManager((HttpTaskManager) Managers.getDefault());
    }

    @AfterEach
    public void after() {
        Task.setCount(0);
    }

    @Test
    void loadToServer() throws IOException, InterruptedException {
        new KVServer().start();
        KVTaskClient client = new KVTaskClient("http://localhost:8078");

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter());
        Gson gson = builder.create();


        Task task = new Task("Таска - 1", "Описание 1...", Status.NEW,
                LocalDateTime.now(), 10);
        String toJson = gson.toJson(task);
        client.put("TASK", toJson);
        String fromJson = client.load("TASK");
        Task recoverTask = gson.fromJson(fromJson, Task.class);
        assertTrue(recoverTask.getName().equals(task.getName()), "Сохраненный объект не восстановился");
    }


}