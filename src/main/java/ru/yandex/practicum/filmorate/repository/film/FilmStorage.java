package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public int add(Film film);

    public boolean update(Film film);

    public void remove(int filmId);

    public Film getById(int filmId);

    public Collection<Film> getAllFilms();

    public Collection<Film> getPopularFilms(int count);

    Collection<Film> getFilmsByUser(int id);
}