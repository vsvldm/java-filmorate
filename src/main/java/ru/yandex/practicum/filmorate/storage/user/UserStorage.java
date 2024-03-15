package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public void add(User user);

    public void update(User user);

    public void remove(int userId);

    public User getById(int userId);

    public List<User> values();
}