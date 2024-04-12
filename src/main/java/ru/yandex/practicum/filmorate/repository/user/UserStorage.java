package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public int add(User user);

    public boolean update(User user);

    boolean deleteById(Integer userID);

    boolean deleteAll();

    public User getById(int userId);

    public Collection<User> getAllUsers();
}