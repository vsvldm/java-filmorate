package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class Genre {
    @Max(6)
    private final int id;
    private final String name;
}
