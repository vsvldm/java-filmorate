package ru.yandex.practicum.filmorate.repository.film_genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbRepository implements FilmGenreRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public void add(int filmId, Set<Genre> genres) {
        String sql = "insert into FILM_GENRE(FILM_ID, GENRE_ID) " +
                "values(?, ?)";

        if (genres != null) {
            for (Genre genre : genres) {
                jdbcOperations.update(sql, filmId, genre.getId());
            }
        }
    }

    @Override
    public boolean remove(int filmId) {
        String sql = "delete from FILM_GENRE where FILM_ID = ?";

        return jdbcOperations.update(sql, filmId) > 0;
    }
}