package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcOperations jdbcOperations;

    @Override
    public void add(Film film) {
        String sqlIntoFilms = "insert into FILMS(FILM_NAME," +
                    " FILM_DESCRIPTION," +
                    " FILM_RELEASE_DATE," +
                    " FILM_DURATION, FILM_MPA)" +
                "values (?, ?, ?, ?, ?)";

        jdbcOperations.update(sqlIntoFilms,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
    }

    @Override
    public boolean update(Film film) {
        String sql = "update FILMS set " +
                "FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?, FILM_DURATION = ?, FILM_MPA = ?" +
                "where FILM_ID = ?";

        return jdbcOperations.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) > 0;
    }

    @Override
    public void remove(int filmId) {
        String sqlRemoveFromFilmGenre = "delete from FILM_GENRE where FILM_ID = ?";
        String sqlRemoveFromFilms = "delete from FILMS where FILM_ID = ?";

        jdbcOperations.update(sqlRemoveFromFilmGenre, filmId);
        jdbcOperations.update(sqlRemoveFromFilms, filmId);
    }

    @Override
    public Film getById(int filmId) {
        String sql = "select F.FILM_ID," +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION," +
                "F.FILM_RELEASE_DATE," +
                "F.FILM_DURATION," +
                "F.FILM_MPA," +
                "M.MPA_TITLE," +
                "from FILMS F " +
                "join MPA M on F.FILM_MPA = M.MPA_ID " +
                "where F.FILM_ID = ?";
        try {
            return jdbcOperations.queryForObject(sql, this::makeFilm, filmId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Филма с id = %d не существует.", filmId));
        }
    }

    @Override
    public Collection<Film> values() {
        String sql = "select F.FILM_ID," +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION," +
                "F.FILM_RELEASE_DATE," +
                "F.FILM_DURATION," +
                "F.FILM_MPA," +
                "M.MPA_TITLE," +
                "from FILMS F " +
                "join MPA M on F.FILM_MPA = M.MPA_ID";

        return jdbcOperations.query(sql, this::makeFilm);
    }

    @Override
    public Film getLast() {
        String sqlQuery = "select F.FILM_ID," +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION," +
                "F.FILM_RELEASE_DATE," +
                "F.FILM_DURATION," +
                "F.FILM_MPA," +
                "M.MPA_TITLE," +
                "from FILMS F " +
                "join MPA M on F.FILM_MPA = M.MPA_ID " +
                "order by F.FILM_ID desc " +
                "limit 1";

        return jdbcOperations.queryForObject(sqlQuery, this::makeFilm);
    }

    private Film makeFilm(ResultSet resultSet, int rn) throws SQLException {
        String sql = "select FILM_GENRE.GENRE_ID, GENRE_TITLE " +
                "from FILM_GENRE " +
                "join PUBLIC.GENRES G2 on G2.GENRE_ID = FILM_GENRE.GENRE_ID " +
                "where FILM_ID = ?";

        Collection<Genre> genres = jdbcOperations.query(sql, (rs, rowNum) -> {
            return new Genre(rs.getInt("GENRE_ID"),
                    rs.getString("GENRE_TITLE"));
        }, resultSet.getInt("FILM_ID"));

        return new Film(resultSet.getInt("FILM_ID"),
                resultSet.getString("FILM_NAME"),
                resultSet.getString("FILM_DESCRIPTION"),
                resultSet.getObject("FILM_RELEASE_DATE", LocalDate.class),
                resultSet.getLong("FILM_DURATION"),
                new Mpa(resultSet.getInt("FILM_MPA"),
                        resultSet.getString("MPA_TITLE")),
                new LinkedHashSet<>(genres.stream().sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toSet())));
    }
}

