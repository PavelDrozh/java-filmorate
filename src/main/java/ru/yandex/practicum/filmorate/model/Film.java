package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Film {

    int id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;

}
