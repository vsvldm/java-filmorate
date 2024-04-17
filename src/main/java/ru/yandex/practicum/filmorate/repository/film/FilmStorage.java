package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    public int add(Film film);

    public boolean update(Film film);

    public boolean deleteById(int filmID);

    public boolean deleteAll();

    public Film getById(int filmId);

    public Collection<Film> getAllFilms();

    public Collection<Film> getPopularFilms(int count);

    public List<Film> findFilmsByDirectorSortByYear(int directorId);

    public List<Film> getRecommendations(int id);

    public List<Film> findFilmsByDirectorSortByLikes(int directorId);

    public List<Film> searchFilmForDirector(String query);

    public List<Film> searchFilmForTitle(String query);

    public List<Film> searchFilmForTitleAndDirector(String query);

    public List<Film> findCommonFilms(int userId, int friendId);

    public Collection<Film> getPopularFilmsByYearAndGenres(int count, Integer genreId, Integer year);
}