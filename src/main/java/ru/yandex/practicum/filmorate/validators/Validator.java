package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exceprions.ValidationException;

public interface Validator<T> {
    void validate(T t);

    void logAndThrowException(ValidationException ex);

}
