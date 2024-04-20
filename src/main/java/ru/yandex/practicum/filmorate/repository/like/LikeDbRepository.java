package ru.yandex.practicum.filmorate.repository.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class LikeDbRepository implements LikeRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public boolean add(int filmId, int userId) {
        String sql = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?, ?)";

        return jdbcOperations.update(sql, filmId, userId) > 0;
    }

    @Override
    public Collection<Integer> getUserLikesByFilm(int filmId) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";

        return jdbcOperations.query(sql, (rs, rowNum) -> {
            return rs.getInt("USER_ID");
        }, filmId);
    }

    @Override
    public boolean remove(int filmId, int userId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";

        return jdbcOperations.update(sql, filmId, userId) > 0;
    }
}