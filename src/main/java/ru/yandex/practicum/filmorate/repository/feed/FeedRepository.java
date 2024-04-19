package ru.yandex.practicum.filmorate.repository.feed;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedRepository {
    public List<Feed> getFeedById(int id);

    public void addFeed(String type, String operation, int entityId, int userId);
}
