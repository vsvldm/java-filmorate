package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Feed {
    private Timestamp timestamp;
    private int userId;
    private String eventType;
    private String operation;
    private int eventId;
    private int entityId;
}
