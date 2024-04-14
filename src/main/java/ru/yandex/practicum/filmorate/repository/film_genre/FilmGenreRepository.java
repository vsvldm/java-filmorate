package ru.yandex.practicum.filmorate.repository.film_genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreRepository {
    public void add(int filmId, Set<Genre> genres);

    public boolean remove(int filmId);

}