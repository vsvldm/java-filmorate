package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 0;
    private final Map<Integer,User> users = new HashMap<>();

    @Override
    public void add(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
    }

    @Override
    public boolean update(User user) {
        if (users.get(user.getId()) != null) {
            users.put(user.getId(), user);
            return true;
        } else {
            throw new NotFoundUserException(String.format("Пользователя с id = %d не существует.", user.getId()));
        }
    }

    @Override
    public void remove(int userId) {
        if (users.remove(userId) == null) {
            throw new NotFoundUserException(String.format("Пользователя с id = %d не существует.", userId));
        }
    }

    @Override
    public User getById(int userId) {
        return users.get(userId);
    }

    @Override
    public List<User> values() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getLast() {
        return users.get(users.size() - 1);
    }
}
