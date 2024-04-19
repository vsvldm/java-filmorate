package ru.yandex.practicum.filmorate.repository.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorRepository {
    Director create(Director director);

    Director update(Director director);

    Director findById(int id);

    List<Director> findAll();

    void remove(int id);

    void removeDirectorsFromFilms(int filmId);

    void addDirectorsToFilm(Set<Director> directors, int filmId);

    List<Director> findDirectorsByFilm(int filmId);

}
