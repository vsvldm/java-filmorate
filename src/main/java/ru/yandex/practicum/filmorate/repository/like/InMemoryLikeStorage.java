//package ru.yandex.practicum.filmorate.repository.like;
//
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public class InMemoryLikeStorage implements LikeStorage {
//    private final Map<Integer,Set<Integer>> likes = new HashMap<>();
//
//    @Override
//    public boolean add(int filmId, int userId) {
//        return likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
//    }
//
//    @Override
//    public Collection<Integer> getUserLikesByFilm(int filmId) {
//        return likes.get(filmId);
//    }
//
//    @Override
//    public boolean remove(int filmId, int userId) {
//        return likes.get(filmId).remove(userId);
//    }
//}