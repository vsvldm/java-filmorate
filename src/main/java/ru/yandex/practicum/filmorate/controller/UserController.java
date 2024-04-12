package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User findUser(@PathVariable int userId) {
        return userService.findById(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> findAllFriendsByUser(@PathVariable int userId) {
        return userService.findAllFriendsByUser(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> findAllCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        return userService.findAllCommonFriends(userId, otherId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer userID) {
        userService.deleteById(userID);
    }

    @DeleteMapping
    public void deleteAll() {
        userService.deleteAll();
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addToFriends(@PathVariable int userId, @PathVariable int friendId) {
        return userService.addToFriends(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User removeFromFriends(@PathVariable int userId, @PathVariable int friendId) {
        return userService.removeFromFriends(userId, friendId);
    }
}
