package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;


public interface DirectorService {
    public Director create(Director director);

    public Director update(Director director);

    public Director getById(int id);

    public List<Director> getAll();
    public void remove(int id);
}
