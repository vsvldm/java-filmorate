package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.feed.FeedRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.film_genre.FilmGenreRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final DirectorRepository directorRepository;
    private final FeedRepository feedRepository;
    private static final String BY_DIRECTOR = "director";
    private static final String BY_TITLE = "title";
    private static final String BY_DIRECTOR_TITLE = "director,title";
    private static final String BY_TITLE_DIRECTOR = "title,director";
    private static final String BY_YEAR = "year";
    private static final String BY_LIKES = "likes";


    @Override
    public Film create(Film film) {
        log.info("FilmService: Начало выполнения метода create.");
        Set<Director> directors = film.getDirectors();

        int filmId = filmRepository.add(film);

        film.setId(filmId);
        filmGenreRepository.add(filmId, film.getGenres());

        if (!directors.isEmpty()) {
            directorRepository.addDirectorsToFilm(directors, film.getId());
        }

        log.info("FilmService: Фильм id = {} успешно создан.", filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("FilmService: Начало выполнения метода update.");
        Set<Director> directors = film.getDirectors();

        log.info("FilmService: Проверка существования фильма с id ={}.", film.getId());
        if (filmRepository.update(film)) {
            filmGenreRepository.remove(film.getId());
            filmGenreRepository.add(film.getId(), film.getGenres());

            Film filmFromDB = filmRepository.getById(film.getId());

            filmFromDB.getGenres().addAll(filmGenreRepository.getByFilm(film.getId()));
            directorRepository.removeDirectorsFromFilms(filmFromDB.getId());

            if (!directors.isEmpty()) {
                directorRepository.addDirectorsToFilm(directors, filmFromDB.getId());
                filmFromDB.getDirectors().addAll(directors);
            }

            log.info("FilmService: Фильм с id = {} успешно обновлен.", film.getId());
            return filmFromDB;
        } else {
            throw new NotFoundException(String.format("Фильма с id = %d не существует.", film.getId()));
        }
    }

    @Override
    public Film findById(int filmId) {
        log.info("FilmService: Начало выполнения метода findById.");
        Set<Director> directors = new HashSet<>(directorRepository.findDirectorsByFilm(filmId));
        Film film = filmRepository.getById(filmId);

        film.getGenres().addAll(filmGenreRepository.getByFilm(filmId));

        if (!directors.isEmpty()) {
            film.getDirectors().addAll(directors);
        }
        log.info("FilmService: Фильм с id = {} найден.", filmId);
        return film;
    }

    @Override
    public List<Film> findAll() {
        log.info("FilmService: Начало выполнения метода findAll.");
        List<Film> films = filmRepository.getAllFilms()
                .stream()
                .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                .peek(f -> f.getDirectors().addAll(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId()))))
                .collect(Collectors.toList());

        log.info("FilmService: Список всех фильмом найден.");
        return films;
    }

    @Override
    public void removeById(int filmID) {
        log.info("FilmService: Начало выполнения метода removeById.");
        filmRepository.deleteById(filmID);

        log.info("FilmService: Фильм с id = {} удалён.", filmID);
    }

    @Override
    public void removeAll() {
        log.info("FilmService: Начало выполнения метода removeAll.");
        filmRepository.deleteAll();

        log.info("FilmService: Все фильмы удалены.");
    }

    @Override
    public List<Film> findPopular(int count, Integer genreId, Integer year) {
        log.info("FilmService: Начало выполнения метода findPopular.");
        List<Film> films = new ArrayList<>((genreId != null || year != null)
                ? filmRepository.getPopularFilmsByYearAndGenres(count, genreId, year)
                : filmRepository.getPopularFilms(count));

        films.forEach(f -> {
            f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId()));
            f.getDirectors().addAll(directorRepository.findDirectorsByFilm(f.getId()));
        });
        log.info("FilmService: Список из count = {} самых популярных фильмов найден.", count);
        return films;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        log.info("FilmService: Начало выполнения метода addLike.");
        log.info("FilmService: Проверка существования фильма с id = {} и  пользователя с id = {}.", filmId, userId);
        Film film = filmRepository.getById(filmId);
        User user = userRepository.getById(userId);

        likeRepository.add(film.getId(), user.getId());
        log.info("FilmService: Пользователь с id = {} поставил лайк фильму c id = {}.", userId, film.getId());
        feedRepository.addFeed(Type.LIKE, Operation.ADD, userId, filmId);
        return film;

    }

    @Override
    public Film removeLike(int filmId, int userId) {
        log.info("FilmService: Начало выполнения метода removeLike.");
        log.info("FilmService: Проверка существования фильма с id = {} и пользователя с id = {}.", filmId, userId);
        Film film = filmRepository.getById(filmId);
        User user = userRepository.getById(userId);

        if (likeRepository.remove(film.getId(), user.getId())) {
            log.info("FilmService: Лайк пользователя с id = {} удален.", user.getId());

        } else {
            log.info("FilmService: Пользователь с id = {} не ставил лайк фильму c id = {}.", user.getId(), film.getId());
        }
        feedRepository.addFeed(Type.LIKE, Operation.REMOVE, userId, filmId);
        return film;
    }

    @Override
    public List<Film> findByDirector(int directorId, String sortBy) {
        log.info("FilmService: findByDirector: Проверка существования режиссера с directorId = {}.",
                directorId);
        directorRepository.findById(directorId);
        log.info("findByDirector: Ищем фильмы режиссера с directorId = {} sortBy = {}.", directorId, sortBy);
        if (BY_YEAR.equals(sortBy)) {
            return filmRepository.findFilmsByDirectorSortByYear(directorId).stream()
                    .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                    .peek(f -> f.getDirectors().addAll(directorRepository.findDirectorsByFilm(f.getId())))
                    .collect(Collectors.toList());
        }
        if (BY_LIKES.equals(sortBy)) {
            return filmRepository.findFilmsByDirectorSortByLikes(directorId).stream()
                    .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                    .peek(f -> f.getDirectors().addAll(directorRepository.findDirectorsByFilm(f.getId())))
                    .collect(Collectors.toList());
        }
        throw new BadRequestException("Неверный параметр сортировки.");
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        log.info("FilmService: Начало выполнения метода searchFilms.");

        if (query == null && by == null) {
            return filmRepository.getPopularFilms(10).stream()
                    .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                    .peek(f -> f.getDirectors().addAll(directorRepository.findDirectorsByFilm(f.getId())))
                    .collect(Collectors.toList());
        }

        if (query == null || query.isBlank()) {
            log.info("FilmService: Пустой запрос.");
            return Collections.emptyList();
        }

        if (!(BY_DIRECTOR.equals(by) || BY_TITLE.equals(by) || BY_DIRECTOR_TITLE.equals(by) ||
                BY_TITLE_DIRECTOR.equals(by))) {
            throw new BadRequestException("Недопустимое значение параметра сортировки 'by': " + by);
        }

        List<Film> result = new ArrayList<>();

        switch (by) {
            case BY_DIRECTOR:
                result = filmRepository.searchFilmForDirector(query).stream()
                        .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                        .peek(f -> f.getDirectors().addAll(directorRepository.findDirectorsByFilm(f.getId())))
                        .collect(Collectors.toList());

                log.info("FilmService: Получены все фильмы по имени режиссёра {}.", query);
                break;
            case BY_TITLE:
                result = filmRepository.searchFilmForTitle(query).stream()
                        .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                        .peek(f -> f.getDirectors().addAll(directorRepository.findDirectorsByFilm(f.getId())))
                        .collect(Collectors.toList());

                log.info("FilmService: Получены все фильмы по названию {}.", query);
                break;
            case BY_DIRECTOR_TITLE:
            case BY_TITLE_DIRECTOR:
                result = filmRepository.searchFilmForTitleAndDirector(query).stream()
                        .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                        .peek(f -> f.getDirectors().addAll(directorRepository.findDirectorsByFilm(f.getId())))
                        .collect(Collectors.toList());

                log.info("FilmService: Получены все фильмы по названию и режиссёру.");
                break;
        }

        return result;

    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        log.info("FilmService: Начало выполнения метода searchFilms.");
        log.info("FilmService:  Проверка существования пользователей  с userId = {} и friendId = {}.", userId, friendId);
        userRepository.getById(userId);
        userRepository.getById(friendId);
        log.info("FilmService:  Ищем общие фильмы пользователей  с userId = {} и friendId = {}.", userId, friendId);
        List<Film> films = filmRepository.findCommonFilms(userId, friendId);

        log.info("FilmService: Получены общие фильмы между пользователями с userId = {} и friendId = {}.", userId, friendId);
        return films;
    }
}
