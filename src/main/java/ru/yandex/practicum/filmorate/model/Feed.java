package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Objects;

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
