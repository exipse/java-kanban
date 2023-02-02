package HTTP.Handlers;

import HTTP.HttpTaskServer;
import Manager.TaskManager;
import Model.Task;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static HTTP.HttpTaskServer.rCode;
import static HTTP.HttpTaskServer.response;

public class TasksHandle implements HttpHandler {

    public TasksHandle(TaskManager manager) {
        this.manager = manager;
    }

    TaskManager manager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] separateUri = exchange.getRequestURI().getPath().split("/");
        String query = exchange.getRequestURI().getQuery();
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        Gson gson = HttpTaskServer.getGson();

        switch (method) {
            case "GET": {
                StringBuffer buffer = new StringBuffer();
                if ((separateUri.length == 3) && (separateUri[2].equals("history"))) {
                    List<Task> historys = manager.getHistory();
                    for (Task history : historys) {
                        buffer.append(gson.toJson(history));
                    }
                    rCode = 200;
                    if (buffer.toString().isBlank()) {
                        response = "Истории не найдено";
                    } else {
                        response = buffer.toString();
                    }
                } else if ((separateUri.length == 3) && (separateUri[2].equals("task"))
                        && (query == null)) {
                    ArrayList<Task> taskList = manager.getTaskList();
                    try {
                        for (Task task : taskList) {
                            buffer.append(gson.toJson(task));
                        }
                        rCode = 200;
                        response = buffer.toString();
                    } catch (NullPointerException e) {
                        rCode = 404;
                        response = "Тасок не найдено";
                    }
                } else if ((separateUri.length == 2) && (query == null)) {
                    Set<Task> priors = manager.getPrioritizedTasks();
                    for (Task prior : priors) {
                        buffer.append(gson.toJson(prior));
                    }
                    rCode = 200;
                    if (buffer.toString().isBlank()) {
                        response = "Список приоритетных задач пуст";
                    } else {
                        response = buffer.toString();
                    }
                } else {
                    String[] splitQuery = query.split("\\&");
                    String[] everyQuery = splitQuery[0].split("=");
                    int idTask = Integer.parseInt(everyQuery[1]);
                    Task task = manager.getTask(idTask);
                    if (task != null) {
                        String json = gson.toJson(task);
                        rCode = 200;
                        response = json;
                    } else {
                        rCode = 404;
                        response = "Таска с id = " + idTask + " не найдена";
                    }
                }
                break;
            }
            case "POST": {
                try {
                    Task task = gson.fromJson(body, Task.class);
                    int id = task.getId();
                    if ((manager.getTask(id) != null)) {
                        manager.updateTask(task);
                        response = "Таска с id=" + id + " обновлена";
                        rCode = 200;
                        break;
                    } else if (manager.getTask(id) == null && (id != 0)) {
                        response = "Таски с id=" + id + " не существует";
                        rCode = 404;
                        break;
                    } else if (manager.getTask(id) == null && (id == 0)
                            && (task.getStatus() == null) ||
                            (task.getStartTime() != null && task.getFinishTime() != null) ||
                            (task.getStartTime() == null && task.getFinishTime() != null)) {
                        response = "Нельзя создать таску. Переданы некорректные входные данные";
                        rCode = 400;
                        break;

                    } else {
                        Task newTask = manager.createTask(task);
                        response = "Таска с id = " + newTask.getId() + " создана";
                        rCode = 201;
                        break;
                    }
                } catch (JsonSyntaxException e) {
                    response = "Ошибка чтения файла. Проверьте входные данные";
                    rCode = 500;
                    break;
                }
            }
            case "DELETE": {
                if ((separateUri.length == 3) && (separateUri[2].equals("task"))
                        && (query == null)) {
                    manager.cleanAllTask();
                    response = "Все таски удалены";
                    rCode = 200;
                } else {
                    String[] splitQuery = query.split("\\&");
                    String[] everyQuery = splitQuery[0].split("=");
                    int idTask = Integer.parseInt(everyQuery[1]);
                    if (manager.getTask(idTask) != null) {
                        manager.deleteTaskById(idTask);
                        rCode = 200;
                        response = "Таска с id = " + idTask + " удалена";
                    } else {
                        rCode = 404;
                        response = "Удаление невозможно. Таски с id = " + idTask + " не существует";
                    }
                }
                break;
            }
            default:
                rCode = 501;
                response = "Метод не реализован";
        }
        exchange.sendResponseHeaders(rCode, 0);
        try (
                OutputStream stream = exchange.getResponseBody()) {
            stream.write(response.getBytes());
        }

    }
}
