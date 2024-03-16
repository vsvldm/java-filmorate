package ru.yandex.practicum.filmorate.storage.friend;

import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundUserException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@ToString
public class InMemoryFriendStorage implements FriendStorage {
    private final Map<Integer,Set<Integer>> friends = new HashMap<>();

    @Override
    public void createStorage(int userId) {
        friends.computeIfAbsent(userId, k -> new HashSet<>());
    }

    @Override
    public boolean add(int userId, int friendId) {
        return friends.get(userId).add(friendId);
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
            throw new NotFoundUserException(String.format("Пользователя с id = %d не существует.", userId));
        }
    }
}