package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    public User create(User user);

    public User update(User user);

    public User findById(int userId);

    public List<User> findAll();

    public User addToFriends(int userId, int friendId);

    public User removeFromFriends(int userId, int friendId);

    public List<User> findAllFriendsByUser(int userId);

    public List<User> findAllCommonFriends(int userId, int otherId);
}