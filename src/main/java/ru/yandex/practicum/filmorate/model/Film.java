package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.filmorate.annotation.ValidDate;
import ru.yandex.practicum.filmorate.storage.like.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotNull
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    @ValidDate
    private final LocalDate releaseDate;
    @PositiveOrZero
    private final long duration;
    private final LikeStorage likeStorage = new InMemoryLikeStorage();
}
