package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
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

    public static final String EMAIL = "email@yandex.ru";
    public static final String LOGIN = "login";
    public static final String NAME = "name";
    private static final LocalDate NORMAL_BIRTHDAY = LocalDate.of(1999,12,12);
    private final Validator<User> validator = new UserValidator();

    private User user;

    @BeforeEach
    void setUser() {
        user = User.builder()
                .login(LOGIN)
                .name(NAME)
                .email(EMAIL)
                .birthday(NORMAL_BIRTHDAY)
                .build();
    }

    @Test
    void validateNull() {
        validateUser(null, "Отсутствует тело запроса");
    }

    @Test
    void validateNullEmail() {
        validateEmail(null, "Отсутствует почта");
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности почты \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    void validateBlankEmail(String email) {
        validateEmail(email, "Отсутствует почта");
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности почты (отсутствует @) \"{arguments}\"")
    @ValueSource(strings = {"asd.asd.com", "asdyasdnkasd", "email.yandex.ru", "yandex.ru"})
    void validateEmailWithoutA(String email) {
        validateEmail(email, "Отсутствует @");
    }

    private void validateEmail(String email, String expectedMessage) {
        user.setEmail(email);
        validateUser(user, expectedMessage);
    }

    @Test
    void validateNullLogin() {
        validateLogin(null, "Логин не должен быть пустым");
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности логина \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    void validateBlankLogin(String login) {
        validateLogin(login, "Логин не должен быть пустым");
    }

    @ParameterizedTest(name = "{index}. Проверка невалидности логина \"{arguments}\"")
    @ValueSource(strings = {"asd asd", "asd   asd asd", "asd asdasd  ", " asd asd  asd ", " asd  asd  "})
    void validateLoginWithBlanks(String login) {
        validateLogin(login, "Логин не должен содержать пробелов");
    }

    private void validateLogin(String login, String expectedMessage) {
        user.setLogin(login);
        validateUser(user, expectedMessage);
    }

    //Если тест не проходит - проверить первую дату, т.к. это проверка граничного уловия от LocalDate.now().
    @ParameterizedTest(name = "{index}. Проверка невалидности даты рождения \"{arguments}\"")
    @ValueSource(strings = {"2023-11-18", "2023-01-01", "2089-12-27"})
    void validateReleaseDate(String date) {
        LocalDate parseDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        user.setBirthday(parseDate);
        validateUser(user, "День рождения не может быть в будущем");
    }

    @ParameterizedTest(name = "{index}. Замена невалидного имени \"{arguments}\"")
    @ValueSource(strings = {"", " ", "  ", "   ", "    ", "     "})
    void validateBlankName(String name) {
        validateName(name);
    }

    @Test
    void validateNullName() {
        validateName(null);
    }

    private void validateUser(User user, String expectedMessage) {
        ValidationException ex = getValidationEx(user);
        assertEquals(expectedMessage, ex.getMessage());
    }

    private void validateName(String name) {
        user.setName(name);
        validator.validate(user);
        assertEquals(user.getName(), user.getLogin());
    }

    private ValidationException getValidationEx(User user) {
        return assertThrows(ValidationException.class,
                () -> validator.validate(user));
    }
}
