package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceprions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceprions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.database.DbFilmService;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmDbServiceTest {

    DbFilmService dbFilmService;
    FilmDbStorage filmStorage;
    UserDbStorage userStorage;
    JdbcTemplate jdbcTemplate;
    ResourceSupplier rs;

    @BeforeEach
    public void insertFilmUser() {
        userStorage.create(rs.getFirstUser());
        filmStorage.create(rs.getFirstFilm());
    }

    @AfterEach
    public void rebootTables() {
        String dropUserTable = "DROP TABLE users_storage CASCADE";
        jdbcTemplate.update(dropUserTable);
        String newUserTable = "CREATE TABLE IF NOT EXISTS users_storage (" +
                "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "email varchar(50) NOT NULL, " +
                "name varchar(50) NOT NULL, " +
                "login varchar(50) NOT NULL, " +
                "birthday DATE NOT NULL)";
        jdbcTemplate.update(newUserTable);

        String dropFilmTable = "DROP TABLE films CASCADE";
        jdbcTemplate.update(dropFilmTable);
        String newFilmTable = "CREATE TABLE IF NOT EXISTS films (" +
                "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "name varchar(50) NOT NULL, " +
                "description varchar(200) NOT NULL, " +
                "release_date DATE NOT NULL , " +
                "duration INTEGER CHECK duration > 0, " +
                "rate_id INTEGER REFERENCES rate(rate_id))";
        jdbcTemplate.update(newFilmTable);
    }

    @Test
    public void addLikeTest() {
        Film film = dbFilmService.likeFilm(1,1);
        assertThat(film).isNotNull();
        assertThat(film.getLikes()).isNotNull().isNotEmpty();
        assertThat(film.getLikes().size()).isEqualTo(1);
    }

    @Test
    public void removeLikeTest() {
        dbFilmService.likeFilm(1,1);
        Film film = dbFilmService.removeLike(1, 1);
        assertThat(film).isNotNull();
        assertThat(film.getLikes()).isEmpty();
    }

    @Test
    public void addLikeWithIncorrectFilmIdTest() {
        assertThatThrownBy(() -> dbFilmService.likeFilm(1000,1))
                .isInstanceOf(FilmNotFoundException.class);
    }

    @Test
    public void addLikeWithIncorrectUserIdTest() {
        assertThatThrownBy(() -> dbFilmService.likeFilm(1,1000))
                .isInstanceOf(UserNotFoundException.class);
    }
}
