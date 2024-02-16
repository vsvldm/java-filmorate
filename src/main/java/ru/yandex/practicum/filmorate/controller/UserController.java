package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataInputException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final List<User> users = new ArrayList<>();
    private int id = 0;

    @GetMapping("/users")
    public List<User> getUsers() {
        return users;
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            log.info("Отсутствует имя пользоваителя. Будет использован login = {} в качестве имени пользователя.", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.add(user);
        log.info("Пользователь с login = {} успешно создан.", user.getLogin());
        return user;
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        try {
            if (users.stream().anyMatch(userFromList -> user.getId() == userFromList.getId())) {
                users.removeIf(userFromList -> user.getId() == userFromList.getId());
                users.add(user);
                log.info("Пользовател с id = {} успешно обновлен.", user.getId());
            } else {
                throw new InvalidDataInputException("Нет пользователя с таким id = " + user.getId());
            }
        } catch (InvalidDataInputException e) {
            log.warn(e.getMessage());
            return ResponseEntity.internalServerError().body(user);
        }
        return ResponseEntity.ok(user);
    }
}
