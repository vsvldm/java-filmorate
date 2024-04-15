package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.feed.FeedDBRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.friend.FriendStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FeedDBRepository feedStorage;
    private final FilmStorage filmStorage;

    @Override
    public User create(User user) {
        log.info("Начало выполнения метода create.");
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Отсутствует имя пользоваителя. Будет использован login = {} в качестве имени пользователя.", user.getLogin());
            user.setName(user.getLogin());
        }
        int userId = userStorage.add(user);
        user.setId(userId);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
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
        List<User> users = new ArrayList<>(userStorage.getAllUsers());

        log.info("Список всех пользователей найден.");
        return users;
    }

    @Override
    public void deleteById(int userID) {
        log.info("Начало выполнения метода deleteById.");
        userStorage.deleteById(userID);

        log.info("Пользователь c id = {} удалён.", userID);
    }

    @Override
    public void deleteAll() {
        log.info("Начало выполнения метода deleteAll.");
        userStorage.deleteAll();

        log.info("Все пользователи удалены");
    }

    @Override
    public User addToFriends(int userId, int friendId) {
        log.info("Начало выполнения метода addToFriends.");
        if (userId == friendId) {
            throw new BadRequestException("Параметры не могут быть равны.");
        }
        log.info("Проверка существования пользователей с id = {} и id = {}.", userId, friendId);
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        friendStorage.add(user.getId(), friend.getId());
        log.info("Пользователь login = {} добавил в друзья login = {}.", user.getLogin(), friend.getLogin());

        log.info("Запись информации о событии в таблицу");
        feedStorage.addFeed("FRIEND", "ADD", userId, friendId);
        log.info("Информация о событии успешно сохранена");

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

        log.info("Запись информации о событии в таблицу");
        feedStorage.addFeed("FRIEND", "REMOVE", userId, friendId);
        log.info("Информация о событии успешно сохранена");

        return user;
    }

    @Override
    public List<User> findAllFriendsByUser(int userId) {
        log.info("Начало выполнения метода findAllFriendsByUser.");
        List<User> friends = new ArrayList<>();

        log.info("Проверка существования пользователя с id = {}.", userId);
        User user = userStorage.getById(userId);
        for (Integer friendId : friendStorage.getFriendIdsByUser(user.getId())) {
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
        Set<Integer> usersCommonFriends = new HashSet<>(friendStorage.getCommonFriends(userId, otherId));

        for (Integer friendId : usersCommonFriends) {
            commonFriends.add(userStorage.getById(friendId));
        }
        if (commonFriends.isEmpty()) {
            log.info("У пользователей id = {} и id = {} отсутсвуют общие друзья.", userId, otherId);
        }
        log.info("Общие друзья у пользователей с id = {} и id = {} успешно найдены.", userId, otherId);
        return commonFriends;
    }

    public List<Film> getRecommendations(int userId) {
        log.info("Начало выполнения метода getRecommendations.");
        log.info("Проверка на существование");
        userStorage.getById(userId);
        log.info("Рекомендации для пользователя с id {} успешно представлены",userId);
       return filmStorage.getRecommendations(userId);

    }

    @Override
    public List<Feed> getFeed(int userId) {//Скорее всего потребуется перенести в другой сервис
        log.info("Начало выполнения метода getFeed.");
        List<User> friendList = findAllFriendsByUser(userId);
        List<Integer> friendIdList = new ArrayList<>();
        log.info("Список друзей - " + friendList);

        for (User friend : friendList) {
            friendIdList.add(friend.getId());
        }
        log.info("Что получаем - " +
                feedStorage.getFeedById(friendIdList.toString().substring(1, friendIdList.toString().length() - 1)));

        return feedStorage.getFeedById(friendIdList.toString().substring(1, friendIdList.toString().length() - 1));
    }
}