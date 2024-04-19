package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.feed.FeedDBRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.review_likes.ReviewLikesRepository;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewRepository reviewRepository;
    private final ReviewLikesRepository reviewLikesRepository;
    private final FeedDBRepository feedStorage;

    @Override
    public Review create(Review review) {
        log.info("Начало выполнения метода create.");
        log.info("Проверка существования фильма с id = {} и  пользователя с id = {}.", review.getFilmId(), review.getUserId());
        if (review.getFilmId() == 0 || review.getUserId() == 0) {
            throw new BadRequestException("Фильм или пользователь не может быть с id = 0");
        }
        filmStorage.getById(review.getFilmId());
        userStorage.getById(review.getUserId());

        review.setReviewId(reviewRepository.add(review));
        log.info("Отзыв с id = {} успешно создан.", review.getReviewId());


        log.info("Запись информации о событии в таблицу");
        feedStorage.addFeed("REVIEW", "ADD", review.getUserId(), review.getReviewId());
        log.info("Информация о событии успешно сохранена");
        return review;
    }

    @Override
    public Review update(Review review) {
        Review r = reviewRepository.getById(review.getReviewId());
        log.info("Начало выполнения метода update.");
        log.info("Проверка существования отзыва с id ={}.", review.getReviewId());
        if (reviewRepository.update(review)) {
            log.info("Отзыв с id = {} успешно обновлен", review.getReviewId());
            log.info("Запись информации о событии в таблицу");
            feedStorage.addFeed("REVIEW", "UPDATE", r.getUserId(), review.getReviewId());
            log.info("Информация о событии успешно сохранена");
            return reviewRepository.getById(review.getReviewId());
        } else {
            throw new NotFoundException(String.format("Отзыва с id = %d не существует.", review.getReviewId()));
        }
    }

    @Override
    public void removeById(int reviewId) {
        log.info("Начало выполнения метода removeById.");
        int userId = reviewRepository.getById(reviewId).getUserId();

        reviewRepository.remove(reviewId);
        log.info("Отзыв с id = {} успешно удален.", reviewId);

        log.info("Запись информации о событии в таблицу");
        feedStorage.addFeed("REVIEW", "REMOVE", userId, reviewId);
        log.info("Информация о событии успешно сохранена");
    }

    @Override
    public Review findById(int reviewId) {
        log.info("Начало выполнения метода findById.");
        Review review = reviewRepository.getById(reviewId);

        log.info("Отзыв с id = {} успешно найден.", reviewId);
        return review;
    }

    @Override
    public List<Review> findAllByFilm(int filmId, int count) {
        log.info("Начало выполнения метода findAllByFilm.");
        List<Review> reviews = new ArrayList<>();

        if (filmId == 0) {
            log.info("Параметр filmId отсутствует в запросе.");
            reviews.addAll(reviewRepository.getAll(count));
            log.info("Получен список всех отзывов.");
            return reviews;
        }
        log.info("Проверка существования фильма с id = {}", filmId);
        filmStorage.getById(filmId);
        reviews.addAll(reviewRepository.getAllByFilm(filmId, count));
        log.info("Список отзывов для фильма с id = {} успешно найден.", filmId);
        return reviews;
    }

    @Override
    public void addLike(int reviewId, int userId) {
        log.info("Начало выполнения метода addLike.");
        log.info("Проверка существования фильма с id = {} и  пользователя с id = {}.", reviewId, userId);
        Review review = reviewRepository.getById(reviewId);
        User user = userStorage.getById(userId);

        reviewLikesRepository.addLike(review.getReviewId(), user.getId());
        log.info("Отзыв с id = {} успешно создан.", reviewId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        log.info("Начало выполнения метода addDislike.");
        log.info("Проверка существования фильма с id = {} и  пользователя с id = {}.", reviewId, userId);
        Review review = reviewRepository.getById(reviewId);
        User user = userStorage.getById(userId);

        reviewLikesRepository.addDislike(review.getReviewId(), user.getId());
        log.info("Отзыв с id = {} успешно создан.", reviewId);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        log.info("Начало выполнения метода removeLike.");
        log.info("Проверка существования фильма с id = {} и  пользователя с id = {}.", reviewId, userId);
        Review review = reviewRepository.getById(reviewId);
        User user = userStorage.getById(userId);

        reviewLikesRepository.removeLike(review.getReviewId(), user.getId());
        log.info("Отзыв с id = {} успешно удален.", reviewId);

    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        log.info("Начало выполнения метода removeDislike.");
        log.info("Проверка существования фильма с id = {} и  пользователя с id = {}.", reviewId, userId);
        Review review = reviewRepository.getById(reviewId);
        User user = userStorage.getById(userId);

        reviewLikesRepository.removeDislike(review.getReviewId(), user.getId());
        log.info("Отзыв с id = {} успешно удален.", reviewId);
    }
}
