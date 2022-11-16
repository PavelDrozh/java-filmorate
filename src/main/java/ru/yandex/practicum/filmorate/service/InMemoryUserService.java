package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InMemoryUserService implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public InMemoryUserService (UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    @Override
    public User addFriend(int initiatorId, int addedId) {
        User initiator = getUser(initiatorId);
        User added = getUser(addedId);
        updateAddedFriends(initiator, added);
        updateAddedFriends(added, initiator);

        return initiator;
    }

    private static void updateAddedFriends(User initiator, User added) {
        Set<Integer> newFriendsSet = initiator.getFriends();
        newFriendsSet.add(added.getId());
        initiator.setFriends(newFriendsSet);
    }

    private User getUser(int id) {
        @NotNull(message = "Пользователей не зарегистрирован")
        User user = userStorage.getById(id);
        return user;
    }

    @Override
    public User removeFriend(int initiatorId, int removedId) {
        User initiator = getUser(initiatorId);
        User removed = getUser(removedId);
        updateRemovedFriends(initiator, removed);
        updateRemovedFriends(removed, initiator);

        return initiator;
    }

    private static void updateRemovedFriends(User initiator, User removed) {
        Set<Integer> newFriendsSet = initiator.getFriends();
        newFriendsSet.remove(removed.getId());
        initiator.setFriends(newFriendsSet);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = getUser(userId);
        return user.getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherUserId) {
        User user = getUser(userId);
        User otherUser = getUser(otherUserId);
        return user.getFriends().stream()
                .filter(friend -> otherUser.getFriends().contains(friend))
                .map(this::getUser)
                .collect(Collectors.toList());
    }
}
