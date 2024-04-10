package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    public Film create(Film film);

    public Film update(Film film);

    public Film findById(int filmId);

    public List<Film> findAll();

    public List<Film> findPopular(int count);

    public Film addLike(int filmId, int userId);

    public Film removeLike(int filmId, int userId);

    public Collection<Film> getFilmsByUser(int id);
    public List<Film> findByDirector(int directorId, String sortBy);
}