package ru.yandex.practicum.filmorate.storage.database;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceprions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreDbStorage implements GenreStorage {

    JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre create(Genre genre) {
        return null;
    }

    @Override
    public Genre updateOrCreate(Genre genre) {
        return null;
    }

    @Override
    public List<Genre> getAll() {
        String findAllMpa = "SELECT genre_id, name " +
                "FROM genre ORDER BY genre_id";
        return jdbcTemplate.query(findAllMpa, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet rs, int i) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public Genre getById(int id) {
        String findGenreById = "SELECT genre_id, name " +
                "FROM genre WHERE genre_id = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(findGenreById, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(String.format("Genre  с id = %d не найден", id));
        }
        return genre;
    }

    @Override
    public List<Genre> getFilmsGenre(int filmId) {
        String findAllMpa = "SELECT genre.genre_id, name " +
                "FROM genre " +
                "RIGHT JOIN film_genre on genre.genre_id = film_genre.genre_id " +
                "WHERE film_genre.film_id = ? ORDER BY genre.genre_id";
        return jdbcTemplate.query(findAllMpa, this::mapRowToGenre, filmId);
    }
}
