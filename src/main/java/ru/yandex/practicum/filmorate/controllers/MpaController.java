package ru.yandex.practicum.filmorate.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaController {

    MpaStorage mpaStorage;

    @Autowired
    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public List<Mpa> findAll() {
        return mpaStorage.getAll();
    }

    @GetMapping("/{id}")
    public Mpa getById (@PathVariable int id) {
        return mpaStorage.getById(id);
    }
}
