package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void update(User user) {
        if (users.get(user.getId()) != null) {
            users.put(user.getId(), user);
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
        User user = users.get(userId);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundUserException(String.format("Пользователя с id = %d не существует.", userId));
        }
    }

    @Override
    public List<User> values() {
        return new ArrayList<>(users.values());
    }
}
