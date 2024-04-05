package ru.yandex.practicum.filmorate.repository.friend;

import java.util.Collection;

public interface FriendStorage {
    public void add(int userId, int friendId);

    public boolean remove(int userId, int friendId);

    public Collection<Integer> valuesByUser(int userId);

    public Collection<Integer> getCommonFriends(int userId, int friendId);
}