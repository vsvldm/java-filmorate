package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaRepository {
    public Mpa getById(int mpaId);

    public Collection<Mpa> values();
}
