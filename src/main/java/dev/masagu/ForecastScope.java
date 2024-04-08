package dev.masagu;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.StructuredTaskScope;

public class ForecastScope extends StructuredTaskScope<Weather> {
    // due to writing and reading actions could eventually happen in two different threads
    // to prevent racing conditions, the collection is a final Concurrent collection
    private final Collection<Weather> forecasts = new ConcurrentLinkedDeque<>();
    private final Collection<Throwable> exceptions = new ConcurrentLinkedDeque<>();

    public static class ForecastException extends RuntimeException {
    }

    @Override
    protected void handleComplete(Subtask<? extends Weather> subtask) {
        switch(subtask.state()){
            case SUCCESS -> this.forecasts.add(subtask.get());
            case FAILED -> this.exceptions.add(subtask.exception());
            case UNAVAILABLE -> {}
        }
    }

    public Double getAverageTemperature() {
        return forecasts.stream()
                .mapToDouble(Weather::temperature)
                .average()
                .orElseThrow(this::exceptions);
    }

    public ForecastException exceptions(){
        ForecastException exception = new ForecastException();
        this.exceptions.forEach(exception::addSuppressed);
        return exception;
    }
}
