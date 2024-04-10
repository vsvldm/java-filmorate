package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.filmorate.annotation.ValidDate;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    @Getter
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
    @Setter
    @Valid
    private Mpa mpa;
    @Setter
    @Valid
    private  Set<Genre> genres;
}