package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataInputException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int id = 0;
    @GetMapping("/films")
    public List<Film> getFilms() {
        return films;
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(++id);
        films.add(film);
        log.info("Фильм {} успешно создан", film.getName());
        return film;
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm (@Valid @RequestBody Film film) {
        try {
            if (films.stream().anyMatch(filmFromList -> film.getId() == filmFromList.getId())) {
                films.removeIf(userFromList -> film.getId() == userFromList.getId());
                films.add(film);
                log.info("Фильм с id = {} успешно обновлен", film.getId());
            } else {
                throw new InvalidDataInputException("Нет фильма с таким id = " + film.getId());
            }
        } catch (InvalidDataInputException e) {
            log.warn(e.getMessage());
            return  ResponseEntity.internalServerError().body(film);
        }
        return ResponseEntity.ok(film);
    }
}
