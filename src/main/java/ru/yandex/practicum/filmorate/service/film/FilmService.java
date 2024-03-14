package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    public List<Film> findAllFilms();

    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public Film findFilm(int filmId);

    public List<Film> findPopularFilms(int count);

    public Film addLike(int filmId, int userId);

    public Film removeLike(int filmId, int userId);
}