package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.film_genre.FilmGenreRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final FilmGenreRepository filmGenreRepository;
    private final MpaService mpaDao;

    @Override
    public Film create(Film film) {
        log.info("Начало выполнения метода create.");
        int filmId = filmStorage.add(film);

        filmGenreRepository.add(filmId, film.getGenres());
        film.setId(filmId);
        log.info("Фильм id = {} успешно создан", filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Начало выполнения метода update.");
        log.info("Проверка существования фильма с id ={}.", film.getId());
        if (filmStorage.update(film)) {
            filmGenreRepository.remove(film.getId());
            filmGenreRepository.add(film.getId(), film.getGenres());

            Film filmFromDB = filmStorage.getById(film.getId());

            log.info("Фильм с id = {} успешно обновлен", film.getId());
            return filmFromDB;
        } else {
            throw new NotFoundException(String.format("Фильма с id = %d не существует.", film.getId()));
        }
    }

    @Override
    public Film findById(int filmId) {
        log.info("Начало выполнения метода findById.");
        Film film = filmStorage.getById(filmId);
        log.info("Фильм с id = {} найден.", filmId);
        return film;
    }

    @Override
    public List<Film> findAll() {
        log.info("Начало выполнения метода findAll.");
        List<Film> films = new ArrayList<>(filmStorage.getAllFilms());

        log.info("Список всех фильмом найден.");
        return films;
    }

    @Override
    public List<Film> findPopular(int count) {
        log.info("Начало выполнения метода findPopular.");
        List<Film> films = new ArrayList<>(filmStorage.getPopularFilms(count));

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
        return film;
    }

    public Collection<Film> getFilmsByUser(int id) {
        Collection<Film> films = filmStorage.getFilmsByUser(id);
        for (Film film : films) {
            film.setGenres((Set<Genre>) filmGenreRepository.genreByFilm(film.getId()));
            film.setMpa(mpaDao.findById(film.getMpa().getId()));
        }
        return films;
    }
}