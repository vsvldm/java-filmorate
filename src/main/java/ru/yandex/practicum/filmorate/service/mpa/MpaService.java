package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    public Mpa findById(int mpaId);

    public List<Mpa> findAll();
}
