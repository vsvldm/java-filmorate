package ru.yandex.practicum.filmorate.repository.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ReviewDbRepository implements ReviewRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public int add(Review review) {
        String sql = "INSERT INTO REVIEWS(REVIEW_CONTENT, " +
                "REVIEW_TYPE, " +
                "REVIEW_USER_ID, " +
                "REVIEW_FILM_ID) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"REVIEW_ID"});
                    ps.setString(1, review.getContent());
                    ps.setBoolean(2, review.getIsPositive());
                    ps.setInt(3, review.getUserId());
                    ps.setInt(4, review.getFilmId());
                    return ps;
                }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public boolean update(Review review) {
        String sql = "UPDATE REVIEWS SET REVIEW_CONTENT = ?, " +
                "REVIEW_TYPE = ?, " +
                "REVIEW_USEFUL = ? " +
                "WHERE REVIEW_ID = ?";

        return jdbcOperations.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId()) > 0;
    }

    @Override
    public boolean remove(int reviewId) {
        String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";

        return jdbcOperations.update(sql, reviewId) > 0;
    }

    @Override
    public Review getById(int reviewId) {
        String sql = "SELECT R.REVIEW_ID, " +
                                "REVIEW_CONTENT, " +
                                "REVIEW_TYPE, " +
                                "REVIEW_USER_ID, " +
                                "REVIEW_FILM_ID, " +
                                "COALESCE(RL.USEFULNESS, 0) AS USEFUL " +
                     "FROM REVIEWS R " +
                     "LEFT JOIN (SELECT REVIEW_ID, " +
                                        "USER_ID, " +
                                        "SUM(CASE WHEN IS_LIKE THEN 1 ELSE -1 END ) AS USEFULNESS " +
                                 "FROM REVIEW_LIKES " +
                                 "GROUP BY REVIEW_ID, " +
                                            "USER_ID) RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                     "WHERE R.REVIEW_ID = ?";

        try {
            return jdbcOperations.queryForObject(sql, this::makeReview, reviewId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Отзыва с id = %d не существует.", reviewId));
        }
    }

    @Override
    public Collection<Review> getAllByFilm(int filmId, int count) {
        String sql = "SELECT R.REVIEW_ID, " +
                                "REVIEW_CONTENT, " +
                                "REVIEW_TYPE, " +
                                "REVIEW_USER_ID, " +
                                "REVIEW_FILM_ID, " +
                                "COALESCE(RL.USEFULNESS, 0) AS USEFUL " +
                     "FROM REVIEWS R " +
                     "LEFT JOIN (SELECT REVIEW_ID, " +
                                        "USER_ID, " +
                                        "SUM(CASE WHEN IS_LIKE THEN 1 ELSE -1 END ) AS USEFULNESS " +
                                 "FROM REVIEW_LIKES " +
                                 "GROUP BY REVIEW_ID, " +
                                            "USER_ID) RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                     "WHERE REVIEW_FILM_ID = ? " +
                     "GROUP BY R.REVIEW_ID " +
                     "ORDER BY USEFUL DESC " +
                     "LIMIT ?";

        return jdbcOperations.query(sql, this::makeReview, filmId, count);
    }

    @Override
    public Collection<Review> getAll(int count) {
        String sql = "SELECT R.REVIEW_ID, " +
                                "REVIEW_CONTENT, " +
                                "REVIEW_TYPE, " +
                                "REVIEW_USER_ID, " +
                                "REVIEW_FILM_ID, " +
                                "COALESCE(RL.USEFULNESS, 0) AS USEFUL " +
                     "FROM REVIEWS R " +
                     "LEFT JOIN (SELECT REVIEW_ID, " +
                                        "USER_ID, " +
                                        "SUM(CASE WHEN IS_LIKE THEN 1 ELSE -1 END ) AS USEFULNESS " +
                                 "FROM REVIEW_LIKES " +
                                 "GROUP BY REVIEW_ID, " +
                                            "USER_ID) RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                     "ORDER BY USEFUL DESC " +
                     "LIMIT ?";

        return jdbcOperations.query(sql, this::makeReview, count);
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("REVIEW_ID"))
                .content(rs.getString("REVIEW_CONTENT"))
                .isPositive(rs.getBoolean("REVIEW_TYPE"))
                .userId(rs.getInt("REVIEW_USER_ID"))
                .filmId(rs.getInt("REVIEW_FILM_ID"))
                .useful(rs.getInt("USEFUL"))
                .build();
    }
}
