package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class InMemoryFilmService implements Service<Film> {

    private final Validator<Film> validator = new FilmValidator();

    private int filmId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        validator.validate(film);
        if (film.getId() == 0) {
            film.setId(++filmId);
            films.put(film.getId(), film);
            log.info("Создание фильма успешно, фильм: {}", film);
        } else {
            ValidationException ex =  new ValidationException("Создать новый фильм с ID невозможно");
            validator.logAndThrowException(ex);
        }
        return film;
    }

    @Override
    public Film updateOrCreate(Film film) {
        validator.validate(film);
        if (film.getId() == 0) {
            film.setId(++filmId);
            films.put(film.getId(), film);
            log.info("Создание фильма успешно, фильм: {}", film);
        } else if (films.containsKey(film.getId())){
            films.put(film.getId(), film);
            log.info("Обновление фильма успешно, фильм: {}", film);
        } else {
            ValidationException ex = new ValidationException("Создать или обновить фильм не удалось");
            validator.logAndThrowException(ex);
        }
        return film;    }

    @Override
    public List<Film> getAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }
}
