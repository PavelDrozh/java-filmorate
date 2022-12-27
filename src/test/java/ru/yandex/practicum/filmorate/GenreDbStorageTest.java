package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceprions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.GenreDbStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreDbStorageTest {

    GenreDbStorage genreDbStorage;
    FilmDbStorage filmStorage;
    ResourceSupplier rs;

    @Test
    public void getAllGenresTest() {
        List<Genre> genres = genreDbStorage.getAll();
        assertThat(genres).isNotNull().isNotEmpty().isEqualTo(rs.getExpectedGenre());
        assertThat(genres.size()).isEqualTo(6);
    }

    @Test
    public void getGenreByIdTest() {
        Genre genre = genreDbStorage.getById(3);
        assertThat(genre).isNotNull().isEqualTo(rs.getExpectedGenre().get(2));
    }

    @Test
    public void getGenreByIncorrectIdTest() {
        assertThatThrownBy(() -> genreDbStorage.getById(1000))
                .isInstanceOf(GenreNotFoundException.class);
    }

    @Test
    public void getGenreByFilmIdTest() {
        Film film = rs.getFirstFilm();
        film.setGenres(List.of(rs.getExpectedGenre().get(0), rs.getExpectedGenre().get(3)));
        Film createdFilm = filmStorage.create(film);
        List<Genre> filmsGenres = genreDbStorage.getFilmsGenre(createdFilm.getId());
        assertThat(filmsGenres).isNotNull().isNotEmpty();
        assertThat(filmsGenres.size()).isEqualTo(2);
        assertThat(filmsGenres.get(0)).isEqualTo(rs.getExpectedGenre().get(0));
        assertThat(filmsGenres.get(1)).isEqualTo(rs.getExpectedGenre().get(3));
    }
}
