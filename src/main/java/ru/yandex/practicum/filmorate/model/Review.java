package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Review {
    private int reviewId;
    @NotNull
    @NotBlank
    private final String content;
    @NotNull
    private final boolean isPositive;
    @NotNull
    private final int userId;
    @NotNull
    private final int filmId;
    private final int useful;
}
