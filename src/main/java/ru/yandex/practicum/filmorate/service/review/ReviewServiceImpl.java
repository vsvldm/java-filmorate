package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewRepository reviewRepository;

    @Override
    public Review create(Review review) {
        return null;
    }

    @Override
    public Review update(Review review) {
        return null;
    }

    @Override
    public void removeById(int reviewId) {

    }

    @Override
    public Review findById(int reviewId) {
        return null;
    }

    @Override
    public List<Review> findAllByFilm(int filmId, int count) {
        return null;
    }

    @Override
    public Review addLike(int reviewId, int userId) {
        return null;
    }

    @Override
    public Review addDislike(int reviewId, int userId) {
        return null;
    }

    @Override
    public void removeLike(int reviewId, int userId) {

    }

    @Override
    public void removeDislike(int reviewId, int userId) {

    }
}
