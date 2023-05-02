package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Component
@Slf4j
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) {
        if (user == null) {
            logAndThrowException(new ValidationException("Отсутствует тело запроса"));
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            logAndThrowException(new ValidationException("Отсутствует почта"));
        }
        if (!user.getEmail().contains("@")) {
            logAndThrowException(new ValidationException("Отсутствует @"));
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            logAndThrowException(new ValidationException("Логин не должен быть пустым"));
        }
        if (user.getLogin().contains(" ")) {
            logAndThrowException(new ValidationException("Логин не должен содержать пробелов"));
        }
        if (user.getBirthday().isAfter(LocalDate.now()) || user.getBirthday().isEqual(LocalDate.now())) {
            logAndThrowException(new ValidationException("День рождения не может быть в будущем"));
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void logAndThrowException(ValidationException ex) {
        log.warn(ex.getMessage());
        throw ex;
    }
}
