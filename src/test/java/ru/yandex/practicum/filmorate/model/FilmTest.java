package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class FilmTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void filmNameNotBeEmptyTest() {
        Set<Genre> genres =  new HashSet<>();
        genres.add(new Genre(1, null));
        Film film = new Film(1, " ", "Description", LocalDate.now(), 0, new Mpa(1, null), genres);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void filmDescriptionMaxLengthTest() {
        Set<Genre> genres =  new HashSet<>();

        genres.add(new Genre(1, null));
        Film film = new Film(1, "Name",
                "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd" +
                "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasda" +
                "asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasda",
                LocalDate.now(),
                0,
                new Mpa(1, null),
                genres);

        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void filmValidDateTest() {
        Set<Genre> genres =  new HashSet<>();

        genres.add(new Genre(1, null));
        Film film = new Film(1, "Name",
                "Description",
                LocalDate.of(1700, 1, 1),
                0,
                new Mpa(1, null),
                genres);

        assertFalse(validator.validate(film).isEmpty()); // Проверяем, что есть ошибки валидации
    }

    @Test
    public void filmDurationPositiveOrZeroTest() {
        Set<Genre> genres =  new HashSet<>();

        genres.add(new Genre(1, null));
        Film film = new Film(1, "Name", "Description", LocalDate.now(), -10, new Mpa(1, null), genres);
        assertFalse(validator.validate(film).isEmpty()); // Проверяем, что есть ошибки валидации
    }
}
