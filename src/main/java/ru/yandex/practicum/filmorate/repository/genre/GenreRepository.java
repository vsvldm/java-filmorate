package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreRepository {
    public Collection<Genre> values();

    public Genre getById(int genreId);


}