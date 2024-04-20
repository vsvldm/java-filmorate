package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;

public interface DirectorService {
    Director create(Director director);

    Director update(Director director);

    Director getById(int id);

    List<Director> getAll();

    void remove(int id);
}