package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.friend.FriendStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Override
    public User create(User user) {
        log.info("Начало выполнения метода create.");
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Отсутствует имя пользоваителя. Будет использован login = {} в качестве имени пользователя.", user.getLogin());
            user.setName(user.getLogin());
        }
        userStorage.add(user);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return userStorage.getLast();
    }

    @Override
    public User update(User user) {
        log.info("Начало выполнения метода update.");
        log.info("Проверка существования пользователя с id ={}.", user.getId());
        if (userStorage.update(user)) {
            log.info("Пользовател с id = {} успешно обновлен.", user.getId());
        } else {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", user.getId()));
        }
        return user;
    }

    @Override
    public User findById(int userId) {
        log.info("Начало выполнения метода findById.");
        User user = userStorage.getById(userId);
        log.info("Пользователь c id = {} успешно найден.", userId);
        return user;
    }

    @Override
    public List<User> findAll() {
        log.info("Начало выполнения метода findAll.");
        List<User> users = new ArrayList<>(userStorage.values());

        log.info("Список всех пользователей найден.");
        return users;
    }

    @Override
    public User addToFriends(int userId, int friendId) {
        log.info("Начало выполнения метода addToFriends.");
        log.info("Проверка существования пользователей с id = {} и id = {}.", userId, friendId);
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        friendStorage.add(user.getId(), friend.getId());
        log.info("Пользователь login = {} добавил в друзья login = {}.", user.getLogin(), friend.getLogin());
        return user;
    }

    @Override
    public User removeFromFriends(int userId, int friendId) {
        log.info("Начало выполнения метода removeFromFriends.");
        log.info("Проверка существования пользователей с id = {} и id = {}.", userId, friendId);
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (friendStorage.remove(userId, friendId)) {
            log.info("Пользователь login = {} удалил из друзей login = {}.", user.getLogin(), friend.getLogin());
        } else {
            log.info("Пользователя login = {} нет в друзьях у пользователя login = {}.", user.getLogin(), friend.getLogin());
        }
        return user;
    }

    @Override
    public List<User> findAllFriendsByUser(int userId) {
        log.info("Начало выполнения метода findAllFriendsByUser.");
        List<User> friends = new ArrayList<>();

        log.info("Проверка существования пользователя с id = {}.", userId);
        User user = userStorage.getById(userId);
        for (Integer friendId : friendStorage.valuesByUser(user.getId())) {
            friends.add(userStorage.getById(friendId));
        }
        log.info("Список друзей пользователя с id = {} успешно получен.", userId);
        return friends;
    }

    @Override
    public List<User> findAllCommonFriends(int userId, int otherId) {
        log.info("Начало выполнения метода findAllCommonFriends.");
        List<User> commonFriends = new ArrayList<>();

        log.info("Проверка существования пользователей с id = {} и id = {}.", userId, otherId);
        userStorage.getById(userId);
        userStorage.getById(otherId);

        log.info("Начало поиска общих друзей среди пользователей с id = {} и id = {}.", userId, otherId);
        Set<Integer> userFriends = new HashSet<>(friendStorage.valuesByUser(userId));

        userFriends.retainAll(friendStorage.valuesByUser(otherId));

        for (Integer friendId : userFriends) {
            commonFriends.add(userStorage.getById(friendId));
        }
        if (commonFriends.isEmpty()) {
            log.info("У пользователей id = {} и id = {} отсутсвуют общие друзья.", userId, otherId);
        }
        log.info("Общие друзья у пользователей с id = {} и id = {} успешно найдены.", userId, otherId);
        return commonFriends;
    }
}