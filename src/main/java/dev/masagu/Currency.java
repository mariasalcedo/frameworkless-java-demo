package dev.masagu;

import dev.masagu.client.ForexRestClient;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

import java.io.StringReader;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public record Currency(String from, String to, Double rate, Double amount) implements TravelComponent {
    public static Currency fromJson(String json, String from) {
        try (JsonParser parser = Json.createParser(new StringReader(json))) {
            parser.next();
            JsonObject jsonObject = parser.getObject();
            JsonNumber rate = jsonObject.getJsonObject("rates")
                    .getJsonNumber("EUR");
            JsonNumber amount = jsonObject.getJsonNumber("amount");
            return new Currency(from, "EUR", rate.doubleValue(), amount.doubleValue());
        }
    }

    public static Currency getEuroValueFrom(String currency) throws InterruptedException {
        try (var scope = new StructuredTaskScope<Currency>()) {
            Subtask<Currency> subtask = scope.fork(() -> ForexRestClient.getEuroConversionFrom(currency));
            scope.join();
            return subtask.get();
        }
    }
}
