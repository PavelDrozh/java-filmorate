package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
public class User {

    int id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Integer> friends = new LinkedHashSet<>();

}
