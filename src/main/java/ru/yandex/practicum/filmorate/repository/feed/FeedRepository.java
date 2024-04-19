package ru.yandex.practicum.filmorate.repository.feed;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Type;

import java.util.List;

public interface FeedRepository {
    List<Feed> getFeedById(int id);

    void addFeed(Type type, Operation operation, int entityId, int userId);
}
