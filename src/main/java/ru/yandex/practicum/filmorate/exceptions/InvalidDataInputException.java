package ru.yandex.practicum.filmorate.exceptions;

public class InvalidDataInputException extends RuntimeException {
    public InvalidDataInputException() {

    }

    public InvalidDataInputException(String message) {
        super(message);
    }
}
