package ru.yandex.practicum.filmorate.storage.database;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceprions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.List;
import java.util.LinkedHashSet;


@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    JdbcTemplate jdbcTemplate;
    FilmValidator validator;
    GenreStorage genreStorage;
    MpaStorage mpaStorage;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmValidator validator, GenreStorage genreStorage, 
                         MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.validator = validator;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film create(Film film) {
        validator.validate(film);
        if (film.getId() == 0) {
            createFilm(film);
        } else {
            ValidationException ex =  new ValidationException("Создать новый фильм с ID невозможно");
            validator.logAndThrowException(ex);
        }
        return this.getById(film.getId());
    }

    private void createFilm(Film film) {
        String insertFilm = "insert into films(name, description, release_date, duration, rate_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(insertFilm, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            createFilmsGenre(film);
        }
        log.info("Создание фильма успешно, фильм: {}", film);
    }

    private void createFilmsGenre(Film film) {
        StringBuilder insertFilmGenre = new StringBuilder("insert into film_genre(film_id, genre_id) " +
                "values ");
        Set<Genre> genreSet = new HashSet<>(film.getGenres());
        genreSet.forEach(genre -> insertFilmGenre
                .append("(")
                .append(film.getId())
                .append(", ")
                .append(genre.getId())
                .append("), "));
        insertFilmGenre.delete(insertFilmGenre.length() - 2, insertFilmGenre.length());
        jdbcTemplate.update(String.valueOf(insertFilmGenre));
    }

    @Override
    public Film updateOrCreate(Film film) {
        validator.validate(film);
        if (film.getId() == 0) {
            createFilm(film);
        } else if (getById(film.getId()) != null) {
            updateFilm(film);
        } else {
            throw new FilmNotFoundException("Создать или обновить фильм не удалось");
        }
        return this.getById(film.getId());
    }

    private void updateFilm(Film film) {
        String updateFilm = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(updateFilm
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        deleteFilmsGenre(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            createFilmsGenre(film);
        }
        log.info("Обновление фильма успешно, фильм: {}", film);
    }

    private void deleteFilmsGenre(Film film) {
        String deleteGenre = "DELETE FROM film_genre " +
                "WHERE film_id = ?";
        jdbcTemplate.update(deleteGenre
                , film.getId());
    }

    @Override
    public List<Film> getAll() {
        String findAllQuery = "SELECT id, name, description, release_date, duration, rate_id " +
                "FROM films";
        List<Film> films = jdbcTemplate.query(findAllQuery, this::mapRowToFilm);
        log.info("Найдено {} фильмов", films.size());
        return films;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int rateId = rs.getInt("rate_id");
        Mpa rate = mpaStorage.getById(rateId);
        int filmId = rs.getInt("id");
        List<Genre> genres = genreStorage.getFilmsGenre(filmId);
        Set<Integer> likes = new LinkedHashSet<>(this.getLikes(filmId));
        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(rate)
                .genres(genres)
                .likes(likes)
                .build();
    }

    private List<Integer> getLikes(int filmId) {
        String findLikesQuery = "SELECT user_id " +
                "FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(findLikesQuery, this::mapRowToUserId, filmId);
    }

    private Integer mapRowToUserId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("user_id");
    }

    @Override
    public Film getById(int id) {
        String findFilmQuery = "SELECT id, name, description, release_date, duration, rate_id " +
                "FROM films WHERE id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(findFilmQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Фильма с id = %d нет в базе данных", id));
        }
        log.info("Найден фильм: {}", film);
        return film;
    }

    public List<Film> getBest(int size) {
    String selectMostLiked = "SELECT id, name, description, release_date, duration, rate_id " +
            "FROM films LEFT JOIN (SELECT film_id, count(user_id) FROM likes GROUP BY film_id " +
            "ORDER BY count(user_id) DESC) AS f " +
            "ON films.id = f.film_id ORDER BY id DESC LIMIT ?";
        return jdbcTemplate.query(selectMostLiked, this::mapRowToFilm, size);
    }
}
