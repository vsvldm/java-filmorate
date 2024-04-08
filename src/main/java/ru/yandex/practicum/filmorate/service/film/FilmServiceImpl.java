package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.film_genre.FilmGenreRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final FilmGenreRepository filmGenreRepository;

    @Override
    public Film create(Film film) {
        log.info("Начало выполнения метода create.");
        int filmId = filmStorage.add(film);

        filmGenreRepository.add(filmId,film.getGenres());
        film.setId(filmId);
        log.info("Фильм id = {} успешно создан", filmId);
        return film;
    }

//    @Override
//    public Film update(Film film) {
//        log.info("Начало выполнения метода update.");
//        log.info("Проверка существования фильма с id ={}.", film.getId());
//        if (filmStorage.update(film)) {
//            Film filmFromDB = filmStorage.getById(film.getId());
//
//            log.info("Фильм с id = {} успешно обновлен", film.getId());
//            return filmFromDB;
//        } else {
//            throw new NotFoundException(String.format("Фильма с id = %d не существует.", film.getId()));
//        }
//    }

    @Override
    public Film update(Film film) {

        boolean isSuccess = filmRepository.update(film);

        if (!isSuccess) {
            throw new NotFoundException("Film with id = " + film.getId() + " hasn't been found");
        }

        genreRepository.removeGenresForFilm(film.getId());

        Set<Integer> uniqueGenreIds = new HashSet<>();

        for (Genre genre : film.getGenres()) {
            uniqueGenreIds.add(genre.getId());
        }

        List<Genre> uniqueGenres = new ArrayList<>();
        for (Integer genreId : uniqueGenreIds) {
            Genre genre = new Genre();
            genre.setId(genreId);
            uniqueGenres.add(genre);
        }

        genreRepository.add(film.getId(), uniqueGenres);
        film.setGenres(uniqueGenres);

    }

    @Override
    public Film findById(int filmId) {
        log.info("Начало выполнения метода findById.");
        Film film = filmStorage.getById(filmId);
        log.info("Фильм с id = {} найден.", filmId);
        return film;
    }

    @Override
    public List<Film> findAll() {
        log.info("Начало выполнения метода findAll.");
        List<Film> films = new ArrayList<>(filmStorage.getAllFilms());

        log.info("Список всех фильмом найден.");
        return films;
    }

    @Override
    public boolean deleteById(Integer filmID) {
        log.info("Начало выполнения метода deleteById.");
        boolean isDeleted = filmStorage.deleteById(filmID);
        log.info("Фильм с id = {} удалён.", filmID);
        return isDeleted;
    }

    @Override
    public boolean deleteAll() {
        log.info("Начало выполнения метода deleteAll.");
        boolean areDeleted = filmStorage.deleteAll();
        log.info("Все фильмы удалены.");
        return areDeleted;
    }

    @Override
    public List<Film> findPopular(int count) {
        log.info("Начало выполнения метода findPopular.");
        List<Film> films = new ArrayList<>(filmStorage.getPopularFilms(count));

        log.info("Список из count = {} самых популярных фильмов найден.", count);
        return films;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        log.info("Начало выполнения метода addLike.");
        log.info("Проверка существования фильма с id = {} и  пользователя с id = {}.", filmId, userId);
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);

        likeStorage.add(film.getId(), user.getId());
        log.info("Пользователь с id = {} поставил лайк фильму c id = {}.", userId, film.getId());
        return film;
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        log.info("Начало выполнения метода removeLike.");
        log.info("Проверка существования фильма с id = {}.", filmId);
        Film film = filmStorage.getById(filmId);

//        if (likeStorage.remove(film.getId(), userId)) {
//            log.info("Лайк пользователя с id = {} удален.", userId);
//        } else {
//            log.info("Пользователь с id = {} не ставил лайк фильму {}.", userId, film.getName());
//        }
//        return film;

        if (!likeStorage.existsLike(filmId, userId)) {
            throw new NotFoundException("User with id = " + userId + " hasn't liked film with id = '" +
                    filmId + "' yet");
        }

        return film;
    }


}