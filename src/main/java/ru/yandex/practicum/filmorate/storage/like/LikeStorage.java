package ru.yandex.practicum.filmorate.storage.like;

import java.util.Set;

public interface LikeStorage {
    public void createStorage(int filmId);

    public boolean add(int filmId, int userId);

    public Set<Integer> getByFilm(int filmId);

    public boolean remove(int filmId, int userId);
}