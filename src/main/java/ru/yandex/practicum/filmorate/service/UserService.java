package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User addFriend(int initiatorId, int addedId);
    User removeFriend(int initiatorId, int removedId);
    List<User> getFriends(int userId);
    List<User> getMutualFriends(int userId, int otherUserId);
}
