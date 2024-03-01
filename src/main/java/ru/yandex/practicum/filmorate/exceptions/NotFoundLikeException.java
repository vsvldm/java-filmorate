package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundLikeException extends RuntimeException{
    public NotFoundLikeException(String message) {
        super(message);
    }
}
