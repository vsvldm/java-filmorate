package ru.yandex.practicum.filmorate.repository.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewRepository {
    public int add(Review review);

    public boolean update(Review review);

    public boolean remove(int reviewId);

    public Review getById(int reviewId);

    public Collection<Review> getAllByFilm(int filmId, int count);

    Collection<Review> getAll(int count);
}
