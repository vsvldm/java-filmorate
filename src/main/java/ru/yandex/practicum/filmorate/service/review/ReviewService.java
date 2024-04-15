package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    public Review create(Review review);

    public Review update(Review review);

    public void removeById(int reviewId);

    public Review findById(int reviewId);

    public List<Review> findAllByFilm(int filmId, int count);

    public void addLike(int reviewId, int userId);

    public void addDislike(int reviewId, int userId);

    public void removeLike(int reviewId, int userId);

    public void removeDislike(int reviewId, int userId);
}
