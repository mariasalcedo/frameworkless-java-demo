package dev.masagu.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public sealed class RestClient permits MeteoNorwayRestClient, SevenTimerRestClient, OpenMeteoRestClient, ForexRestClient {

    static String readApi(String uri) throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(uri))
                            .header("User-Agent", "curl/8.1.2")
                            .header("Accept", "application/json")
                            .GET().build();
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        }
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("Server unavailable");
        }
    }
}
