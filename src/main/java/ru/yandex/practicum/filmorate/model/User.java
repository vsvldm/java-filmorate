package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.friend.InMemoryFriendStorage;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
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
    private final FriendStorage friendStorage = new InMemoryFriendStorage();
}
