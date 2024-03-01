package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public void add(User user);

    public void remove(int userId);

    public User getUserById(int userId);

    public Collection<User> values();
}