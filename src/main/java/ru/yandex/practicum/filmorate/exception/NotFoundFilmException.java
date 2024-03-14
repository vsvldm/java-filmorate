package ru.yandex.practicum.filmorate.exception;

public class NotFoundFilmException extends RuntimeException {
    public NotFoundFilmException(String message) {
        super(message);
    }
}
