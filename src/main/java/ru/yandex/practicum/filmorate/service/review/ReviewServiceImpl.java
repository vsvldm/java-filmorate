package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.Type;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.feed.FeedDBRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.review_likes.ReviewLikesRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikesRepository reviewLikesRepository;
    private final FeedDBRepository feedStorage;

    @Override
    public Review create(Review review) {
        log.info("ReviewService: Начало выполнения метода create.");
        log.info("ReviewService: Проверка существования фильма с id = {} и  пользователя с id = {}.", review.getFilmId(), review.getUserId());
        if (review.getFilmId() == 0 || review.getUserId() == 0) {
            throw new BadRequestException("Фильм или пользователь не может быть с id = 0");
        }
        filmRepository.getById(review.getFilmId());
        userRepository.getById(review.getUserId());

        review.setReviewId(reviewRepository.add(review));
        log.info("ReviewService: Отзыв с id = {} успешно создан.", review.getReviewId());


        log.info("ReviewService: Запись информации о событии в таблицу");
        feedStorage.addFeed(Type.REVIEW, Operation.ADD, review.getUserId(), review.getReviewId());
        log.info("ReviewService: Информация о событии успешно сохранена");
        return review;
    }

    @Override
    public Review update(Review review) {
        Review r = reviewRepository.getById(review.getReviewId());
        log.info("ReviewService: Начало выполнения метода update.");
        log.info("ReviewService: Проверка существования отзыва с id ={}.", review.getReviewId());
        if (reviewRepository.update(review)) {
            log.info("ReviewService: Отзыв с id = {} успешно обновлен", review.getReviewId());
            log.info("ReviewService: Запись информации о событии в таблицу");
            feedStorage.addFeed(Type.REVIEW, Operation.UPDATE, r.getUserId(), review.getReviewId());
            log.info("ReviewService: Информация о событии успешно сохранена");
            return reviewRepository.getById(review.getReviewId());
        } else {
            throw new NotFoundException(String.format("Отзыва с id = %d не существует.", review.getReviewId()));
        }
    }

    @Override
    public void removeById(int reviewId) {
        log.info("ReviewService: Начало выполнения метода removeById.");
        int userId = reviewRepository.getById(reviewId).getUserId();

        reviewRepository.remove(reviewId);
        log.info("ReviewService: Отзыв с id = {} успешно удален.", reviewId);

        log.info("ReviewService: Запись информации о событии в таблицу");
        feedStorage.addFeed(Type.REVIEW, Operation.REMOVE, userId, reviewId);
        log.info("ReviewService: Информация о событии успешно сохранена");
    }

    @Override
    public Review findById(int reviewId) {
        log.info("ReviewService: Начало выполнения метода findById.");
        Review review = reviewRepository.getById(reviewId);

        log.info("ReviewService: Отзыв с id = {} успешно найден.", reviewId);
        return review;
    }

    @Override
    public List<Review> findAllByFilm(int filmId, int count) {
        log.info("ReviewService: Начало выполнения метода findAllByFilm.");
        List<Review> reviews = new ArrayList<>();

        if (filmId == 0) {
            log.info("ReviewService: Параметр filmId отсутствует в запросе.");
            reviews.addAll(reviewRepository.getAll(count));
            log.info("ReviewService: Получен список всех отзывов.");
            return reviews;
        }
        log.info("ReviewService: Проверка существования фильма с id = {}", filmId);
        filmRepository.getById(filmId);
        reviews.addAll(reviewRepository.getAllByFilm(filmId, count));
        log.info("ReviewService: Список отзывов для фильма с id = {} успешно найден.", filmId);
        return reviews;
    }

    @Override
    public void addLike(int reviewId, int userId) {
        log.info("ReviewService: Начало выполнения метода addLike.");
        log.info("ReviewService: Проверка существования фильма с id = {} и  пользователя с id = {}.", reviewId, userId);
        Review review = reviewRepository.getById(reviewId);
        User user = userRepository.getById(userId);

        reviewLikesRepository.addLike(review.getReviewId(), user.getId());
        log.info("ReviewService: Отзыв с id = {} успешно создан.", reviewId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        log.info("ReviewService: Начало выполнения метода addDislike.");
        log.info("ReviewService: Проверка существования фильма с id = {} и  пользователя с id = {}.", reviewId, userId);
        Review review = reviewRepository.getById(reviewId);
        User user = userRepository.getById(userId);

        reviewLikesRepository.addDislike(review.getReviewId(), user.getId());
        log.info("ReviewService: Отзыв с id = {} успешно создан.", reviewId);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        log.info("ReviewService: Начало выполнения метода removeLike.");
        log.info("ReviewService: Проверка существования фильма с id = {} и  пользователя с id = {}.", reviewId, userId);
        Review review = reviewRepository.getById(reviewId);
        User user = userRepository.getById(userId);

        reviewLikesRepository.removeLike(review.getReviewId(), user.getId());
        log.info("ReviewService: Отзыв с id = {} успешно удален.", reviewId);

    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        log.info("ReviewService: Начало выполнения метода removeDislike.");
        log.info("ReviewService: Проверка существования фильма с id = {} и  пользователя с id = {}.", reviewId, userId);
        Review review = reviewRepository.getById(reviewId);
        User user = userRepository.getById(userId);

        reviewLikesRepository.removeDislike(review.getReviewId(), user.getId());
        log.info("ReviewService: Отзыв с id = {} успешно удален.", reviewId);
    }
}
