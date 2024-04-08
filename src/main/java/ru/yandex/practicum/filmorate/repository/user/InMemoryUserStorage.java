package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    public int add(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public boolean update(User user) {
        if (users.get(user.getId()) != null) {
            users.put(user.getId(), user);
            return true;
        } else {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", user.getId()));
        }
    }

//    @Override
//    public void remove(int userId) {
//        if (users.remove(userId) == null) {
//            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", userId));
//        }
//    }


    @Override
    public boolean deleteById(Integer userID) {
        return false;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }

    @Override
    public User getById(int userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
