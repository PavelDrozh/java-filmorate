package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film rateFilm(int filmId, int userId);
    Film removeRate(int filmId, int userId);
    List<Film> getMostRated(int size);
}
