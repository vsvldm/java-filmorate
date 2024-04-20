package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film create(Film film);

    Film update(Film film);

    Film findById(int filmId);

    List<Film> findAll();

    List<Film> findPopular(int count, Integer genreId, Integer year);

    void removeById(int filmID);

    void removeAll();

    Film addLike(int filmId, int userId);

    Film removeLike(int filmId, int userId);

    List<Film> findByDirector(int directorId, String sortBy);

    List<Film> searchFilms(String query, String by);

    List<Film> findCommonFilms(int userId, int friendId);
}