package ru.yandex.practicum.filmorate.exceptions;

public class UserIdIntersectionException extends RuntimeException {
    public UserIdIntersectionException(String message) {
        super(message);
    }
}
