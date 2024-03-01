package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private int id = 0;
    private final UserStorage userStorage;

    public User createUser(User user) {
        if (user.getName().isBlank()) {
            log.info("Отсутствует имя пользоваителя. Будет использован login = {} в качестве имени пользователя.", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(++id);
        userStorage.add(user);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
    }

    public User updateUser(User user) {
        if (userStorage.getUserById(user.getId()) != null) {
            userStorage.add(user);
            log.info("Пользовател с id = {} успешно обновлен.", user.getId());
        } else {
            throw new NotFoundUserException(String.format("Пользователя с id = %d не существует.", user.getId()));
        }
        return user;
    }

    public User findUser(int userId) {
        User user = userStorage.getUserById(userId);

        if (user != null) {
            return user;
        } else {
            throw new NotFoundUserException(String.format("Пользователя с id = %d не существует.", userId));
        }
    }

    public User addToFriends(int userId, int friendId) {
        User user = findUser(userId);
        User friend = findUser(friendId);

        user.getFriends().add(friendId);
        log.info("Пользователь login = {} добавил в друзья login = {}.", user.getLogin(), friend.getLogin());
        friend.getFriends().add(userId);
        return user;
    }

    public User removeFromFriends(int userId, int friendId) {
        User user = findUser(userId);
        User friend = findUser(friendId);

        if (user.getFriends().contains(friend.getId())) {
            user.getFriends().remove(friend.getId());
            log.info("Пользователь login = {} удалил из друзей login = {}.", user.getLogin(), friend.getLogin());
        } else {
            throw new NotFoundUserException(String.format("В друзьях нет пользователя с id = %d", friendId));
        }

        return user;
    }

    public Set<User> findAllFriendsByUser(int userId) {
        Set<User> friends = new TreeSet<>(Comparator.comparing(User::getId));
        User user = findUser(userId);

        for (Integer friend : user.getFriends()) {
            friends.add(userStorage.getUserById(friend));
        }

        return friends;
    }

    public Set<User> findAllCommonFriends(int userId, int otherId) {
        Set<User> commonFriends = new HashSet<>();
        Set<Integer> userFriends = new HashSet<>(findUser(userId).getFriends());
        Set<Integer> otherFriends = new HashSet<>(findUser(otherId).getFriends());

        userFriends.retainAll(otherFriends);

        for (Integer friend : userFriends) {
            commonFriends.add(userStorage.getUserById(friend));
        }
        if (commonFriends.isEmpty()) {
            log.info("У пользователей id = {} и id = {} отсутсвуют общие друзья.", userId, otherId);
        }

        return commonFriends;
    }

    public Collection<User> findAllUsers() {
        return userStorage.values();
    }
}