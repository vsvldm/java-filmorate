package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer,User> users = new HashMap<>();

    @Override
    public void add(User user) {
         users.put(user.getId(), user);
    }

    @Override
    public void remove(int userId) {
        users.remove(userId);
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> values() {
        return users.values();
    }
}
