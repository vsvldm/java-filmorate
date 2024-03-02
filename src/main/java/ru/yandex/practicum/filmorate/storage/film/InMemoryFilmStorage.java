package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void add(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        if (films.get(film.getId()) != null) {
            films.put(film.getId(), film);
        } else {
            throw new NotFoundFilmException(String.format("Филма с id = %d не существует.", film.getId()));
        }
    }

    @Override
    public void remove(int filmId) {
        if (films.remove(filmId) == null) {
            throw new NotFoundFilmException(String.format("Филма с id = %d не существует.", filmId));
        }
    }

    @Override
    public Film getFilmById(int filmId) {
        Film film = films.get(filmId);
        if (film != null) {
            return film;
        } else {
            throw new NotFoundFilmException(String.format("Филма с id = %d не существует.", filmId));
        }
    }

    @Override
    public List<Film> values() {
        return new ArrayList<>(films.values());
    }
}