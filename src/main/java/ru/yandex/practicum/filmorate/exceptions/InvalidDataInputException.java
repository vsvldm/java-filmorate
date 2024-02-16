package ru.yandex.practicum.filmorate.exceptions;

public class InvalidDataInputException extends Exception {
    public InvalidDataInputException() {

    }

    public InvalidDataInputException(String message) {
        super(message);
    }
}
