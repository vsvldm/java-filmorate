package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.feed.FeedDBRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.film_genre.FilmGenreRepository;
import ru.yandex.practicum.filmorate.repository.friend.FriendRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FeedDBRepository feedStorage;
    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;

    @Override
    public User create(User user) {
        log.info("UserService: Начало выполнения метода create.");
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("UserService: Отсутствует имя пользоваителя. Будет использован login = {} в качестве имени пользователя.", user.getLogin());
            user.setName(user.getLogin());
        }
        int userId = userRepository.add(user);
        user.setId(userId);
        log.info("UserService: Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
    }

    @Override
    public User update(User user) {
        log.info("UserService: Начало выполнения метода update.");
        log.info("UserService: Проверка существования пользователя с id ={}.", user.getId());
        if (userRepository.update(user)) {
            log.info("UserService: Пользовател с id = {} успешно обновлен.", user.getId());
        } else {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", user.getId()));
        }
        return user;
    }

    @Override
    public User findById(int userId) {
        log.info("UserService: Начало выполнения метода findById.");
        User user = userRepository.getById(userId);
        log.info("UserService: Пользователь c id = {} успешно найден.", userId);
        return user;
    }

    @Override
    public List<User> findAll() {
        log.info("UserService: Начало выполнения метода findAll.");
        List<User> users = new ArrayList<>(userRepository.getAllUsers());

        log.info("UserService: Список всех пользователей найден.");
        return users;
    }

    @Override
    public void deleteById(int userID) {
        log.info("UserService: Начало выполнения метода deleteById.");
        userRepository.deleteById(userID);

        log.info("UserService: Пользователь c id = {} удалён.", userID);
    }

    @Override
    public void deleteAll() {
        log.info("UserService: Начало выполнения метода deleteAll.");
        userRepository.deleteAll();

        log.info("UserService: Все пользователи удалены.");
    }

    @Override
    public User addToFriends(int userId, int friendId) {
        log.info("UserService: Начало выполнения метода addToFriends.");
        if (userId == friendId) {
            throw new BadRequestException("Параметры не могут быть равны.");
        }
        log.info("UserService: Проверка существования пользователей с id = {} и id = {}.", userId, friendId);
        User user = userRepository.getById(userId);
        User friend = userRepository.getById(friendId);

        friendRepository.add(user.getId(), friend.getId());
        log.info("UserService: Пользователь login = {} добавил в друзья login = {}.", user.getLogin(), friend.getLogin());

        log.info("UserService: Запись информации о событии в таблицу.");
        feedStorage.addFeed(Type.FRIEND, Operation.ADD, userId, friendId);
        log.info("UserService: Информация о событии успешно сохранена.");

        return user;
    }

    @Override
    public User removeFromFriends(int userId, int friendId) {
        log.info("UserService: Начало выполнения метода removeFromFriends.");
        log.info("UserService: Проверка существования пользователей с id = {} и id = {}.", userId, friendId);
        User user = userRepository.getById(userId);
        User friend = userRepository.getById(friendId);

        if (friendRepository.remove(userId, friendId)) {
            log.info("UserService: Пользователь login = {} удалил из друзей login = {}.", user.getLogin(), friend.getLogin());

        } else {
            log.info("UserService: Пользователя login = {} нет в друзьях у пользователя login = {}.", user.getLogin(), friend.getLogin());
        }
        log.info("UserService: Запись информации о событии в таблицу.");
        feedStorage.addFeed(Type.FRIEND, Operation.REMOVE, userId, friendId);
        log.info("UserService: Информация о событии успешно сохранена.");

        return user;
    }

    @Override
    public List<User> findAllFriendsByUser(int userId) {
        log.info("UserService: Начало выполнения метода findAllFriendsByUser.");
        List<User> friends = new ArrayList<>();

        log.info("UserService: Проверка существования пользователя с id = {}.", userId);
        User user = userRepository.getById(userId);
        for (Integer friendId : friendRepository.getFriendIdsByUser(user.getId())) {
            friends.add(userRepository.getById(friendId));
        }
        log.info("UserService: Список друзей пользователя с id = {} успешно получен.", userId);
        return friends;
    }

    @Override
    public List<User> findAllCommonFriends(int userId, int otherId) {
        log.info("UserService: Начало выполнения метода findAllCommonFriends.");
        List<User> commonFriends = new ArrayList<>();

        log.info("UserService: Проверка существования пользователей с id = {} и id = {}.", userId, otherId);
        userRepository.getById(userId);
        userRepository.getById(otherId);

        log.info("UserService: Начало поиска общих друзей среди пользователей с id = {} и id = {}.", userId, otherId);
        Set<Integer> usersCommonFriends = new HashSet<>(friendRepository.getCommonFriends(userId, otherId));

        for (Integer friendId : usersCommonFriends) {
            commonFriends.add(userRepository.getById(friendId));
        }
        if (commonFriends.isEmpty()) {
            log.info("UserService: У пользователей id = {} и id = {} отсутсвуют общие друзья.", userId, otherId);
        }
        log.info("UserService: Общие друзья у пользователей с id = {} и id = {} успешно найдены.", userId, otherId);
        return commonFriends;
    }

    public List<Film> getRecommendations(int userId) {
        log.info("UserService: Начало выполнения метода getRecommendations.");
        log.info("UserService: Проверка на существование.");
        userRepository.getById(userId);
        log.info("UserService: Рекомендации для пользователя с id {} успешно представлены.",userId);
       return filmRepository.getRecommendations(userId).stream()
               .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
               .collect(Collectors.toList());
    }

    @Override
    public List<Feed> getFeed(int userId) {
        log.info("UserService: Начало выполнения метода getFeed.");
        log.info("UserService: Проверка на существование.");
        userRepository.getById(userId);
        List<Feed> feed = feedStorage.getFeedById(userId);

        log.info("UserService: Лента событий успешно получена.");
        return feed;
    }
}