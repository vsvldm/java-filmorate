package ru.yandex.practicum.filmorate.repository.friend;

import java.util.Collection;

public interface FriendRepository {
    void add(int userId, int friendId);

    boolean remove(int userId, int friendId);

    Collection<Integer> getFriendIdsByUser(int userId);

    Collection<Integer> getCommonFriends(int userId, int friendId);
}