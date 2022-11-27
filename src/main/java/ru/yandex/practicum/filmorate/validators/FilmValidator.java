package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
@Slf4j
public class FilmValidator implements Validator<Film> {

    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate EARLIER_RELEASE_DATE = LocalDate.of(1895, 12,28);

    @Override
    public void validate(Film film) {
        if (film == null) {
            logAndThrowException(new ValidationException("Отсутствует тело запроса"));
        }
        if (film.getName() == null || film.getName().isBlank()) {
            logAndThrowException(new ValidationException("Отсутствует имя фильма"));
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            logAndThrowException(
                    new ValidationException("Слишком длинное описание, максимальное - " + MAX_DESCRIPTION_LENGTH));
        }
        if (film.getReleaseDate().isBefore(EARLIER_RELEASE_DATE)) {
            logAndThrowException(
                    new ValidationException("Фильм не должен быть выпущен ранее чем " + EARLIER_RELEASE_DATE));
        }
        if (film.getDuration() <= 0) {
            logAndThrowException(
                    new ValidationException("Фильм не может иметь отрицательную продолжительность"));
        }
    }

    public void logAndThrowException(ValidationException ex) {
        log.warn(ex.getMessage());
        throw ex;
    }
}
