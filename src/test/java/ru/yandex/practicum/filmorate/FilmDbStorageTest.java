package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceprions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmDbStorageTest {

    FilmDbStorage filmStorage;
    JdbcTemplate jdbcTemplate;
    ResourceSupplier rs;

    @BeforeEach
    public void rebootTable() {
        String dropTable = "DROP TABLE films CASCADE";
        jdbcTemplate.update(dropTable);
        String newTable = "CREATE TABLE IF NOT EXISTS films (" +
                "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "name varchar(50) NOT NULL, " +
                "description varchar(200) NOT NULL, " +
                "release_date DATE NOT NULL , " +
                "duration INTEGER CHECK duration > 0, " +
                "rate_id INTEGER REFERENCES rate(rate_id))";
        jdbcTemplate.update(newTable);
    }

    @Test
    public void createFilmTest() {
        Film created = filmStorage.create(rs.getFirstFilm());
        assertThat(created).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void createFilmIsNotExistTest() {
        Film created = filmStorage.updateOrCreate(rs.getFirstFilm());
        assertThat(created).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void updateFilmTest() {
        Film created = filmStorage.create(rs.getFirstFilm());
        created.setGenres(rs.getGenres());
        created.setName("newFilmName");
        created.setDescription("newFilmDescription");
        Film updated = filmStorage.updateOrCreate(created);

        assertThat(updated).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "newFilmName")
                .hasFieldOrPropertyWithValue("description", "newFilmDescription");
        assertThat(updated.getGenres().size()).isEqualTo(2);
        assertThat(updated.getGenres().get(0)).hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat(updated.getGenres().get(1)).hasFieldOrPropertyWithValue("name", "Мультфильм");
    }

    @Test
    public void updateFilmWithIncorrectIdTest() {
        Film film = rs.getFirstFilm();
        film.setId(1000);
        assertThatThrownBy(() -> filmStorage.updateOrCreate(film))
                .isInstanceOf(FilmNotFoundException.class);
    }

    @Test
    public void getAllFilmsTest() {
        filmStorage.create(rs.getFirstFilm());
        filmStorage.create(rs.getSecondFilm());

        List<Film> films = filmStorage.getAll();
        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0)).isNotNull().hasFieldOrPropertyWithValue("id", 1);
        assertThat(films.get(1)).isNotNull().hasFieldOrPropertyWithValue("id", 2);
    }

    @Test
    public void getByIdTest() {
        filmStorage.create(rs.getFirstFilm());
        filmStorage.create(rs.getSecondFilm());

        Film film = filmStorage.getById(2);
        assertThat(film).isNotNull()
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("name", "secondFilm")
                .hasFieldOrPropertyWithValue("description", "second description");
        assertThat(film.getMpa()).isNotNull()
                .hasFieldOrPropertyWithValue("name", "R")
                .hasFieldOrPropertyWithValue("id", 4);
    }

    @Test
    public void getBestTest() {
        filmStorage.create(rs.getFirstFilm());
        filmStorage.create(rs.getSecondFilm());

        List<Film> films = filmStorage.getBest(10);
        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0)).isNotNull().hasFieldOrPropertyWithValue("id", 2);
        assertThat(films.get(1)).isNotNull().hasFieldOrPropertyWithValue("id", 1);
    }
}
