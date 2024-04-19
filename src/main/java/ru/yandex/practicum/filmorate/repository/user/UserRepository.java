package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserRepository {
    int add(User user);

    boolean update(User user);

    boolean deleteById(Integer userID);

    boolean deleteAll();

    User getById(int userId);

    Collection<User> getAllUsers();
}