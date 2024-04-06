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
        return genreRepository.getById(genreId);
    }

    @Override
    public List<Genre> findAll() {
        return new ArrayList<>(genreRepository.values());
    }
}
