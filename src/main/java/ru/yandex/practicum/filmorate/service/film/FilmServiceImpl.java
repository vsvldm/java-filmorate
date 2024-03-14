package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserIdIntersectionException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;

    public List<Film> findAllFilms() {
        return filmStorage.values();
    }

    public Film createFilm(Film film) {
        filmStorage.add(film);
        log.info("Фильм {} успешно создан", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.update(film);
        log.info("Фильм с id = {} успешно обновлен", film.getId());
        return film;
    }

    public Film findFilm(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.values().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
            log.info("Пользователь с id = {} поставил лайк фильму {}.", userId, film.getName());
            return film;
        } else {
            throw new UserIdIntersectionException(String.format("Лайк пользователя с id = %d уже стоит.", userId));
        }
    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (film.getLikes().remove(userId)) {
            log.info("Лайк пользователя с id = {} удален.", userId);
        } else {
            log.info("Пользователь с id = {} не ставил лайк фильму {}.", userId, film.getName());
        }
        return film;
    }

    private int compare(Film f1, Film f2) {
        return Integer.compare(f2.getLikes().size(), f1.getLikes().size());
    }
}