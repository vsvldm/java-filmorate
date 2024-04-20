package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(User user);

    User findById(int userId);

    List<User> findAll();

    void deleteById(int userID);

    void deleteAll();

    User addToFriends(int userId, int friendId);

    User removeFromFriends(int userId, int friendId);

    List<User> findAllFriendsByUser(int userId);

    List<User> findAllCommonFriends(int userId, int otherId);

    List<Feed> getFeed(int userId);

    List<Film> getRecommendations(int userId);
}