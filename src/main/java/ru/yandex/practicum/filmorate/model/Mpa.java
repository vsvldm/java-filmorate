package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class Mpa {
    @Max(5)
    private final int id;
    private final String name;
}