package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    public Film create(Film film);

    public Film update(Film film);

    public Film findById(int filmId);

    public List<Film> findAll();

    public List<Film> findPopular(int count,Integer genreId,Integer year);

    public void deleteById(int filmID);

    public void deleteAll();

    public Film addLike(int filmId, int userId);

    public Film removeLike(int filmId, int userId);

    public List<Film> findByDirector(int directorId, String sortBy);

    List<Film> searchFilms(String query, String by);
}