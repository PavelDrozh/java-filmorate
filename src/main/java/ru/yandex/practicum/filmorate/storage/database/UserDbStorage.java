package ru.yandex.practicum.filmorate.storage.database;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceprions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceprions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    JdbcTemplate jdbcTemplate;
    UserValidator validator;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserValidator validator) {
        this.jdbcTemplate = jdbcTemplate;
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
        String insertUser = "insert into users_storage(email, name, login, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(insertUser, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Создание пользователя успешно, пользователь: {}", user);
    }

    @Override
    public User updateOrCreate(User user) {
        validator.validate(user);
        if (user.getId() == 0) {
            createUser(user);
        } else if (getById(user.getId()) != null) {
            String updateUser = "update users_storage set " +
                    "email = ?, name = ?, login = ?, birthday = ? " +
                    "WHERE id = ?";
            jdbcTemplate.update(updateUser
                    , user.getEmail()
                    , user.getName()
                    , user.getLogin()
                    , Date.valueOf(user.getBirthday())
                    , user.getId());
            log.info("Обновление пользователя успешно, пользователь: {}", user);
        } else {
            throw new UserNotFoundException("Обновить пользователя не удалось");
        }
        return user;
    }

    private User mapRowToUser(ResultSet rs, int i) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public List<User> getAll() {
        String findAllQuery = "SELECT id, email, name, login, birthday " +
                "FROM users_storage";
        return jdbcTemplate.query(findAllQuery, this::mapRowToUser);
    }

    @Override
    public User getById(int id) {
        String findUserQuery = "SELECT id, email, name, login, birthday " +
                "FROM users_storage WHERE id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(findUserQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Пользователь с id = {} не найден.", id);
            throw new  UserNotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        log.info("Найден пользователь: {}", user);
        return user;
    }
}
