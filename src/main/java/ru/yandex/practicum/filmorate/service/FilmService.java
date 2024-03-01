package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundLikeException;
import ru.yandex.practicum.filmorate.exceptions.UserIdIntersectionException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private int id = 0;
    private final FilmStorage filmStorage;

    public Collection<Film> findAllFilms() {
        return filmStorage.values();
    }

    public Film createFilm(Film film) {
        film.setId(++id);
        filmStorage.add(film);
        log.info("Фильм {} успешно создан", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilmById(film.getId()) != null) {
            filmStorage.add(film);
            log.info("Фильм с id = {} успешно обновлен", film.getId());
        } else {
            throw new NotFoundFilmException(String.format("Филма с id = %d не существует.", film.getId()));
        }
        return film;
    }

    public Film findFilm(int filmId) {
        Film film = filmStorage.getFilmById(filmId);

        if (film != null) {
            return film;
        } else {
            throw new NotFoundFilmException(String.format("Филма с id = %d не существует.", filmId));
        }
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.values().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addLike(int filmId, int userId) {
        Film film = findFilm(filmId);

        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
            log.info("Пользователь с id = {} поставил лайк фильму {}.", userId, film.getName());
            return film;
        } else {
            throw new UserIdIntersectionException(String.format("Лайк пользователя с id = %d уже стоит.", userId));
        }
    }

    public Film removeLike(int filmId, int userId) {
        Film film = findFilm(filmId);

        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
            log.info("Лайк пользователя с id = {} удален.", userId);
            return film;
        } else {
            throw new NotFoundLikeException(String.format("Пользователь с id = %d не ставил лайк фильму %s.", userId, film.getName()));
        }
    }

    private int compare(Film f1, Film f2) {
        return Integer.compare(f2.getLikes().size(), f1.getLikes().size());
    }
}