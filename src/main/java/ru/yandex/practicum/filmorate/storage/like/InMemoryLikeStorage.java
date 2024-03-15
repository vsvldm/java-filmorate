package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    private final Set<Integer> likes = new HashSet<>();

    @Override
    public void add(int userId) {
        likes.add(userId);
    }

    @Override
    public boolean remove(int userId) {
        return likes.removeIf(i -> userId == i);
    }

    @Override
    public int size() {
        return likes.size();
    }
}