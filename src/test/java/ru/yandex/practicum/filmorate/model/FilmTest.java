package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class FilmTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void filmNameNotBeEmptyTest() {
        Film film = new Film("", "Description", LocalDate.now(), 0);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void filmDescriptionMaxLengthTest() {
        Film film = new Film("Name",
                "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd" +
                "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasda" +
                "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasda" ,
                LocalDate.now(),
                0);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void filmValidDateTest() {
        Film film = new Film("Name",
                "Description",
                LocalDate.of(1700, 1, 1),
                0);
        assertFalse(validator.validate(film).isEmpty()); // Проверяем, что есть ошибки валидации
    }

    @Test
    public void filmDurationPositiveOrZeroTest() {
        Film film = new Film("Name", "Description", LocalDate.now(), -10);
        assertFalse(validator.validate(film).isEmpty()); // Проверяем, что есть ошибки валидации
    }
}