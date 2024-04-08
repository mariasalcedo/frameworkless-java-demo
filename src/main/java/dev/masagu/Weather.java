package dev.masagu;


import dev.masagu.client.MeteoNorwayRestClient;
import dev.masagu.client.OpenMeteoRestClient;
import dev.masagu.client.SevenTimerRestClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public record Weather(String server, String city, Integer temperature) {

    public static class WeatherException extends RuntimeException {
        public WeatherException(Throwable e) {
            super(e);
        }
    }

    public static Weather readWeather(String city) throws WeatherException {
        var coordinates = Geocode.getCoordinates(city);
        try (var scope = new StructuredTaskScope<Weather>()) {
            Subtask<Weather> subtask = scope.fork(() -> OpenMeteoRestClient.readForecast(coordinates));
            scope.join();
            return subtask.get();
        } catch(InterruptedException e) {
            throw new WeatherException(e);
        }
    }

    /*
    pattern:
    fork multiple objects from the same kind, join them, get a single result.
    Once one of the forked objects return, all others get cancelled, and a single result is returned
     */
    public static Weather requestMultipleWeather(String city) throws InterruptedException, ExecutionException {
        var coordinates = Geocode.getCoordinates(city);
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Weather>()) {
            Subtask<Weather> subtaskA = scope.fork(() -> OpenMeteoRestClient.readForecast(coordinates));
            Subtask<Weather> subtaskB = scope.fork(SevenTimerRestClient::readForecast);
            Subtask<Weather> subtaskC = scope.fork(() -> MeteoNorwayRestClient.readForecast(coordinates));
            scope.join();
            return scope.result();
        }
    }

    /*
    pattern:
    fork multiple objects,
    join them,
    get average after receiving all calls,
    get a single result.
    */
    public static Double readAverageTemperature(String city) throws InterruptedException {
        var coordinates = Geocode.getCoordinates(city);
        try (var scope = new ForecastScope()) {
            scope.fork(() -> OpenMeteoRestClient.readForecast(coordinates));
            //scope.fork(SevenTimerRestClient::readForecast);
            scope.fork(() -> MeteoNorwayRestClient.readForecast(coordinates));
            scope.join();
            return scope.getAverageTemperature();
        }
    }
}
