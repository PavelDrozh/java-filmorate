package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Film {

    int id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    Set<Integer> likes =new LinkedHashSet<>();

}
