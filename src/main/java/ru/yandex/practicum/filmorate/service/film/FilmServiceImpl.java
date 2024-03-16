package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    public List<Film> findAllFilms() {
        return filmStorage.values();
    }

    public Film createFilm(Film film) {
        filmStorage.add(film);
        likeStorage.createStorage(film.getId());
        log.info("Фильм {} успешно создан", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.update(film);
        log.info("Фильм с id = {} успешно обновлен", film.getId());
        return film;
    }

    public Film findFilm(int filmId) {
        return filmStorage.getById(filmId);
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.values().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);

        likeStorage.add(filmId, userId);
        log.info("Пользователь с id = {} поставил лайк фильму {}.", userId, film.getName());
        return film;

    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);

        if (likeStorage.remove(filmId, userId)) {
            log.info("Лайк пользователя с id = {} удален.", userId);
        } else {
            log.info("Пользователь с id = {} не ставил лайк фильму {}.", userId, film.getName());
        }
        return film;
    }

    private int compare(Film f1, Film f2) {
        return Integer.compare(likeStorage.getByFilm(f2.getId()).size(), likeStorage.getByFilm(f1.getId()).size());
    }
}