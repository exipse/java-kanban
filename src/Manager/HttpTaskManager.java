package Manager;

import HTTP.KVTaskClient;
import Model.Epic;
import Model.SubTask;
import Model.Task;
import adapter.LocalDateAdapter;
import com.google.gson.*;
import exception.ManagerSaveException;

import java.time.LocalDateTime;
import java.util.HashMap;


public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    Gson gson;

    public HttpTaskManager(String uriKvserver) {
        client = new KVTaskClient(uriKvserver);
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter());
        gson = builder.create();
        loadHistoryFromKVVServer();
    }


    private void loadHistoryFromKVVServer() {
        loadTaskFromServer();
        loadEpicFromServer();
        loadSubtasksFromServer();
        loadHistory();
        jsonPrior();
    }

    private void loadTaskFromServer() {
        String tasks = client.load("Tasks");
        if (!(tasks.isBlank())) {
            HashMap<Integer, Task> recoverTasks = new HashMap<>();
            JsonElement jsonElement = JsonParser.parseString(tasks);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                Task task = gson.fromJson(element, Task.class);
                recoverTasks.put(task.getId(), task);
            }
            this.tasks.putAll(recoverTasks);
        }
    }

    private void loadEpicFromServer() {
        String epics = client.load("Epics");
        if (!(epics.isBlank())) {
            HashMap<Integer, Epic> recoverEpic = new HashMap<>();
            JsonElement jsonElement = JsonParser.parseString(epics);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                Epic epic = gson.fromJson(element, Epic.class);
                recoverEpic.put(epic.getId(), epic);
            }
            this.epics.putAll(recoverEpic);
        }
    }

    private void loadSubtasksFromServer() {
        String subtasks = client.load("Subtasks");
        if (!(subtasks.isBlank())) {
            HashMap<Integer, SubTask> recoverSub = new HashMap<>();
            JsonElement jsonElement = JsonParser.parseString(subtasks);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                SubTask subTask = gson.fromJson(element, SubTask.class);
                recoverSub.put(subTask.getId(), subTask);
            }
            this.subtasks.putAll(recoverSub);
        }
    }

    private void loadHistory() {
        String recoveryHistory = client.load("History");
        if (!(recoveryHistory.isBlank())) {
            JsonElement jsonElement = JsonParser.parseString(recoveryHistory);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                Task tasks = gson.fromJson(element, Task.class);
                this.getObjectHistory().add(tasks);
            }
        }
    }

    private void jsonPrior() {
        String recoveryPrior = client.load("Prior");
        if (!(recoveryPrior.isBlank())) {
            JsonElement jsonElement = JsonParser.parseString(recoveryPrior);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                Task task = gson.fromJson(element, Task.class);
                this.prioritizedTasks.add(task);
            }
        }
    }

    @Override
    protected void save() throws ManagerSaveException {
        String jsonTasks = gson.toJson(getTaskList());
        String jsonEpics = gson.toJson(getEpicList());
        String jsonSubTasks = gson.toJson(getSubTaskList());
        String jsonHistory = gson.toJson(getHistory());
        String jsonPrior = gson.toJson(getPrioritizedTasks());

        if (!(jsonTasks.equals("null"))) {
            client.put("Tasks", jsonTasks);
        }
        if (!(jsonEpics.equals("null"))) {
            client.put("Epics", jsonEpics);
        }
        if (!(jsonSubTasks.equals("null"))) {
            client.put("Subtasks", jsonSubTasks);
        }
        if (!(jsonHistory.isEmpty())) {
            client.put("History", jsonHistory);
        }
        if (!(jsonPrior.isEmpty())) {
            client.put("Prior", jsonPrior);
        }
    }
}
