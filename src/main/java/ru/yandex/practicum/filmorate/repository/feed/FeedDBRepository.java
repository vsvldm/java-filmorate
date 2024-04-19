package ru.yandex.practicum.filmorate.repository.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedDBRepository implements FeedRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public List<Feed> getFeedById(int id) {
        String sqlQuery = "SELECT * FROM FEED WHERE USER_ID = ? ";

        return jdbcOperations.query(sqlQuery, this::feedRowToFeed, id);
    }

    @Override
    public void addFeed(Type type, Operation operation, int userId, int entityId) {
        String sql = "INSERT INTO FEED(" +
                " USER_ID," +
                " ENTITY_ID," +
                " EVENT_TYPE," +
                " OPERATION," +
                " FEED_TIMESTAMP)" +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"EVENT_ID"});
                    ps.setInt(1, userId);
                    ps.setInt(2, entityId);
                    ps.setString(3, type.toString());
                    ps.setString(4, operation.toString());
                    ps.setLong(5, Timestamp.valueOf(LocalDateTime.now()).getTime());
                    return ps;
                }, keyHolder);
    }

    private Feed feedRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getInt("EVENT_ID"))
                .userId(resultSet.getInt("USER_ID"))
                .entityId(resultSet.getInt("ENTITY_ID"))
                .eventType(Type.valueOf(resultSet.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(resultSet.getString("OPERATION")))
                .timestamp(resultSet.getLong("FEED_TIMESTAMP"))
                .build();
    }
}
