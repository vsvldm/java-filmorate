package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
    public void remove(int filmId) {
        if (films.remove(filmId) == null) {
            throw new NotFoundException(String.format("Филма с id = %d не существует.", filmId));
        }
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
    public Collection<Film> getPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f1, Film f2) {
        return Integer.compare(likeStorage.getUserLikesByFilm(f2.getId()).size(), likeStorage.getUserLikesByFilm(f1.getId()).size());
    }

    @Override
    public List<Film> getFilmsByUser(int userId) {
        return null; //inmemory больше не поддерживаем, поэтому просто заглушка
    }

    @Override
    public HashSet<Genre> getGenresByFilm(int id){
        return null; //inmemory больше не поддерживаем, поэтому просто заглушка
    }
}
