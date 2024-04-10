package ru.yandex.practicum.filmorate.repository.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorRepository {
    public Director create(Director director);

    public Director update(Director director);

    public Optional<Director> findById(int id);

    public List<Director> findAll();

    public void remove(int id);

    public void removeDirectorsFromFilms(int filmId);

    public void addDirectorsToFilm(Set<Director> directors, int filmId);

    public List<Director> findDirectorsByFilm(int filmId);

}
