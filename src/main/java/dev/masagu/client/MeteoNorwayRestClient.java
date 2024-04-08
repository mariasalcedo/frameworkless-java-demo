package dev.masagu.client;

import dev.masagu.Geocode;
import dev.masagu.Weather;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

import java.io.IOException;
import java.io.StringReader;

public final class MeteoNorwayRestClient extends RestClient {

    public static final String SERVER_URI = "https://api.met.no/weatherapi/locationforecast/2.0/compact.json";

    public static Weather fromJson(String json) {
        try (JsonParser parser = Json.createParser(new StringReader(json))) {
            parser.next();
            JsonObject jsonObject = parser.getObject();
            JsonObject current = jsonObject.getJsonObject("properties")
                    .getJsonArray("timeseries")
                    .getJsonObject(0)
                    .getJsonObject("data")
                    .getJsonObject("instant")
                    .getJsonObject("details");
            JsonNumber temperature2m = current.getJsonNumber("air_temperature");
            return new Weather("met.no", "Ingolstadt", temperature2m.intValue());
        }
    }

    public static Weather readForecast(Geocode coordinates) throws IOException, InterruptedException {
        String response = RestClient.readApi(String.format("%s?lat=%s&lon=%s", SERVER_URI, coordinates.latitude(), coordinates.longitude()));
        return fromJson(response);
    }
}