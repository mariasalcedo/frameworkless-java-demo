package dev.masagu;

import dev.masagu.client.OpenMeteoRestClient;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

import java.io.StringReader;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public record Geocode(String city, Double latitude, Double longitude) {

    public static class GeocodeException extends RuntimeException {
        public GeocodeException(Throwable e) {
            super(e);
        }
    }

    public static Geocode fromJson(String json, String city) {
        try (JsonParser parser = Json.createParser(new StringReader(json))) {
            parser.next();
            JsonObject jsonObject = parser.getObject();
            JsonObject data = jsonObject.getJsonArray("results")
                    .getJsonObject(0);
            JsonNumber latitude = data.getJsonNumber("latitude");
            JsonNumber longitude = data.getJsonNumber("longitude");
            return new Geocode(city, latitude.doubleValue(), longitude.doubleValue());
        }
    }

    public static Geocode getCoordinates(String city) {
        try(var scope = new StructuredTaskScope<Geocode>()){
            Subtask<Geocode> subtask = scope.fork(() -> OpenMeteoRestClient.getCoordinates(city));
            scope.join();
            return subtask.get();
        } catch (InterruptedException e) {
            throw new GeocodeException(e);
        }
    }
}
