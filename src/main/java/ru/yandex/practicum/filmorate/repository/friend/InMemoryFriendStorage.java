package ru.yandex.practicum.filmorate.repository.friend;

import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.*;

@Component
@ToString
public class InMemoryFriendStorage implements FriendStorage {
    private final Map<Integer,Set<Integer>> friends = new HashMap<>();

    @Override
    public void add(int userId, int friendId) {
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
    }

    @Override
    public boolean remove(int userId, int friendId) {
        return friends.get(userId).removeIf(i -> friendId == i);
    }

    @Override
    public Set<Integer> valuesByUser(int userId) {
        Set<Integer> friendsByUser = friends.getOrDefault(userId, null);

        if (friendsByUser != null) {
            return friendsByUser;
        } else {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", userId));
        }
    }

    @Override
    public Collection<Integer> getCommonFriends(int userId, int otherId) {
        Set<Integer> usersFriends = new HashSet<>(valuesByUser(userId));

        usersFriends.retainAll(valuesByUser(otherId));
        return usersFriends;
    }
}