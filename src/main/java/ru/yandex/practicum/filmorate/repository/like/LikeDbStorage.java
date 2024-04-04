package ru.yandex.practicum.filmorate.repository.like;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
@Primary
public class LikeDbStorage implements LikeStorage {
    private final JdbcOperations jdbcOperations;

    @Override
    public boolean add(int filmId, int userId) {
        String sql = "insert into LIKES(FILM_ID, USER_ID) values(?, ?)";

        return jdbcOperations.update(sql, filmId, userId) > 0;
    }

    @Override
    public Collection<Integer> valuesByFilm(int filmId) {
        String sql = "select USER_ID from LIKES where FILM_ID = ?";

        return jdbcOperations.query(sql, (rs, rowNum) -> {
            return rs.getInt("USER_ID");
        }, filmId);
    }

    @Override
    public boolean remove(int filmId, int userId) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";

        return jdbcOperations.update(sql, filmId, userId) > 0;
    }
}