package ru.yandex.practicum.filmorate.repository.review_likes;

public interface ReviewLikesRepository {
    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void removeLike(int reviewId, int userId);

    void removeDislike(int reviewId, int userId);
}
