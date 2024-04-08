package dev.masagu.client;

import dev.masagu.Geocode;
import dev.masagu.Weather;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

import java.io.IOException;
import java.io.StringReader;

public final class OpenMeteoRestClient extends RestClient {

    public static final String FORECAST_URI = "https://api.open-meteo.com/v1/forecast?current=temperature_2m";
    public static final String GEOCODE_URI = "https://geocoding-api.open-meteo.com/v1/search?count=1&language=en&format=json";

    public static Weather fromJson(String json, String city) {
        try (JsonParser parser = Json.createParser(new StringReader(json))) {
            parser.next();
            JsonObject jsonObject = parser.getObject();
            JsonObject current = jsonObject.getJsonObject("current");
            JsonNumber temperature2m = current.getJsonNumber("temperature_2m");
            return new Weather("open-meteo.com", city, temperature2m.intValue());
        }
    }

    public static Weather readForecast(Geocode coordinates) throws IOException, InterruptedException {
        String response = RestClient.readApi(String.format("%s&latitude=%s&longitude=%s",
                FORECAST_URI, coordinates.latitude(), coordinates.longitude()));
        return fromJson(response, coordinates.city());
    }

    public static Geocode getCoordinates(String city) throws IOException, InterruptedException {
        String response = RestClient.readApi(String.format("%s&name=%s", GEOCODE_URI, city));
        return Geocode.fromJson(response, city);
    }
}
