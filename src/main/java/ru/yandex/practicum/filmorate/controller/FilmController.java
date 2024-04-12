package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    public Film findFilm(@PathVariable int filmId) {
        return filmService.findById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.findPopular(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
         return filmService.update(film);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Integer filmID) {
        filmService.deleteById(filmID);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        filmService.deleteAll();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable int filmId, @PathVariable int userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("director/{directorId}")
    public List<Film> findByDirector(@PathVariable int directorId,
                                     @RequestParam(required = false, defaultValue = "year") String sortBy) {
        return filmService.findByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> findCommonFilms(@RequestParam int userId,
                                      @RequestParam int friendId) {
        return filmService.findCommonFilms(userId, friendId);
    }
}
