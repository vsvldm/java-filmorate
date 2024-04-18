package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.feed.FeedRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.film_genre.FilmGenreRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final FilmGenreRepository filmGenreRepository;
    private final DirectorRepository directorRepository;
    private final FeedRepository feedRepository;
    private final String sortByYear = "year";
    private final String sortByLikes = "likes";

    @Override
    public Film create(Film film) {
        log.info("Начало выполнения метода create.");
        Set<Director> directors = film.getDirectors();

        int filmId = filmStorage.add(film);

        film.setId(filmId);
        filmGenreRepository.add(filmId, film.getGenres());

        film.setGenres(film.getGenres() != null
                ? new LinkedHashSet<>(filmGenreRepository.getByFilm(filmId))
                : new LinkedHashSet<>());

        if (directors != null) {
            if (!directors.isEmpty()) {
                directorRepository.addDirectorsToFilm(directors, film.getId());
            }
        }

        log.info("Фильм id = {} успешно создан", filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Начало выполнения метода update.");
        Set<Director> directors = film.getDirectors();

        log.info("Проверка существования фильма с id ={}.", film.getId());
        if (filmStorage.update(film)) {
            filmGenreRepository.remove(film.getId());
            filmGenreRepository.add(film.getId(), film.getGenres());

            Film filmFromDB = filmStorage.getById(film.getId());

            filmFromDB.getGenres().addAll(filmGenreRepository.getByFilm(film.getId()));

            directorRepository.removeDirectorsFromFilms(filmFromDB.getId());
            if (directors != null && !directors.isEmpty()) {
                    directorRepository.addDirectorsToFilm(directors, filmFromDB.getId());
            }
            filmFromDB.setDirectors(directors);

            log.info("Фильм с id = {} успешно обновлен", film.getId());
            return filmFromDB;
        } else {
            throw new NotFoundException(String.format("Фильма с id = %d не существует.", film.getId()));
        }
    }

    @Override
    public Film findById(int filmId) {
        log.info("Начало выполнения метода findById.");
        Set<Director> directors = new HashSet<>(directorRepository.findDirectorsByFilm(filmId));

        Film film = filmStorage.getById(filmId);

        film.getGenres().addAll(filmGenreRepository.getByFilm(filmId));

        if (!directors.isEmpty()) {
            film.setDirectors(directors);
        }
        log.info("Фильм с id = {} найден.", filmId);
        return film;
    }

    @Override
    public List<Film> findAll() {
        log.info("Начало выполнения метода findAll.");
        List<Film> films = filmStorage.getAllFilms()
                .stream()
                .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                .peek(f -> f.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId()))))
                .collect(Collectors.toList());

        log.info("Список всех фильмом найден.");
        return films;
    }

    @Override
    public void deleteById(int filmID) {
        log.info("Начало выполнения метода deleteById.");
        filmStorage.deleteById(filmID);

        log.info("Фильм с id = {} удалён.", filmID);
    }

    @Override
    public void deleteAll() {
        log.info("Начало выполнения метода deleteAll.");
        filmStorage.deleteAll();

        log.info("Все фильмы удалены.");
    }

    @Override
    public List<Film> findPopular(int count, Integer genreId, Integer year) {
        log.info("Начало выполнения метода findPopular.");
        List<Film> films = new ArrayList<>((genreId != null || year != null)
                ? filmStorage.getPopularFilmsByYearAndGenres(count, genreId, year)
                : filmStorage.getPopularFilms(count));

        films.forEach(f -> {
            f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId()));
            f.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId())));
        });
        log.info("Список из count = {} самых популярных фильмов найден.", count);
        return films;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        log.info("Начало выполнения метода addLike.");
        log.info("Проверка существования фильма с id = {} и  пользователя с id = {}.", filmId, userId);
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);

        likeStorage.add(film.getId(), user.getId());
        log.info("Пользователь с id = {} поставил лайк фильму c id = {}.", userId, film.getId());
        feedRepository.addFeed("LIKE", "ADD", userId, filmId);
        return film;

    }

    @Override
    public Film removeLike(int filmId, int userId) {
        log.info("Начало выполнения метода removeLike.");
        log.info("Проверка существования фильма с id = {} и пользователя с id = {}.", filmId, userId);
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);

        if (likeStorage.remove(film.getId(), user.getId())) {
            log.info("Лайк пользователя с id = {} удален.", user.getId());

        } else {
            log.info("Пользователь с id = {} не ставил лайк фильму c id = {}.", user.getId(), film.getId());
        }
        feedRepository.addFeed("LIKE", "REMOVE", userId, filmId);
        return film;
    }

    @Override
    public List<Film> findByDirector(int directorId, String sortBy) {
        List<Film> films;

        directorRepository.findById(directorId).orElseThrow(() -> new NotFoundException(
                String.format("Режиссер с ID = %d не найден ", directorId)));
        if (sortByYear.equals(sortBy)) {
            films = filmStorage.findFilmsByDirectorSortByYear(directorId).stream()
                    .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                    .peek(f -> f.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId()))))
                    .collect(Collectors.toList());
            return films;
        }
        if (sortByLikes.equals(sortBy)) {
            films = filmStorage.findFilmsByDirectorSortByLikes(directorId).stream()
                    .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                    .peek(f -> f.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId()))))
                    .collect(Collectors.toList());
            return films;
        }
        throw new BadRequestException("Неверный параметр сортировки");
    }

    @Override
    public List<Film> searchFilms(String query, String by) {

        log.info("Начало выполнения метода searchFilms.");

        if (query == null && by == null) {
            return filmStorage.getPopularFilms(10).stream()
                    .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                    .peek(f -> f.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId()))))
                    .collect(Collectors.toList());
        }

        if (query == null || query.isBlank()) {
            log.warn("Пустой запрос");
            return Collections.emptyList();
        }

        if (!("director".equals(by) || "title".equals(by) || "director,title".equals(by) ||
                "title,director".equals(by))) {
            throw new BadRequestException("Недопустимое значение параметра сортировки 'by': " + by);
        }

        List<Film> result = new ArrayList<>();

        switch (by) {
            case "director":
                result = filmStorage.searchFilmForDirector(query).stream()
                        .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                        .peek(f -> f.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId()))))
                        .collect(Collectors.toList());

                log.debug("Получены все фильмы по имени режиссёра {}", query);
                break;
            case "title":
                result = filmStorage.searchFilmForTitle(query).stream()
                        .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                        .peek(f -> f.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId()))))
                        .collect(Collectors.toList());

                log.debug("Получены все фильмы по названию {}", query);
                break;
            case "director,title":
            case "title,director":
                result = filmStorage.searchFilmForTitleAndDirector(query).stream()
                        .peek(f -> f.getGenres().addAll(filmGenreRepository.getByFilm(f.getId())))
                        .peek(f -> f.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(f.getId()))))
                        .collect(Collectors.toList());

                log.debug("Получены все фильмы по названию и режиссёру");
                break;
        }

        return result;
    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);

        return filmStorage.findCommonFilms(userId, friendId);
    }
}