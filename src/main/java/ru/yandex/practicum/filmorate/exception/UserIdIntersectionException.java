package ru.yandex.practicum.filmorate.exception;

public class UserIdIntersectionException extends RuntimeException {
    public UserIdIntersectionException(String message) {
        super(message);
    }
}
