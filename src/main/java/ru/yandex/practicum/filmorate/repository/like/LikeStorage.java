package ru.yandex.practicum.filmorate.repository.like;

import java.util.Collection;

public interface LikeStorage {
    public boolean add(int filmId, int userId);

    public Collection<Integer> getUserLikesByFilm(int filmId);

    public boolean remove(int filmId, int userId);

    boolean existsLike(Integer filmId, Integer userId);

}