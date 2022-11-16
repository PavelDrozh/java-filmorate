package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(FilmStorage filmStorage, UserStorage userStorage){
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film rateFilm(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = getUser(userId);
        film.getLikes().add(user.getId());
        filmStorage.updateOrCreate(film);
        return film;
    }

    private User getUser(int userId) {
        @NotNull(message = "Пользователь не зарегистрирован") User user = userStorage.getById(userId);
        return user;
    }

    private Film getFilm(int filmId) {
        @NotNull(message = "Такого фильма нет") Film film = filmStorage.getById(filmId);
        return film;
    }

    @Override
    public Film removeRate(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = getUser(userId);
        film.getLikes().remove(user.getId());
        filmStorage.updateOrCreate(film);
        return film;
    }

    @Override
    public List<Film> getMostRated(int size) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(f -> - f.getLikes().size()))
                .limit(size).collect(Collectors.toList());
    }
}
