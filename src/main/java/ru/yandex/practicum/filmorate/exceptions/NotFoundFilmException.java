package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundFilmException extends RuntimeException {
    public NotFoundFilmException(String message) {
        super(message);
    }
}
