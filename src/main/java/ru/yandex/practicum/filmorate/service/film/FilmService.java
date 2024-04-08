package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film create(Film film);

    Film update(Film film);

    Film findById(int filmId);

    List<Film> findAll();

    List<Film> findPopular(int count);

    boolean deleteById(Integer filmID);

    boolean deleteAll();

    Film addLike(int filmId, int userId);

    Film removeLike(int filmId, int userId);
}