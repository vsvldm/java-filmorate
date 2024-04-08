package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    int add(Film film);

    boolean update(Film film);

    boolean deleteById(Integer filmID);

    boolean deleteAll();

    Film getById(int filmId);

    Collection<Film> getAllFilms();

    Collection<Film> getPopularFilms(int count);
}