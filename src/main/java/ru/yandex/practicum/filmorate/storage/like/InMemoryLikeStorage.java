package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    private final Map<Integer,Set<Integer>> likes = new HashMap<>();

    @Override
    public void createStorage(int filmId) {
        likes.computeIfAbsent(filmId, k -> new HashSet<>());
    }

    @Override
    public boolean add(int filmId, int userId) {
        return likes.get(filmId).add(userId);
    }

    @Override
    public Set<Integer> getByFilm(int filmId) {
        return likes.get(filmId);
    }

    @Override
    public boolean remove(int filmId, int userId) {
        return likes.get(filmId).remove(userId);
    }
}