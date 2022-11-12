package ru.yandex.practicum.filmorate.exceprions;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
