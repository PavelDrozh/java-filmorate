package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class InMemoryUserService implements Service<User> {

    private final Validator<User> validator = new UserValidator();
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @Override
    public User create(User user) {
        validator.validate(user);
        if (user.getId() == 0) {
            user.setId(++userId);
            users.put(user.getId(), user);
            log.info("Создание пользователя успешно, пользователь: {}", user);
        } else {
            ValidationException ex =  new ValidationException("Создать нового пользователя с ID невозможно");
            validator.logAndThrowException(ex);
        }
        return user;

    }

    @Override
    public User updateOrCreate(User user) {
        validator.validate(user);
        if (user.getId() == 0) {
            user.setId(++userId);
            users.put(user.getId(), user);
            log.info("Создание пользователя успешно, пользователь: {}", user);
        } else if (users.containsKey(user.getId())){
            users.put(user.getId(), user);
            log.info("Обновление пользователя успешно, пользователь: {}", user);
        } else {
            ValidationException ex =  new ValidationException("Создать или обновить пользователя не удалось");
            validator.logAndThrowException(ex);
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }
}
