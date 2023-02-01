package HTTP;

import Manager.Managers;
import Manager.TaskManager;
import Model.Epic;
import Model.SubTask;
import Model.Task;
import adapter.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HttpTaskServer {

    public static HttpServer httpServer;
    private static final int PORT = 8085;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static String response = "";
    public static int rCode = 0;

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", new TaskHandler());
    }

    public void startServer() {
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
    }

    static class TaskHandler implements HttpHandler {
        TaskManager manager = Managers.getDefault();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Стартовал новый запрос на сервер");
            String[] url = exchange.getRequestURI().getPath().split("/");
            if (url.length > 2 && url[2].equals("epic")) {
                handleEpics(exchange);
            } else if (url.length > 2 && url[2].equals("subtask")) {
                handleSubtasks(exchange);
            } else if ((url[1].equals("tasks") && url.length == 2) ||
                    (url[1].equals("tasks") && url[2].equals("task")) ||
                    (url[1].equals("tasks") && url[2].equals("history"))) {
                handleTasks(exchange);
            } else {
                rCode = 500;
                response = "Введен некорректный endpoint";
                exchange.sendResponseHeaders(rCode, 0);
                try (
                        OutputStream stream = exchange.getResponseBody()) {
                    stream.write(response.getBytes());
                }
            }
        }

        void handleTasks(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] separeteUri = exchange.getRequestURI().getPath().split("/");
            String query = exchange.getRequestURI().getQuery();
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            builder.registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter());
            Gson gson = builder.create();

            switch (method) {
                case "GET": {
                    StringBuffer buffer = new StringBuffer();
                    if ((separeteUri.length == 3) && (separeteUri[2].equals("history"))) {
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
                    } else if ((separeteUri.length == 3) && (separeteUri[2].equals("task"))
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
                    } else if ((separeteUri.length == 2) && (query == null)) {
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
                    if ((separeteUri.length == 3) && (separeteUri[2].equals("task"))
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

        void handleEpics(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] separeteUri = exchange.getRequestURI().getPath().split("/");
            String query = exchange.getRequestURI().getQuery();
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            builder.registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter());
            Gson gson = builder.create();

            switch (method) {
                case "GET": {
                    if ((separeteUri.length == 3) && (separeteUri[2].equals("epic"))
                            && (query == null)) {
                        ArrayList<Epic> epicList = manager.getEpicList();
                        try {
                            StringBuffer buffer = new StringBuffer();
                            for (Epic epic : epicList) {
                                buffer.append(gson.toJson(epic));
                            }
                            rCode = 200;
                            response = buffer.toString();
                        } catch (NullPointerException e) {
                            rCode = 404;
                            response = "Эпиков не найдено";
                        }
                    } else {
                        String[] splitQuery = query.split("\\&");
                        String[] everyQuery = splitQuery[0].split("=");
                        int idEpic = Integer.parseInt(everyQuery[1]);
                        Epic epic = manager.getEpic(idEpic);
                        if (epic != null) {
                            String json = gson.toJson(epic);
                            rCode = 200;
                            response = json;
                        } else {
                            rCode = 404;
                            response = "Эпик с id = " + idEpic + " не найден";
                        }
                    }
                    break;
                }
                case "POST": {
                    try {
                        Epic epic = gson.fromJson(body, Epic.class);
                        int id = epic.getId();

                        if ((manager.getEpic(id) != null)) {
                            manager.updateEpic(epic);
                            response = "Эпик с id=" + id + " обновлен";
                            rCode = 200;
                            break;
                        } else if (manager.getEpic(id) == null && (epic.getId() != 0)) {
                            response = "Эпик с id=" + id + " не существует";
                            rCode = 404;
                            break;
                        } else if (manager.getEpic(id) == null && (id == 0)
                                && ((epic.getStartTime() != null ||
                                epic.getFinishTime() != null))) {
                            response = "Нельзя создать эпик. Переданы некорректные входные данные";
                            rCode = 400;
                            break;
                        } else {
                            Epic newEpic = manager.createEpic(epic);
                            response = "Эпик с id = " + newEpic.getId() + " создан";
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
                    if ((separeteUri.length == 3) && (separeteUri[2].equals("epic"))
                            && (query == null)) {
                        manager.cleanAllEpic();
                        response = "Все эпики и связанные с ними сабтаски удалены";
                        rCode = 200;
                    } else {
                        String[] splitQuery = query.split("\\&");
                        String[] everyQuery = splitQuery[0].split("=");
                        int idEpic = Integer.parseInt(everyQuery[1]);
                        if (manager.getEpic(idEpic) != null) {
                            manager.deleteEpicById(idEpic);
                            rCode = 200;
                            response = "Эпик с id = " + idEpic + " , а так же все связанные сабтаски удалены";
                        } else {
                            rCode = 404;
                            response = "Удаление невозможно. Эпика с id = " + idEpic + " не существует";
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

        void handleSubtasks(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] separeteUri = exchange.getRequestURI().getPath().split("/");
            String query = exchange.getRequestURI().getQuery();
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            builder.registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter());
            Gson gson = builder.create();

            switch (method) {
                case "GET": {
                    StringBuffer buffer = new StringBuffer();
                    if ((separeteUri.length == 4) && (separeteUri[2].equals("subtask"))
                            && (separeteUri[3].equals("epic"))) {
                        String[] idQuery = query.split("\\&")[0].split("=");
                        int epic = Integer.parseInt(idQuery[1]);
                        try {
                            ArrayList<SubTask> subListbyEpic = manager.getAllSubTaskByEpic(epic);
                            for (SubTask subTask : subListbyEpic) {
                                buffer.append(gson.toJson(subTask));
                            }
                            rCode = 200;
                            response = buffer.toString();
                        } catch (NullPointerException e) {
                            rCode = 404;
                            response = "Сабтасок у эпика не найдено";
                        }
                    } else if ((separeteUri.length == 3) && (separeteUri[2].equals("subtask"))
                            && (query == null)) {
                        ArrayList<SubTask> subTaskList = manager.getSubTaskList();
                        try {
                            for (SubTask subTask : subTaskList) {
                                buffer.append(gson.toJson(subTask));
                            }
                            rCode = 200;
                            response = buffer.toString();
                        } catch (NullPointerException e) {
                            rCode = 404;
                            response = "Сабтасок не найдено";
                        }
                    } else {
                        String[] splitQuery = query.split("\\&");
                        String[] everyQuery = splitQuery[0].split("=");
                        int idSubTask = Integer.parseInt(everyQuery[1]);
                        SubTask subTask = manager.getSubTask(idSubTask);
                        if (subTask != null) {
                            String json = gson.toJson(subTask);
                            rCode = 200;
                            response = json;
                        } else {
                            rCode = 404;
                            response = "СабТаска с id = " + idSubTask + " не найдена";
                        }
                    }
                    break;
                }
                case "POST": {
                    try {
                        SubTask subTask = gson.fromJson(body, SubTask.class);
                        int id = subTask.getId();
                        if ((manager.getSubTask(id) != null)) {
                            manager.updateSubTask(subTask);
                            response = "Сабтаска с id=" + id + " обновлена";
                            rCode = 200;
                            break;
                        } else if (manager.getSubTask(id) == null && (subTask.getId() != 0)) {
                            response = "Сабтаски с id=" + id + " не существует";
                            rCode = 404;
                            break;
                        } else if (manager.getSubTask(id) == null && (id == 0)
                                && ((subTask.getStatus() == null) ||
                                (subTask.getStartTime() != null && subTask.getFinishTime() != null) ||
                                (subTask.getStartTime() == null && subTask.getFinishTime() != null)
                                || subTask.getEpicId() == 0)) {
                            response = "Нельзя создать сабтаску. Переданы некорректные входные данные";
                            rCode = 400;
                            break;
                        } else {
                            SubTask newSubTask = manager.createSubTask(subTask);
                            if (newSubTask != null) {
                                response = "Сабтаска с id = " + newSubTask.getId() + " создана";
                                rCode = 201;
                                break;
                            } else {
                                response = "Произошла ошибка при сохранении сабтаски в эпик. Проверьте наличие эпика";
                                rCode = 500;
                                break;
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Ошибка чтения файла. Проверьте входные данные";
                        rCode = 500;
                        break;
                    }
                }
                case "DELETE": {
                    if ((separeteUri.length == 3) && (separeteUri[2].equals("subtask"))
                            && (query == null)) {
                        manager.cleanAllSubTask();
                        response = "Все сабтаски удалены";
                        rCode = 200;

                    } else {
                        String[] splitQuery = query.split("\\&");
                        String[] everyQuery = splitQuery[0].split("=");
                        int idSubTask = Integer.parseInt(everyQuery[1]);
                        if (manager.getSubTask(idSubTask) != null) {
                            manager.deleteSubTaskById(idSubTask);
                            rCode = 200;
                            response = "Сабтаска с id = " + idSubTask + " удалена";
                        } else {
                            rCode = 404;
                            response = "Удаление невозможно. СабТаски с id = " + idSubTask + " не существует";
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

}
