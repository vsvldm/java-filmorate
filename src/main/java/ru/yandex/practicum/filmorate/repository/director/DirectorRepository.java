package ru.yandex.practicum.filmorate.repository.director;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    public Director create(Director director);

    public Director update(Director director);

    public Optional<Director> getById(int id);

    public List<Director> getAll();
    public void remove(int id);
}
