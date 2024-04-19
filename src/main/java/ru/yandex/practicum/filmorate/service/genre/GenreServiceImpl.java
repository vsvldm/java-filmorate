package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public Genre findById(int genreId) {
        log.info("GenreService: Начало выполнения метода findById.");
        Genre genre = genreRepository.getById(genreId);

        log.info("GenreService: Жанр с id = {} успешно найден", genreId);
        return genre;
    }

    @Override
    public List<Genre> findAll() {
        log.info("GenreService: Начало выполнения метода findAll.");
        List<Genre> genres = new ArrayList<>(genreRepository.values());

        log.info("GenreService: Список жанров найден.");
        return genres;
    }
}
