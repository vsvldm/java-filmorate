package ru.yandex.practicum.filmorate.repository.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class FriendDbRepository implements FriendRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(int userId, int friendId) {
        String sql = "INSERT INTO FRIENDS(USER_ID, FRIEND_ID) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public boolean remove(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";

        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public Collection<Integer> getFriendIdsByUser(int userId) {
        String sql = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";

        return jdbcTemplate.query(sql, this::mapRowToInteger, userId);
    }

    @Override
    public Collection<Integer> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT FRIEND_ID " +
                "FROM FRIENDS " +
                "WHERE USER_ID IN (?, ?) " +
                "GROUP BY FRIEND_ID " +
                "HAVING COUNT(FRIEND_ID) = 2";

        return jdbcTemplate.query(sql, this::mapRowToInteger, userId, otherId);
    }

    private Integer mapRowToInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FRIEND_ID");
    }
}