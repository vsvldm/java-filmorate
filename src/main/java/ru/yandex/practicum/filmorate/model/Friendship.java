package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private final int userId;
    private final int friendId;
    private final boolean isFriends;
}
