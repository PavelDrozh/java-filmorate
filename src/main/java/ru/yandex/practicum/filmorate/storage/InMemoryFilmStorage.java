package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceprions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Validator<Film> validator;

    private int filmId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Autowired
    public InMemoryFilmStorage (Validator<Film> validator) {
        this.validator = validator;
    }

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
            throw new FilmNotFoundException("Создать или обновить фильм не удалось");
        }
        return film;    }

    @Override
    public List<Film> getAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(int id) {
        Film film = films.getOrDefault(id, null);
        if (film == null) throw new FilmNotFoundException(String.format("Фильм %d не найден", id));
        log.info("Найден фильм: {}", film);
        return film;
    }
}

