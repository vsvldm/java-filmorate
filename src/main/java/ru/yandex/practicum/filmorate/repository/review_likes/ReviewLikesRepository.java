package ru.yandex.practicum.filmorate.repository.review_likes;

public interface ReviewLikesRepository {
    public void addLike(int reviewId, int userId);

    public void addDislike(int reviewId, int userId);

    public void removeLike(int reviewId, int userId);

    public void removeDislike(int reviewId, int userId);
}
