package ru.yandex.practicum.filmorate.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;


@RestController
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {

    FilmStorage storage;
    FilmService service;

    @Autowired
    public FilmController(FilmStorage storage, FilmService service) {
        this.storage = storage;
        this.service = service;
    }
    @GetMapping
    public List<Film> findAll() {
        return storage.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return storage.getById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return storage.create(film);
    }

    @PutMapping
    public Film updateOrCreate(@RequestBody Film film) {
        return storage.updateOrCreate(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film rateFilm(@PathVariable int id, @PathVariable int userId) {
        return service.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeRate(@PathVariable int id, @PathVariable int userId) {
        return service.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostRated(@RequestParam (defaultValue = "10") int count) {
        return service.getMostLiked(count);
    }
}
