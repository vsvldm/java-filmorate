package ru.yandex.practicum.filmorate.storage.friend;

import java.util.Set;

public interface FriendStorage {
    public void createStorage(int userId);

    public boolean add(int userId, int friendId);

    public boolean remove(int userId, int friendId);

    public Set<Integer> valuesByUser(int userId);
}