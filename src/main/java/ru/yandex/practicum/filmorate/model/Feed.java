package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feed {
    private int eventId;
    private int userId;
    private int entityId;
    private Type eventType;
    private Operation operation;
    private long timestamp;
}
