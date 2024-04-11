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

@Repository
@RequiredArgsConstructor
public class ReviewDbRepository implements ReviewRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public int add(Review review) {
        String sql = "insert into REVIEWS(REVIEW_CONTENT, " +
                "REVIEW_TYPE, " +
                "REVIEW_USER_ID, " +
                "REVIEW_FILM_ID) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"REVIEW_ID"});
                    ps.setString(1, review.getContent());
                    ps.setBoolean(2, review.isPositive());
                    ps.setInt(3, review.getUserId());
                    ps.setInt(4, review.getFilmId());
                    return ps;
                }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public boolean update(Review review) {
        String sql = "update REVIEWS set REVIEW_CONTENT = ?, " +
                "REVIEW_TYPE = ?, " +
                "REVIEW_USER_ID = ?, " +
                "REVIEW_FILM_ID = ?, " +
                "REVIEW_USEFUL = ? " +
                "where REVIEW_ID = ?";

        return jdbcOperations.update(sql,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getReviewId()) > 0;
    }

    @Override
    public boolean remove(int reviewId) {
        String sql = "delete from REVIEWS where REVIEW_ID = ?";

        return jdbcOperations.update(sql, reviewId) > 0;
    }

    @Override
    public Review getById(int reviewId) {
        String sql = "select REVIEW_ID, " +
                "REVIEW_CONTENT, " +
                "REVIEW_TYPE, " +
                "REVIEW_USER_ID, " +
                "REVIEW_FILM_ID, " +
                "REVIEW_USEFUL " +
                "from REVIEWS " +
                "where REVIEW_ID = ?";
        try {
            return jdbcOperations.queryForObject(sql, this::makeReview);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Отзыва с id = %d не существует.", reviewId));
        }
    }

    @Override
    public Collection<Review> getAllByFilm(int filmId, int count) {
        String sql = "select REVIEW_ID, " +
                "REVIEW_CONTENT, " +
                "REVIEW_TYPE, " +
                "REVIEW_USER_ID, " +
                "REVIEW_FILM_ID, " +
                "REVIEW_USEFUL " +
                "from REVIEWS " +
                "where REVIEW_FILM_ID = ? " +
                "order by REVIEW_USEFUL desc " +
                "limit ?";
        return jdbcOperations.query(sql, this::makeReview, count);
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(rs.getInt("REVIEW_ID"),
                rs.getString("REVIEW_CONTENT"),
                rs.getBoolean("REVIEW_TYPE"),
                rs.getInt("REVIEW_USER_ID"),
                rs.getInt("REVIEW_FILM_ID"),
                rs.getInt("REVIEW_USEFUL"));
    }
}
