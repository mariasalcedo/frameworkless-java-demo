package dev.masagu.client;

import dev.masagu.Currency;

import java.io.IOException;

public final class ForexRestClient extends RestClient {
    public static final String SERVER_URI = "https://api.frankfurter.app/latest?amount=1&to=EUR";

    public static Currency getEuroConversionFrom(String from) throws IOException, InterruptedException {
        String response = RestClient.readApi(String.format("%s&from=%s", SERVER_URI, from));
        return Currency.fromJson(response, from);
    }
}
