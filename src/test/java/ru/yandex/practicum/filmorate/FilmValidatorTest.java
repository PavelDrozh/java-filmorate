package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
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

    private static final String NORMAL_NAME = "name";
    private static final String NORMAL_DESCRIPTION = "Достаточно короткое описание занимающее менее 200.";
    private static final int NORMAL_DURATION = 10;
    private final Validator<Film> validator = new FilmValidator();
    Film film;

    @BeforeEach
    void setFilm() {
        film = new Film();
        film.setName(NORMAL_NAME);
        film.setDescription(NORMAL_DESCRIPTION);
        film.setReleaseDate(FilmValidator.EARLIER_RELEASE_DATE);
        film.setDuration(NORMAL_DURATION);
    }

    @Test
    void validateNull() {
        validateFilm(null, "Отсутствует тело запроса");
    }

    @Test
    void validateNullName() {
        validateName(null);
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности имени \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    void validateBlankName(String name) {
        validateName(name);
    }

    private void validateName(String name) {
        film.setName(name);
        validateFilm(film, "Отсутствует имя фильма");
    }

    @Test
    void validateLongDescription() {
        film.setDescription("Очень длинное описание которое должно занимать 201 символов." +
                "Очень длинное описание которое должно занимать 201 символов." +
                "Очень длинное описание которое должно занимать 201 символов." +
                "И еще 21 символ!!!!!!");
        validateFilm(film, "Слишком длинное описание, максимальное - " + FilmValidator.MAX_DESCRIPTION_LENGTH);
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности даты релиза \"{arguments}\"")
    @ValueSource(strings = {"1800-12-12", "1200-01-01", "1895-12-27"})
    void validateReleaseDate(String date) {
        LocalDate parseDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        film.setReleaseDate(parseDate);
        validateFilm(film, "Фильм не должен быть выпущен ранее чем " + FilmValidator.EARLIER_RELEASE_DATE);
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности длительности фильма \"{arguments}\"")
    @ValueSource(ints = {Integer.MIN_VALUE, -10, -5, -1})
    void validateDuration(int duration) {
        film.setDuration(duration);
        validateFilm(film, "Фильм не может иметь отрицательную продолжительность");
    }

    private void validateFilm(Film film, String expected) {
        ValidationException ex = getValidationEx(film);
        assertEquals(expected, ex.getMessage());
    }

    private ValidationException getValidationEx(Film film) {
        return assertThrows(ValidationException.class,
                () -> validator.validate(film));
    }

}
