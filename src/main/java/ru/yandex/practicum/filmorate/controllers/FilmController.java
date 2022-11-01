package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;
import ru.yandex.practicum.filmorate.service.Service;

import java.util.List;


@RestController
@RequestMapping("/films")
public class FilmController {
    Service<Film> service = new InMemoryFilmService();

    @GetMapping
    public List<Film> findAll() {
        return service.getAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return service.create(film);
    }

    @PutMapping
    public Film updateOrCreate(@RequestBody Film film){
        return service.updateOrCreate(film);
    }
}
