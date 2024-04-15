package ru.yandex.practicum.filmorate.repository.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FeedDBRepository implements FeedRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getFeedById(int id) {
        String sqlQuery = "select * from feed where USER_ID = ? order by EVENT_TYPE ASC, OPERATION ASC";
        //order by EVENT_TYPE ASC, OPERATION ASC
        System.out.println("Запрос формируется по id - " + id);
        return jdbcTemplate.query(sqlQuery, this::feedRowToFilm, id);
    }

    @Override
    public void addFeed(String type, String operation, int userId, int entityId) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FEED")
                .usingGeneratedKeyColumns("EVENT_ID");
        Feed feed = Feed.builder()
                .timestamp(Timestamp.valueOf(LocalDateTime.now()).getTime())
                .userId(userId)
                .eventType(type)
                .operation(operation)
                .entityId(entityId)
                .build();
        insert.execute(feedToMap(feed));
    }

    public Map<String, Object> feedToMap(Feed feed) {
        return Map.of(
                "EVENT_ID", feed.getEventId(),
                "USER_ID", feed.getUserId(),
                "ENTITY_ID", feed.getEntityId(),
                "EVENT_TYPE", feed.getEventType(),
                "OPERATION", feed.getOperation(),
                "FEED_TIMESTAMP",feed.getTimestamp()
        );
    }

    private Feed feedRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getInt("EVENT_ID"))
                .userId(resultSet.getInt("USER_ID"))
                .entityId(resultSet.getInt("ENTITY_ID"))
                .eventType(resultSet.getString("EVENT_TYPE"))
                .operation(resultSet.getString("OPERATION"))
                .timestamp(resultSet.getLong("FEED_TIMESTAMP"))
                .build();
    }
}
