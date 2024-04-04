package ru.yandex.practicum.filmorate.repository.film_genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbRepository implements FilmGenreRepository {
    private final JdbcOperations jdbcOperations;
    @Override
    public void add(int filmId, Set<Genre> genres) {
        String sql = "insert into FILM_GENRE(FILM_ID, GENRE_ID) " +
                "values(?, ?)";

        if (genres !=null) {
            for (Genre genre : genres) {
                jdbcOperations.update(sql, filmId, genre.getId());
            }
        }
    }

    @Override
    public Collection<Genre> valuesByFilm(int filmId) {
        String sql = "select FILM_GENRE.GENRE_ID, GENRE_TITLE " +
                "from FILM_GENRE " +
                "join PUBLIC.GENRES G2 on G2.GENRE_ID = FILM_GENRE.GENRE_ID " +
                "where FILM_ID = ?";

        return jdbcOperations.query(sql,this::makeGenre, filmId);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"),
                rs.getString("GENRE_TITLE"));
    }
}