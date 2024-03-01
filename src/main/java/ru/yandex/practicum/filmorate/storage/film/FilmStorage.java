package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public void add(Film film);

    public void remove(int filmId);

    public Film getFilmById(int filmId);

    public Collection<Film> values();
}