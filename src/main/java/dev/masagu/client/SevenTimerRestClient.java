package dev.masagu.client;

import dev.masagu.Weather;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneOffset;

public final class SevenTimerRestClient extends RestClient {

    public static final String SERVER_URI = "https://www.7timer.info/bin/civil.php?lon=11.4&lat=48.8&ac=0&unit=metric&output=json&tzshift=0";

    public static Weather fromJson(String json) {
        try (JsonParser parser = Json.createParser(new StringReader(json))) {
            parser.next();
            JsonObject jsonObject = parser.getObject();
            int timeslot = (Instant.now().atZone(ZoneOffset.of("+02:00")).getHour()/3)-1;
            JsonObject current = jsonObject.getJsonArray("dataseries")
                    .getJsonObject(timeslot);
            JsonNumber temperature2m = current.getJsonNumber("temp2m");
            return new Weather("7timer.info", "Ingolstadt", temperature2m.intValue());
        }
    }

    public static Weather readForecast() throws IOException, InterruptedException {
        String response = RestClient.readApi(SERVER_URI);
        return fromJson(response);
    }
}
