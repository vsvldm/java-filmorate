package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    public User create(User user);

    public User update(User user);

    public User findById(int userId);

    public List<User> findAll();

    public void deleteById(int userID);

    public void deleteAll();

    public User addToFriends(int userId, int friendId);

    public User removeFromFriends(int userId, int friendId);

    public List<User> findAllFriendsByUser(int userId);

    public List<User> findAllCommonFriends(int userId, int otherId);
    public List<Feed> getFeed(int userId);

    public List<Film> getRecommendations(int userId);
}