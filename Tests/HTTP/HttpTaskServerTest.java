package HTTP;

import Model.Task;
import adapter.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().
            registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter()).create();
    private static final String URL = "http://localhost:8085/tasks/";

    @BeforeEach
    public void startServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskServer = new HttpTaskServer();
        taskServer.startServer();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        taskServer.stopServer();
        Task.setCount(0);
    }

    @Test
    void createAllTypeTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String epic = "{ \"name\":\" Эпик \",\"description\":\"ОПИСАНИЕ ЭПИКА\",\"status\":\"NEW\",\"type\":\"ззз\"}";
        String subTask = "{ \"name\":\" -сабтаска эпика \",\"description\":\"Описание 1...\",\"status\":\"NEW\"," +
                "\"epicId\":1, \"startTime\":\"2024-07-26T10:40:19.754705\",\"duration\": 10}";
        String task = "{ \"name\":\" -таск-1\",\"description\":\"Описание 1...\"," +
                "\"status\":\"NEW\",\"startTime\":\"2024-03-26T10:40:19.754705\",\"duration\": 10}";

        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestSubTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(subTask))
                .build();
        HttpResponse<String> responseSubTask = client.send(requestSubTask, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/"))
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic.statusCode(), "Эпик не удалось создать");
        assertEquals(201, responseSubTask.statusCode(), "Сабтаску не удалось создать");
        assertEquals(201, responseTask.statusCode(), "Таску не удалось создать");
    }


    @Test
    void updateAllTypeTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String epic = "{ \"name\":\" Эпик \",\"description\":\"ОПИСАНИЕ ЭПИКА\",\"status\":\"NEW\",\"type\":\"ззз\"}";
        String subTask = "{ \"name\":\" -сабтаска эпика \",\"description\":\"Описание 1...\",\"status\":\"NEW\"," +
                "\"epicId\":1, \"startTime\":\"2024-07-26T10:40:19.754705\",\"duration\": 10}";
        String task = "{ \"name\":\" -таск-1\",\"description\":\"Описание 1...\"," +
                "\"status\":\"NEW\",\"startTime\":\"2024-03-26T10:40:19.754705\",\"duration\": 10}";

        String updEpic = "{ \"id\":1,\"name\":\" ЭпикНовый \",\"description\":\"ОПИСАНИЕ ЭПИКА\"," +
                                                                            "\"status\":\"NEW\",\"type\":\"ззз\"}";
        String updSub = "{ \"id\":2,\"name\":\"Upd sub \",\"description\":\"upd\",\"status\":\"NEW\",\"epicId\":1," +
                                                     " \"startTime\":\"2024-07-26T10:40:19.754705\",\"duration\": 10}";
        String updTask = "{ \"id\":\"3\", \"name\":\" UOD-таск-1\",\"description\":\"Описание 1...\"," +
                "\"status\":\"NEW\",\"startTime\":\"2024-03-26T10:40:19.754705\",\"duration\": 10}";


        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestSubTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(subTask))
                .build();
        HttpResponse<String> responseSubTask = client.send(requestSubTask, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/"))
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic.statusCode(), "Эпик не удалось создать");
        assertEquals(201, responseSubTask.statusCode(), "Сабтаску не удалось создать");
        assertEquals(201, responseTask.statusCode(), "Таску не удалось создать");

        HttpRequest reqUpdEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(updEpic))
                .build();
        HttpResponse<String> respUpdEpic = client.send(reqUpdEpic, HttpResponse.BodyHandlers.ofString());


        HttpRequest reqUpdSub = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(updSub))
                .build();
        HttpResponse<String> respUpdSub = client.send(reqUpdSub, HttpResponse.BodyHandlers.ofString());

        HttpRequest reqUpdTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(updTask))
                .build();
        HttpResponse<String> respUpdTask = client.send(reqUpdTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, respUpdEpic.statusCode(), "Эпик не удалось обновить");
        assertEquals(200, respUpdSub.statusCode(), "Сабтаску не удалось обновить");
        assertEquals(200, respUpdTask.statusCode(), "Таску не удалось обновить");
    }


    @Test
    void getAllTypeTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String epic = "{ \"name\":\" Эпик \",\"description\":\"ОПИСАНИЕ ЭПИКА\",\"status\":\"NEW\",\"type\":\"ззз\"}";
        String subTask = "{ \"name\":\" -сабтаска эпика \",\"description\":\"Описание 1...\",\"status\":\"NEW\"," +
                "\"epicId\":1, \"startTime\":\"2024-07-26T10:40:19.754705\",\"duration\": 10}";
        String task = "{ \"name\":\" -таск-1\",\"description\":\"Описание 1...\"," +
                "\"status\":\"NEW\",\"startTime\":\"2024-03-26T10:40:19.754705\",\"duration\": 10}";


        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestSubTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(subTask))
                .build();
        HttpResponse<String> responseSubTask = client.send(requestSubTask, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/"))
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic.statusCode(), "Эпик не удалось создать");
        assertEquals(201, responseSubTask.statusCode(), "Сабтаску не удалось создать");
        assertEquals(201, responseTask.statusCode(), "Таску не удалось создать");

        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/?id=1"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> respGetEpic = client.send(getEpic, HttpResponse.BodyHandlers.ofString());


        HttpRequest getSub = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/?id=2"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> respGetSub = client.send(getSub, HttpResponse.BodyHandlers.ofString());

        HttpRequest getTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/?id=3"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> respGetTask = client.send(getTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, respGetEpic.statusCode(), "Эпик по id не найден");
        assertEquals(200, respGetSub.statusCode(), "Сабтаска по id не найдена");
        assertEquals(200, respGetTask.statusCode(), "Таска по id не найдена");
    }


    @Test
    void deleteAllTypeTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String epic = "{ \"name\":\" Эпик \",\"description\":\"ОПИСАНИЕ ЭПИКА\",\"status\":\"NEW\",\"type\":\"ззз\"}";
        String subTask = "{ \"name\":\" -сабтаска эпика \",\"description\":\"Описание 1...\",\"status\":\"NEW\"," +
                "\"epicId\":1, \"startTime\":\"2024-07-26T10:40:19.754705\",\"duration\": 10}";
        String task = "{ \"name\":\" -таск-1\",\"description\":\"Описание 1...\"," +
                "\"status\":\"NEW\",\"startTime\":\"2024-03-26T10:40:19.754705\",\"duration\": 10}";


        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestSubTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(subTask))
                .build();
        HttpResponse<String> responseSubTask = client.send(requestSubTask, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/"))
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic.statusCode(), "Эпик не удалось создать");
        assertEquals(201, responseSubTask.statusCode(), "Сабтаску не удалось создать");
        assertEquals(201, responseTask.statusCode(), "Таску не удалось создать");

        HttpRequest getSub = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/?id=2"))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> respDelSub = client.send(getSub, HttpResponse.BodyHandlers.ofString());

        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/?id=1"))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> respDelEpic = client.send(getEpic, HttpResponse.BodyHandlers.ofString());


        HttpRequest getTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/?id=3"))
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();
        HttpResponse<String> respDelTask = client.send(getTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(respDelSub.body(), "Сабтаска с id = 2 удалена", "Сабтаска по id не удалена");
        assertEquals(respDelEpic.body(), "Эпик с id = 1 , а так же все связанные сабтаски удалены",
                "Эпик по id не удален");
        assertEquals(respDelTask.body(), "Таска с id = 3 удалена", "Таска по id не удалена");
    }

    @Test
    void getSubTaskByEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String epic = "{ \"name\":\" Эпик \",\"description\":\"ОПИСАНИЕ ЭПИКА\",\"status\":\"NEW\",\"type\":\"ззз\"}";
        String subTask = "{ \"name\":\" -сабтаска эпика \",\"description\":\"Описание 1...\",\"status\":\"NEW\"," +
                "\"epicId\":1, \"startTime\":\"2024-07-26T10:40:19.754705\",\"duration\": 10}";


        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestSubTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(subTask))
                .build();
        HttpResponse<String> responseSubTask = client.send(requestSubTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic.statusCode(), "Эпик не удалось создать");
        assertEquals(201, responseSubTask.statusCode(), "Сабтаску не удалось создать");


        HttpRequest getSubByEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/epic?id=1"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> respSubtasks = client.send(getSubByEpic, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, respSubtasks.statusCode(), "Не удалось получить сабтаски по эпику");

    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String task = "{ \"name\":\" -таск-1\",\"description\":\"Описание 1...\"," +
                "\"status\":\"NEW\",\"startTime\":\"2024-03-26T10:40:19.754705\",\"duration\": 10}";


        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/"))
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseTask.statusCode(), "Таску не удалось создать");

        HttpRequest getSub = HttpRequest.newBuilder()
                .uri(URI.create(URL + "history"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> respGetSub = client.send(getSub, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, respGetSub.statusCode(), "Эпик по id не найден");

    }


    @Test
    void getPriorTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String epic = "{ \"name\":\" Эпик \",\"description\":\"ОПИСАНИЕ ЭПИКА\",\"status\":\"NEW\",\"type\":\"ззз\"}";
        String subTask = "{ \"name\":\" -сабтаска эпика \",\"description\":\"Описание 1...\",\"status\":\"NEW\"," +
                "\"epicId\":1, \"startTime\":\"2024-07-26T10:40:19.754705\",\"duration\": 10}";
        String task = "{ \"name\":\" -таск-1\",\"description\":\"Описание 1...\"," +
                "\"status\":\"NEW\",\"startTime\":\"2024-03-26T10:40:19.754705\",\"duration\": 10}";


        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL + "epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestSubTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "subtask/"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(subTask))
                .build();
        HttpResponse<String> responseSubTask = client.send(requestSubTask, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(URI.create(URL + "task/"))
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic.statusCode(), "Эпик не удалось создать");
        assertEquals(201, responseSubTask.statusCode(), "Сабтаску не удалось создать");
        assertEquals(201, responseTask.statusCode(), "Таску не удалось создать");

        HttpRequest getPriorTasks = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> respGetEpic = client.send(getPriorTasks, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, respGetEpic.statusCode(), "Произошла ошибка");
    }

}