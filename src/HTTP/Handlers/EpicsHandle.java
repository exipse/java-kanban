package HTTP.Handlers;

import HTTP.HttpTaskServer;
import Manager.TaskManager;
import Model.Epic;
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

public class EpicsHandle implements HttpHandler {

    public EpicsHandle(TaskManager manager) {
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
                if ((separateUri.length == 3) && (separateUri[2].equals("epic"))
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
                if ((separateUri.length == 3) && (separateUri[2].equals("epic"))
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

}


