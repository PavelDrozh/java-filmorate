package ru.yandex.practicum.filmorate.exceprions;

public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(String message) {
        super(message);
    }
}
