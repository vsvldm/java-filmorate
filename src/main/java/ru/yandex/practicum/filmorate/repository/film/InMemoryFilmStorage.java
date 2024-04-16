package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.like.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 0;
    private final LikeStorage likeStorage = new InMemoryLikeStorage();
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public int add(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return film.getId();
    }

    @Override
    public boolean update(Film film) {
        if (films.get(film.getId()) != null) {
            films.put(film.getId(), film);
        } else {
            throw new NotFoundException(String.format("Филма с id = %d не существует.", film.getId()));
        }
        return false;
    }

    @Override
    public boolean deleteById(int filmID) {
        return false;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }

    @Override
    public Film getById(int filmId) {
        Film film = films.get(filmId);
        if (film != null) {
            return film;
        } else {
            throw new NotFoundException(String.format("Филма с id = %d не существует.", filmId));
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Collection<Film> getPopularFilms(int count,Integer genreId,Integer year) {
        return getAllFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> findFilmsByDirectorSortByYear(int directorId) {
        return null;
    }

    @Override
    public List<Film> findFilmsByDirectorSortByLikes(int directorId) {
        return null;
    }

    @Override
    public List<Film> searchFilmForDirector(String query) {
        return null;
    }

    @Override
    public List<Film> searchFilmForTitle(String query) {
        return null;
    }

    @Override
    public List<Film> searchFilmForTitleAndDirector(String query) {
        return null;
    }

    private int compare(Film f1, Film f2) {
        return Integer.compare(likeStorage.getUserLikesByFilm(f2.getId()).size(), likeStorage.getUserLikesByFilm(f1.getId()).size());
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        return null;
    }
}
