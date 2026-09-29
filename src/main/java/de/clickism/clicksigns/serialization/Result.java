package de.clickism.clicksigns.serialization;

import java.util.Optional;
import java.util.function.Function;

public class Result<T> {
    private final T value;
    private final Exception exception;

    public Result(T value, Exception exception) {
        this.value = value;
        this.exception = exception;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(Exception exception) {
        return new Result<>(null, exception);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>(null, new Exception(message));
    }

    public static <T> Result<T> ofOptional(Optional<T> optional, String failureMessage) {
        return optional.map(Result::success)
            .orElseGet(() -> Result.failure(failureMessage));
    }

    public static <T> Result<T> ofOptional(Optional<T> optional, Exception exception) {
        return optional.map(Result::success)
            .orElseGet(() -> Result.failure(exception));
    }

    public boolean isSuccess() {
        return exception == null;
    }

    public boolean isFailure() {
        return exception != null;
    }

    public Exception exception() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get exception from a successful result");
        }
        return exception;
    }

    public T orElseThrow() {
        if (isFailure()) {
            throw new RuntimeException(exception);
        }
        return value;
    }

    public T orElse(T other) {
        return isSuccess() ? value : other;
    }

    public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        if (isSuccess()) {
            try {
                return Result.success(mapper.apply(value));
            } catch (Exception e) {
                return Result.failure(e);
            }
        } else {
            return Result.failure(exception);
        }
    }
}
