package dev.masagu;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

import java.io.StringReader;

public record Currency(String from, String to, Double rate, Double amount) {
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
}
