package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.filmorate.annotation.ValidDate;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
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
    @Valid
    private final Mpa mpa;
    @Valid
    private final Set<Genre> genres = new LinkedHashSet<>();
    @Valid
    private final Set<Director> directors = new HashSet<>();
}