package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    public void add(int userId);

    public boolean remove(int userId);

    public int size();
}