package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashSet;

public interface GenreRepository {
    public Collection<Genre> values();

    public Genre getById(int genreId);
}