package ru.yandex.practicum.filmorate.service;

import java.util.List;

public interface Service<T> {
    T create(T t);
    T updateOrCreate(T t);
    List<T> getAll();
}
