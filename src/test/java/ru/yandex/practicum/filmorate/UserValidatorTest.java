package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {

    private final Validator<User> validator = new UserValidator();

    @Test
    void validateNull() {
        ValidationException ex = getValidationEx(null);
        assertEquals("Отсутствует тело запроса", ex.getMessage());
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности почты \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    void validateBlankEmail(String email) {
        User user = new User();
        user.setEmail(email);
        ValidationException ex = getValidationEx(user);
        assertEquals("Отсутствует почта", ex.getMessage());
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности почты (отсутствует @) \"{arguments}\"")
    @ValueSource(strings = {"asd.asd.com", "asdyasdnkasd", "email.yandex.ru", "yandex.ru"})
    void validateEmailWithoutA(String email) {
        User user = new User();
        user.setEmail(email);
        ValidationException ex = getValidationEx(user);
        assertEquals("Отсутствует @", ex.getMessage());
    }

    @Test
    void validateNullEmail() {
        User user = new User();
        user.setEmail(null);
        ValidationException ex = getValidationEx(user);
        assertEquals("Отсутствует почта", ex.getMessage());
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности логина \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    void validateBlankLogin(String login) {
        User user = new User();
        user.setEmail("email@yandex.ru");
        user.setLogin(login);
        ValidationException ex = getValidationEx(user);
        assertEquals("Логин не должен быть пустым", ex.getMessage());
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности логина \"{arguments}\"")
    @ValueSource(strings = {"asd asd", "asd   asd asd", "asd asdasd  ", " asd asd  asd ", " asd  asd  "})
    void validateLoginWithBlanks(String login) {
        User user = new User();
        user.setEmail("email@yandex.ru");
        user.setLogin(login);
        ValidationException ex = getValidationEx(user);
        assertEquals("Логин не должен содержать пробелов", ex.getMessage());
    }

    @Test
    void validateNullLogin() {
        User user = new User();
        user.setEmail("email@yandex.ru");
        user.setLogin(null);
        ValidationException ex = getValidationEx(user);
        assertEquals("Логин не должен быть пустым", ex.getMessage());
    }

    //Если тест не проходит - проверить првую дату, т.к. это проверка граничного уловия.
    @ParameterizedTest(name = "{index}. Проверка невалидности даты рождения \"{arguments}\"")
    @ValueSource(strings = {"2022-11-02", "2023-01-01", "2089-12-27"})
    void validateReleaseDate(String date) {
        User user = new User();
        user.setEmail("email@yandex.ru");
        user.setLogin("login");
        LocalDate parseDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        user.setBirthday(parseDate);
        ValidationException ex = getValidationEx(user);
        assertEquals("День рождения не может быть в будущем", ex.getMessage());
    }

    @ParameterizedTest(name = "{index}. Замена невалидного имени \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    void validateBlankName(String name) {
        User user = new User();
        user.setEmail("email@yandex.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1999,12,12));
        user.setName(name);
        validator.validate(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void validateNullName() {
        User user = new User();
        user.setEmail("email@yandex.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1999,12,12));
        validator.validate(user);
        assertEquals(user.getName(), user.getLogin());
    }

    private ValidationException getValidationEx(User user) {
        return assertThrows(ValidationException.class,
                () -> validator.validate(user));
    }

}
