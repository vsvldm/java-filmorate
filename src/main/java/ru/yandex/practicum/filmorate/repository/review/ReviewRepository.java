package ru.yandex.practicum.filmorate.repository.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewRepository {
    int add(Review review);

    boolean update(Review review);

    boolean remove(int reviewId);

    Review getById(int reviewId);

    Collection<Review> getAllByFilm(int filmId, int count);

    Collection<Review> getAll(int count);
}
