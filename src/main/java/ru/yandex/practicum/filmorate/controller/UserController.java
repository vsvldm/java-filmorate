package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataInputException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<Integer,User> users = new HashMap<>();
    private int id = 0;

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            log.info("Отсутствует имя пользоваителя. Будет использован login = {} в качестве имени пользователя.", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользовател с id = {} успешно обновлен.", user.getId());
        } else {
            throw new InvalidDataInputException("Нет пользователя с таким id = " + user.getId());
        }
        return user;
    }
}
