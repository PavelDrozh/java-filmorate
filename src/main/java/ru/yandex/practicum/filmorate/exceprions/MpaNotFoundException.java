package ru.yandex.practicum.filmorate.exceprions;

public class MpaNotFoundException extends RuntimeException {

    public MpaNotFoundException(String message) {
        super(message);
    }
}
