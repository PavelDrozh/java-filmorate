package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceprions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserStorage implements UserStorage {

    final Validator<User> validator;
    final Map<Integer, User> users = new HashMap<>();
    int userId = 0;

    @Autowired
    public InMemoryUserStorage (Validator<User> validator) {
        this.validator = validator;
    }

    @Override
    public User create(User user) {
        validator.validate(user);
        if (user.getId() == 0) {
            createUser(user);
        } else {
            ValidationException ex =  new ValidationException("Создать нового пользователя с ID невозможно");
            validator.logAndThrowException(ex);
        }
        return user;

    }

    private void createUser(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
        log.info("Создание пользователя успешно, пользователь: {}", user);
    }

    @Override
    public User updateOrCreate(User user) {
        validator.validate(user);
        if (user.getId() == 0) {
            createUser(user);
        } else if (users.containsKey(user.getId())){
            users.put(user.getId(), user);
            log.info("Обновление пользователя успешно, пользователь: {}", user);
        } else {
            throw new UserNotFoundException("Обновить пользователя не удалось");
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        User user = users.getOrDefault(id, null);
        if (user == null) throw new UserNotFoundException(String.format("Пользователь %d не найден", id));
        log.info("Найден пользователь: {}", user);
        return user;
    }
}

