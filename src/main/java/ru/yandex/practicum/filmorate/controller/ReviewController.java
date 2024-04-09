package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{reviewId}")
    public void removeReview(@PathVariable int reviewId) {
        reviewService.removeById(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review findReview(@PathVariable int reviewId) {
        return reviewService.findById(reviewId);
    }

    @GetMapping
    public List<Review> findAllReviewByFilm(@RequestParam(required = false, defaultValue = "0") int filmId,
                                            @RequestParam(required = false, defaultValue = "10") int count) {
        return reviewService.findAllByFilm(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public Review addLikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public Review addDislikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.removeDislike(reviewId, userId);
    }
}
