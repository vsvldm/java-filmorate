package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;

@Data
@EqualsAndHashCode(exclude = "name")
public class Genre {
    @Max(6)
    private final int id;
    private final String name;
}
