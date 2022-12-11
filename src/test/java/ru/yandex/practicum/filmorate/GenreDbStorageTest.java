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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.GenreDbStorage;

import java.time.LocalDate;
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

    List<Genre> expectedGenre = List.of(Genre.builder().id(1).name("Комедия").build(),
                                        Genre.builder().id(2).name("Драма").build(),
                                        Genre.builder().id(3).name("Мультфильм").build(),
                                        Genre.builder().id(4).name("Триллер").build(),
                                        Genre.builder().id(5).name("Документальный").build(),
                                        Genre.builder().id(6).name("Боевик").build());

    Film film = Film.builder()
            .name("firstFilm")
            .description("first description")
            .duration(25)
            .releaseDate(LocalDate.of(1999, 11, 23))
            .mpa(Mpa.builder().id(2).build())
            .genres(List.of(expectedGenre.get(0), expectedGenre.get(3)))
            .build();


    @Test
    public void getAllGenresTest() {
        List<Genre> genres = genreDbStorage.getAll();
        assertThat(genres).isNotNull().isNotEmpty().isEqualTo(expectedGenre);
        assertThat(genres.size()).isEqualTo(6);
    }

    @Test
    public void getGenreByIdTest() {
        Genre genre = genreDbStorage.getById(3);
        assertThat(genre).isNotNull().isEqualTo(expectedGenre.get(2));
    }

    @Test
    public void getGenreByIncorrectIdTest() {
        assertThatThrownBy(() -> genreDbStorage.getById(1000))
                .isInstanceOf(GenreNotFoundException.class);
    }

    @Test
    public void getGenreByFilmIdTest() {
        Film createdFilm = filmStorage.create(film);
        List<Genre> filmsGenres = genreDbStorage.getFilmsGenre(createdFilm.getId());
        assertThat(filmsGenres).isNotNull().isNotEmpty();
        assertThat(filmsGenres.size()).isEqualTo(2);
        assertThat(filmsGenres.get(0)).isEqualTo(expectedGenre.get(0));
        assertThat(filmsGenres.get(1)).isEqualTo(expectedGenre.get(3));
    }
}
