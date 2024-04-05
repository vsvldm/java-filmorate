package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User add(User user);

    public boolean update(User user);

    public void remove(int userId);

    public User getById(int userId);

    public Collection<User> values();
}