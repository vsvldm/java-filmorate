package ru.yandex.practicum.filmorate.storage.friend;

import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ToString
public class InMemoryFriendStorage implements FriendStorage {
    private final Set<Integer> friends = new HashSet<>();

    @Override
    public void add(int friendId) {
        friends.add(friendId);
    }

    @Override
    public boolean remove(int friendId) {
        return friends.removeIf(i -> friendId == i);
    }

    @Override
    public Set<Integer> values() {
        return friends;
    }
}