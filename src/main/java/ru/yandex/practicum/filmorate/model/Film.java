package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.filmorate.annotation.ValidDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<Integer> likes = new HashSet<>();
}
