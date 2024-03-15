package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Отсутствует имя пользоваителя. Будет использован login = {} в качестве имени пользователя.", user.getLogin());
            user.setName(user.getLogin());
        }
        userStorage.add(user);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
    }

    public User updateUser(User user) {
        userStorage.update(user);
        log.info("Пользовател с id = {} успешно обновлен.", user.getId());
        return user;
    }

    public User findUser(int userId) {
        return userStorage.getById(userId);
    }

    public User addToFriends(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = findUser(friendId);

        user.getFriendStorage().add(friendId);
        log.info("Пользователь login = {} добавил в друзья login = {}.", user.getLogin(), friend.getLogin());
        friend.getFriendStorage().add(userId);
        return user;
    }

    public User removeFromFriends(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (user.getFriendStorage().remove(friend.getId())) {
            log.info("Пользователь login = {} удалил из друзей login = {}.", user.getLogin(), friend.getLogin());
        } else {
            log.info("Пользователя login = {} нет в друзьях у пользователя login = {}.", user.getLogin(), friend.getLogin());
        }
        return user;
    }

    public List<User> findAllFriendsByUser(int userId) {
        List<User> friends = new ArrayList<>();
        User user = userStorage.getById(userId);

        for (Integer friend : user.getFriendStorage().values()) {
            friends.add(userStorage.getById(friend));
        }
        return friends;
    }

    public List<User> findAllCommonFriends(int userId, int otherId) {
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> userFriends = new HashSet<>(findUser(userId).getFriendStorage().values());
        Set<Integer> otherFriends = new HashSet<>(findUser(otherId).getFriendStorage().values());

        userFriends.retainAll(otherFriends);

        for (Integer friend : userFriends) {
            commonFriends.add(userStorage.getById(friend));
        }
        if (commonFriends.isEmpty()) {
            log.info("У пользователей id = {} и id = {} отсутсвуют общие друзья.", userId, otherId);
        }
        return commonFriends;
    }

    public List<User> findAllUsers() {
        return userStorage.values();
    }
}