package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResourceSupplier {
    List<Genre> expectedGenre = List.of(Genre.builder().id(1).name("Комедия").build(),
            Genre.builder().id(2).name("Драма").build(),
            Genre.builder().id(3).name("Мультфильм").build(),
            Genre.builder().id(4).name("Триллер").build(),
            Genre.builder().id(5).name("Документальный").build(),
            Genre.builder().id(6).name("Боевик").build());

    List<Mpa> expectedMpa = List.of(Mpa.builder().id(1).name("G").build(),
            Mpa.builder().id(2).name("PG").build(),
            Mpa.builder().id(3).name("PG-13").build(),
            Mpa.builder().id(4).name("R").build(),
            Mpa.builder().id(5).name("NC-17").build());

    public Film getFirstFilm() {
        return Film.builder()
                .name("firstFilm")
                .description("first description")
                .duration(25)
                .releaseDate(LocalDate.of(1999, 11, 23))
                .mpa(Mpa.builder().id(2).build())
                .build();
    }

    public Film getSecondFilm() {
        return Film.builder()
                .name("secondFilm")
                .description("second description")
                .duration(45)
                .releaseDate(LocalDate.of(2001, 3, 15))
                .mpa(Mpa.builder().id(4).build())
                .build();
    }

    public User getFirstUser() {
        return  User.builder()
                .email("mail@mail.mail")
                .login("login")
                .name("Joe")
                .birthday(LocalDate.of(1994, 8, 14))
                .build();
    }

    public User getSecondUser() {
        return User.builder()
                .email("secondmail@mail.mail")
                .login("secondLogin")
                .name("secondJoe")
                .birthday(LocalDate.of(2000, 8, 14))
                .build();
    }

    public User getThirdUser() {
        return User.builder()
                .email("thirdmail@mail.mail")
                .login("thirdLogin")
                .name("thirdJoe")
                .birthday(LocalDate.of(2005, 8, 14))
                .build();
    }

    List<Genre> genres = List.of(Genre.builder().id(1).build(), Genre.builder().id(3).build());

}
