package ru.yandex.practicum.filmorate.service.database;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Qualifier("DbFilmService")
public class DbFilmService implements FilmService {

    FilmStorage filmStorage;
    UserStorage userStorage;
    JdbcTemplate jdbcTemplate;

    public DbFilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         @Qualifier("UserDbStorage")UserStorage userStorage,
                         JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film likeFilm(int filmId, int userId) {
        String insertLike = "INSERT INTO likes (film_id, user_id) " +
                "VALUES (?, ?) ";
        if (userStorage.getById(userId) != null && filmStorage.getById(filmId) != null) {
            jdbcTemplate.update(insertLike, filmId, userId);
        }
        return filmStorage.getById(filmId);
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        String deleteLike = "DELETE FROM likes " +
                "WHERE film_id = ? AND user_id = ?";
        if (userStorage.getById(userId) != null && filmStorage.getById(filmId) != null) {
            jdbcTemplate.update(deleteLike, filmId, userId);
        }
        return filmStorage.getById(filmId);
    }

    @Override
    public List<Film> getMostLiked(int size) {
        return filmStorage.getBest(size);
    }
}
