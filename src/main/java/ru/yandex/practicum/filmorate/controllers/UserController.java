package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.service.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    Service<User> service = new InMemoryUserService();

    @GetMapping
    public List<User> findAll() {
        return service.getAll();
    }

    @PostMapping
    public User create(@RequestBody @NotNull User user) {
        return service.create(user);
    }

    @PutMapping
    public User updateOrCreate(@RequestBody User user){
        return service.updateOrCreate(user);
    }

}
