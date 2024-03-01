package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void add(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void remove(int filmId) {
        films.remove(filmId);
    }

    @Override
    public Film getFilmById(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Collection<Film> values() {
        return films.values();
    }
}