package ru.yandex.practicum.filmorate.repository.review_likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewLikesDbRepository implements ReviewLikesRepository {
    private final JdbcOperations jdbcOperations;


    @Override
    public void addLike(int reviewId, int userId) {
        String sql = "insert into REVIEW_LIKES(REVIEW_ID, USER_ID, IS_LIKE)" +
                "values (?, ?, true)";

        jdbcOperations.update(sql, reviewId, userId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        String sql = "insert into REVIEW_LIKES(REVIEW_ID, USER_ID, IS_LIKE)" +
                "values (?, ?, false)";

        jdbcOperations.update(sql, reviewId, userId);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        String sql = "delete from REVIEW_LIKES where REVIEW_ID = ? and USER_ID = ? and IS_LIKE = true";

        jdbcOperations.update(sql, reviewId, userId);
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        String sql = "delete from REVIEW_LIKES where REVIEW_ID = ? and USER_ID = ? and IS_LIKE = false";

        jdbcOperations.update(sql, reviewId, userId);
    }
}
