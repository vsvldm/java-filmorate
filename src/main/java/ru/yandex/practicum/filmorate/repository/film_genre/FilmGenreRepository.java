package ru.yandex.practicum.filmorate.repository.film_genre;

import ru.yandex.practicum.filmorate.model.Genre;

import javax.validation.Valid;
import java.util.Set;

public interface FilmGenreRepository {
    public void add(int filmId, Set<Genre> genres);

    boolean remove(int filmId);

    public @Valid Set<Genre> genreByFilm(int filmId);
}