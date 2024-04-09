package dev.masagu;

import java.util.Objects;
import java.util.concurrent.StructuredTaskScope;

public record TravelInfo(Weather weather, Currency currency) {

    public static TravelInfo getInfo(String city, String currency){
        try(var scope = new TravelScope()){
            scope.fork(() -> Weather.readWeather(city));
            scope.fork(() -> Currency.getEuroValueFrom(currency));
            scope.join();

            return scope.getTravelInfo();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class TravelScope extends StructuredTaskScope<TravelComponent> {

        private volatile Throwable exception;
        private volatile Weather weather;
        private volatile Currency currency;

        @Override
        protected void handleComplete(Subtask<? extends TravelComponent> subtask) {
            switch (subtask.state()){
                case FAILED -> this.exception = subtask.exception();
                case UNAVAILABLE -> {}
                case SUCCESS -> {
                    switch (subtask.get()) {
                        case Weather weather -> this.weather = weather;
                        case Currency currency -> this.currency = currency;
                    }
                }
            }
        }

        public TravelInfo getTravelInfo() {
            return new TravelInfo(
                    Objects.requireNonNullElse(this.weather, new Weather("unknown", "unknown", 20)),
                    Objects.requireNonNullElse(this.currency, new Currency("unknown", "unknown", 0.0, 0.0))
            );
        }
    }
}
