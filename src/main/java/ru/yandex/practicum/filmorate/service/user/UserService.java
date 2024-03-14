package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    public User createUser(User user);

    public User updateUser(User user);

    public User findUser(int userId);

    public User addToFriends(int userId, int friendId);

    public User removeFromFriends(int userId, int friendId);

    public List<User> findAllFriendsByUser(int userId);

    public List<User> findAllCommonFriends(int userId, int otherId);

    public List<User> findAllUsers();
}