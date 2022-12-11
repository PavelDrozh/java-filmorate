package ru.yandex.practicum.filmorate.service.database;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Qualifier("DbUserService")
public class DbUserService implements UserService {

    UserStorage userStorage;
    JdbcTemplate jdbcTemplate;

    public DbUserService(@Qualifier("UserDbStorage") UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addFriend(int initiatorId, int addedId) {
        User user = userStorage.getById(initiatorId);
        if (isAlreadyFriend(initiatorId, addedId).isEmpty() && userStorage.getById(addedId) != null) {
            String insertFriends = "insert into friends(first_user_id, second_user_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(insertFriends, initiatorId, addedId);
            log.info("Дружба между {} и {} добавлена", initiatorId, addedId);
        } else {
            log.info("Дружба между юзерами {} и {} уже существует", initiatorId, addedId);
        }
        user.setFriends(this.getFriendsId(initiatorId));
        return user;
    }

    private Optional<Integer> isAlreadyFriend(int initiatorId, int addedId) {
        String findFriendship = "SELECT second_user_id FROM friends " +
                "WHERE first_user_id = ? AND second_user_id = ?";
        Optional<Integer> friendId = Optional.empty();
        try {
            friendId = Optional.ofNullable(jdbcTemplate
                    .queryForObject(findFriendship, this::mapToUserId, initiatorId, addedId));
        } catch (EmptyResultDataAccessException e) {
            log.info("Дружбы между юзерами {} и {} еще нет", initiatorId, addedId);
        }
        return friendId;
    }

    private Integer mapToUserId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("second_user_id");
    }

    private Set<Integer> getFriendsId(int userId) {
        String findFriendship = "SELECT second_user_id FROM friends " +
                "WHERE first_user_id = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(findFriendship, this::mapToUserId, userId));
    }

    @Override
    public User removeFriend(int initiatorId, int removedId) {
        User user = null;
        if (userStorage.getById(removedId) != null && userStorage.getById(initiatorId) != null
                && isAlreadyFriend(initiatorId, removedId).isPresent()) {
            String deleteFriends = "DELETE FROM friends " +
                    "WHERE first_user_id = ? AND second_user_id = ?";
            jdbcTemplate.update(deleteFriends, initiatorId, removedId);
            user = userStorage.getById(initiatorId);
            log.info("Дружба между {} и {} удалена", initiatorId, removedId);
        }
        return user;
    }

    @Override
    public Set<User> getFriends(int userId) {
        String findFriendship = "SELECT second_user_id FROM friends " +
                "WHERE first_user_id = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(findFriendship, this::mapToUser, userId));
    }

    private User mapToUser(ResultSet rs, int i) throws SQLException {
        return userStorage.getById(rs.getInt("second_user_id"));
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherUserId) {
        String findFriendship = "SELECT second_user_id FROM friends " +
                "WHERE first_user_id IN (? , ?) " +
                "GROUP BY second_user_id " +
                "HAVING count(first_user_id) = 2";
        if (userStorage.getById(userId) != null && userStorage.getById(otherUserId) != null) {
            return jdbcTemplate.query(findFriendship, this::mapToUser, userId, otherUserId);
        }
        return List.of();
    }
}
