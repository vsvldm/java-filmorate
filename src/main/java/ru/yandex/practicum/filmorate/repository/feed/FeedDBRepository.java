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
public class FeedDBRepository implements FeedRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getFeed() {
        return null;
    }

    public List<Feed> getFeedById(int id) {
        String sqlQuery = "select * from feed where USER_ID = ? order by FEED_TIMESTAMP desc";
        return jdbcTemplate.query(sqlQuery, this::feedRowToFilm, id);
    }

    @Override
    public void addFeed(String type, String operation, int entityId, int userId) {
        LocalDateTime date = LocalDateTime.now();
        Timestamp timestamp = new Timestamp(date.getSecond());
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FEED")
                .usingGeneratedKeyColumns("EVENT_ID");
        Feed feed = Feed.builder()
                .timestamp(timestamp)
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
                "FEED_TIMESTAMP",feed.getTimestamp(),
                "USER_ID", feed.getUserId(),
                "EVENT_TYPE", feed.getEventType(),
                "OPERATION", feed.getOperation(),
                "ENTITY_ID", feed.getEntityId()
        );
    }

    private Feed feedRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getInt("EVENT_ID"))
                .timestamp(resultSet.getTimestamp("FEED_TIMESTAMP"))
                .userId(resultSet.getInt("USER_ID"))
                .eventType(resultSet.getString("EVENT_TYPE"))
                .operation(resultSet.getString("OPERATION"))
                .entityId(resultSet.getInt("ENTITY_ID"))
                .build();
    }
}
