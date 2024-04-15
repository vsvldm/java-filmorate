package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feed {
    private int eventId;
    private int userId;
    private int entityId;
    private String eventType;
    private String operation;
    private long timestamp;
}
