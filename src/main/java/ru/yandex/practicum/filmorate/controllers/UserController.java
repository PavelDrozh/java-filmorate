package ru.yandex.practicum.filmorate.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    private final static String ID_PATH = "/{id}";
    private final static String FRIENDS_PATH = "/friends";
    private final static String FRIENDS_ID_PATH = "/{friendId}";

    UserStorage storage;
    UserService service;
    @Autowired
    public UserController(UserStorage storage, UserService service) {
        this.storage = storage;
        this.service = service;
    }

    @GetMapping
    public List<User> findAll() {
        return storage.getAll();
    }

    @PostMapping
    public User create(@RequestBody @NotNull User user) {
        return storage.create(user);
    }

    @PutMapping
    public User updateOrCreate(@RequestBody User user) {
        return storage.updateOrCreate(user);
    }

    @GetMapping(ID_PATH)
    public User getUserById(@PathVariable int id) {
        return storage.getById(id);
    }

    @PutMapping(ID_PATH + FRIENDS_PATH + FRIENDS_ID_PATH)
    public User addFriend(@PathVariable int friendId, @PathVariable int id) {
        return service.addFriend(id, friendId);
    }

    @DeleteMapping(ID_PATH + FRIENDS_PATH + FRIENDS_ID_PATH)
    public User deleteFriend(@PathVariable int friendId, @PathVariable int id) {
        return service.removeFriend(id, friendId);
    }

    @GetMapping(ID_PATH + FRIENDS_PATH)
    public Set<User> getFriends(@PathVariable int id) {
        return service.getFriends(id);
    }

    @GetMapping(ID_PATH + FRIENDS_PATH + "/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        return service.getMutualFriends(id, otherId);
    }
}
