package ru.yandex.practicum.filmorate.storage.friend;

import java.util.Set;

public interface FriendStorage {
    public void add(int friendId);

    public boolean remove(int friendId);

    public Set<Integer> values();
}