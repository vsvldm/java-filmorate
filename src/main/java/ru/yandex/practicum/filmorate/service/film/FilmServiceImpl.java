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
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.film_genre.FilmGenreRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Override
    public Film create(Film film) {
        Set<Director> directors = film.getDirectors();

        log.info("Начало выполнения метода create.");
        int filmId = filmStorage.add(film);
        film.setId(filmId);

        filmGenreRepository.add(filmId,film.getGenres());
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
        Set<Director> directors = film.getDirectors();

        log.info("Начало выполнения метода update.");
        log.info("Проверка существования фильма с id ={}.", film.getId());
        if (filmStorage.update(film)) {
            filmGenreRepository.remove(film.getId());
            filmGenreRepository.add(film.getId(), film.getGenres());

            Film filmFromDB = filmStorage.getById(film.getId());

            directorRepository.removeDirectorsFromFilms(filmFromDB.getId());
            if (directors != null) {
                if (!directors.isEmpty()) {
                    directorRepository.addDirectorsToFilm(directors, filmFromDB.getId());
                }
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
        Set<Director> directors = new HashSet<>(directorRepository.findDirectorsByFilm(filmId));

        log.info("Начало выполнения метода findById.");
        Film film = filmStorage.getById(filmId);
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
                .peek(film -> film.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(film.getId()))))
                .collect(Collectors.toList());

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

    @Override
    public List<Film> findByDirector(int directorId, String sortBy) {
        directorRepository.findById(directorId).orElseThrow(() -> new NotFoundException(
                String.format("Режиссер с ID = %d не найден ", directorId)));
        if ("year".equals(sortBy)) {
            return filmStorage.findFilmsByDirectorSortByYear(directorId);
        }
        if ("likes".equals(sortBy)) {
            return filmStorage.findFilmsByDirectorSortByLikes(directorId);
        }
        throw new BadRequestException("Неверный параметр сортировки");
    }


}