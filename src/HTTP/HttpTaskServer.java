package HTTP;

import HTTP.Handlers.EpicsHandle;
import HTTP.Handlers.SubtasksHandle;
import HTTP.Handlers.TasksHandle;
import Manager.Managers;
import Manager.TaskManager;
import adapter.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public static HttpServer httpServer;
    private static final int PORT = 8085;
    public static String response = "Введен некорректный запрос";
    public static int rCode = 500;

    TaskManager manager = Managers.getDefault();

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", (HttpHandler) new TasksHandle(manager));
        httpServer.createContext("/tasks/epic", (HttpHandler) new EpicsHandle(manager));
        httpServer.createContext("/tasks/subtask", (HttpHandler) new SubtasksHandle(manager));
    }

    public void startServer() {
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter());
        Gson gson = builder.create();
        return gson;
    }
}
