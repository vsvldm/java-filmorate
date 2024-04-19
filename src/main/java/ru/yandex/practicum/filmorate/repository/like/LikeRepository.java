package ru.yandex.practicum.filmorate.repository.like;

import java.util.Collection;

public interface LikeRepository {
    boolean add(int filmId, int userId);

    Collection<Integer> getUserLikesByFilm(int filmId);

    boolean remove(int filmId, int userId);
}