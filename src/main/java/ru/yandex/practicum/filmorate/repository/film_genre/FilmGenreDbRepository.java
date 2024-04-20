package ru.yandex.practicum.filmorate.repository.film_genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbRepository implements FilmGenreRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public void add(int filmId, Set<Genre> genres) {
        String sql = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) " +
                "VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        if (genres != null) {
            for (Genre genre : genres) {
                batchArgs.add(new Object[]{filmId, genre.getId()});
            }
        }
        jdbcOperations.batchUpdate(sql, batchArgs);
    }

    @Override
    public boolean remove(int filmId) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";

        return jdbcOperations.update(sql, filmId) > 0;
    }

    @Override
    public Collection<Genre> getByFilm(int filmId) {
        String sql = "SELECT FG.GENRE_ID, GENRE_TITLE " +
                "FROM FILM_GENRE FG " +
                "JOIN GENRES G ON G.GENRE_ID = FG.GENRE_ID " +
                "WHERE FILM_ID = ?" +
                "ORDER BY GENRE_ID";

        return jdbcOperations.query(sql, (rs, rowNum) -> {
            return new Genre(rs.getInt("GENRE_ID"),
                    rs.getString("GENRE_TITLE"));
        }, filmId);
    }
}