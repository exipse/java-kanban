package HTTP;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String adress;
    private final String token;

    public KVTaskClient(String adress) {
        this.adress = adress;
        String newtoken = "";
        try {
            URI uri = URI.create(adress + "/register");

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            newtoken = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. " +
                    "Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        }
        token = newtoken;
    }

    public void put(String key, String json) {
        try {
            URI uri = URI.create(adress + "/save/" + key + "?API_TOKEN=" + token);

            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(body)
                    .uri(uri)
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            System.out.println("Код операции: " + response.statusCode());
            System.out.println("Ответ: KVServer сохранил данные в key = " + key);
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. " +
                    "Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        }
    }

    public String load(String key) {
        String answer = "";
        try {
            URI uri = URI.create(adress + "/load/" + key + "?API_TOKEN=" + token);

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            System.out.println("Код операции: " + response.statusCode());
            if (response.statusCode() == 200) {
                System.out.println("Полученный ответ: " + response.body());
            }
            answer = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. " +
                    "Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        }
        return answer;
    }
}