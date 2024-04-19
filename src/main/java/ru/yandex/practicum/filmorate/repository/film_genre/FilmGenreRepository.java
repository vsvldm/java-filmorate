package ru.yandex.practicum.filmorate.repository.film_genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface FilmGenreRepository {
    void add(int filmId, Set<Genre> genres);

    boolean remove(int filmId);

    Collection<Genre> getByFilm(int filmId);
}