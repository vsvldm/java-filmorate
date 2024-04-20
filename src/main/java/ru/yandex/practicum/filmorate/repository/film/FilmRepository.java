package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmRepository {
    int add(Film film);

    boolean update(Film film);

    boolean deleteById(int filmID);

    boolean deleteAll();

    Film getById(int filmId);

    Collection<Film> getAllFilms();

    Collection<Film> getPopularFilms(int count);

    List<Film> findFilmsByDirectorSortByYear(int directorId);

    List<Film> getRecommendations(int id);

    List<Film> findFilmsByDirectorSortByLikes(int directorId);

    List<Film> searchFilmForDirector(String query);

    List<Film> searchFilmForTitle(String query);

    List<Film> searchFilmForTitleAndDirector(String query);

    List<Film> findCommonFilms(int userId, int friendId);

    Collection<Film> getPopularFilmsByYearAndGenres(int count, Integer genreId, Integer year);
}