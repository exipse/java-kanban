package HTTP.Handlers;

import HTTP.HttpTaskServer;
import Manager.TaskManager;
import Model.SubTask;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static HTTP.HttpTaskServer.rCode;
import static HTTP.HttpTaskServer.response;

public class SubtasksHandle implements HttpHandler {

    public SubtasksHandle(TaskManager manager){
        this.manager = manager;
    }

    TaskManager manager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        String method = exchange.getRequestMethod();
        String[] separateUri = exchange.getRequestURI().getPath().split("/");
        String query = exchange.getRequestURI().getQuery();
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        Gson gson = HttpTaskServer.getGson();

        switch (method) {
            case "GET": {
                StringBuffer buffer = new StringBuffer();
                if ((separateUri.length == 4) && (separateUri[2].equals("subtask"))
                        && (separateUri[3].equals("epic"))) {
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
                } else if ((separateUri.length == 3) && (separateUri[2].equals("subtask"))
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
                if ((separateUri.length == 3) && (separateUri[2].equals("subtask"))
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

