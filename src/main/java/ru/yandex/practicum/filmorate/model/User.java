package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    private String name;
    @NotBlank
    private final String login;
    @Past
    private final LocalDate birthday;
    @NotNull
    @Email(message = "Email должен содержать специальный символ \"@\".")
    private final String email;
}
