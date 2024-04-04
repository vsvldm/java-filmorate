package ru.yandex.practicum.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class GenreDbRepository implements GenreRepository {
    private final JdbcOperations jdbcOperations;


    @Override
    public Collection<Genre> values() {
        String sql = "select * from GENRES";

        return jdbcOperations.query(sql, this::makeGenre);
    }

    @Override
    public Genre getById(int genreId) {
        String sql = "select * from GENRES where GENRE_ID = ?";

        try {
            return jdbcOperations.queryForObject(sql, this::makeGenre, genreId);
        } catch(DataAccessException e) {
            throw new NotFoundException(String.format("Жанр с id = %d не найден.", genreId));
        }
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"),
                rs.getString("GENRE_TITLE"));
    }
}
