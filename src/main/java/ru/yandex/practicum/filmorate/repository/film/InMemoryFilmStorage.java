package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        film.setId(++id);
        return films.put(film.getId(), film);
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
    public Collection<Film> values() {
        return new ArrayList<>(films.values());
    }
}
