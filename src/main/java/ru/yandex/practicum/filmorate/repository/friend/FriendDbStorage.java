package ru.yandex.practicum.filmorate.repository.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
@Primary
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(int userId, int friendId) {
        String sql = "insert into FRIENDS(USER_ID, FRIEND_ID) " +
                "values(?, ?)";

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public boolean remove(int userId, int friendId) {
        String sql = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";

        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public Collection<Integer> getFriendIdsByUser(int userId) {
        String sql = "select FRIEND_ID from FRIENDS where USER_ID = ?";

        return jdbcTemplate.query(sql, this::mapRowToInteger, userId);
    }

    @Override
    public Collection<Integer> getCommonFriends(int userId, int otherId) {
        String sql = "select FRIEND_ID " +
                "from FRIENDS " +
                "where USER_ID in (?, ?) " +
                "group by FRIEND_ID " +
                "having count(FRIEND_ID) = 2";

        return jdbcTemplate.query(sql, this::mapRowToInteger, userId, otherId);
    }

    private Integer mapRowToInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FRIEND_ID");
    }
}