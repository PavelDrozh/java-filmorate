package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {

    private final Validator<Film> validator = new FilmValidator();

    @Test
    void validateNull() {
        ValidationException ex = getValidationEx(null);
        assertEquals("Отсутствует тело запроса", ex.getMessage());
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности имени \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    void validateBlankName(String name) {
        Film film = new Film();
        film.setName(name);
        ValidationException ex = getValidationEx(film);
        assertEquals("Отсутствует имя фильма", ex.getMessage());
    }

    @Test
    void validateNullName() {
        Film film = new Film();
        film.setName(null);
        ValidationException ex = getValidationEx(film);
        assertEquals("Отсутствует имя фильма", ex.getMessage());
    }

    @Test
    void validateLongDescription() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("Очень длинное описание которое должно занимать 201 символов." +
                "Очень длинное описание которое должно занимать 201 символов." +
                "Очень длинное описание которое должно занимать 201 символов." +
                "И еще 21 символ!!!!!!");
        ValidationException ex = getValidationEx(film);
        assertEquals("Слишком длинное описание, максимальное - " + FilmValidator.MAX_DESCRIPTION_LENGTH,
                ex.getMessage());
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности даты релиза \"{arguments}\"")
    @ValueSource(strings = {"1800-12-12", "1200-01-01", "1895-12-27"})
    void validateReleaseDate(String date) {
        Film film = new Film();
        film.setName("name");
        film.setDescription("Достаточно короткое описание занимающее менее 200.");
        LocalDate parseDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        film.setReleaseDate(parseDate);
        ValidationException ex = getValidationEx(film);
        assertEquals("Фильм не должен быть выпущен ранее чем " + FilmValidator.EARLIER_RELEASE_DATE,
                ex.getMessage());
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности даты релиза \"{arguments}\"")
    @ValueSource(ints = {Integer.MIN_VALUE, -10, -5, -1})
    void validateDuration(int duration) {
        Film film = new Film();
        film.setName("name");
        film.setDescription("Достаточно короткое описание занимающее менее 200.");
        film.setReleaseDate(FilmValidator.EARLIER_RELEASE_DATE);
        film.setDuration(duration);
        ValidationException ex = getValidationEx(film);
        assertEquals("Фильм не может иметь отрицательную продолжительность", ex.getMessage());
    }

    private ValidationException getValidationEx(Film film) {
        return assertThrows(ValidationException.class,
                () -> validator.validate(film));
    }
}
