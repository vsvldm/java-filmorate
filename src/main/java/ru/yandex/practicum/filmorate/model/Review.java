package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    private int reviewId;
    @NotNull
    @NotBlank
    private final String content;
    @NotNull
    private final Boolean isPositive;
    private final int userId;
    private final int filmId;
    private final int useful;
}
