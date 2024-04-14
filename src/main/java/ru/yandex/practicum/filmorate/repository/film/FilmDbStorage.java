package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcOperations jdbcOperations;


    @Override
    public int add(Film film) {
        String sql = "insert into FILMS(FILM_NAME," +
                    " FILM_DESCRIPTION," +
                    " FILM_RELEASE_DATE," +
                    " FILM_DURATION," +
                    " FILM_MPA)" +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"FILM_ID"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                    ps.setLong(4, film.getDuration());
                    ps.setInt(5, film.getMpa().getId());
                    return ps;
                }, keyHolder);

        return keyHolder.getKey().intValue();
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
    public boolean deleteById(int filmID) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";

        return jdbcOperations.update(sqlQuery, filmID) > 0;
    }

    @Override
    public boolean deleteAll() {
        String sqlQuery = "DELETE FROM FILMS";

        return jdbcOperations.update(sqlQuery) > 0;
    }

    @Override
    public Film getById(int filmId) {
        String sql = "select F.FILM_ID," +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION," +
                "F.FILM_RELEASE_DATE," +
                "F.FILM_DURATION," +
                "F.FILM_MPA," +
                "M.MPA_TITLE " +
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
    public Collection<Film> getAllFilms() {
        String sql = "select F.FILM_ID," +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION," +
                "F.FILM_RELEASE_DATE," +
                "F.FILM_DURATION," +
                "F.FILM_MPA," +
                "M.MPA_TITLE " +
                "from FILMS F " +
                "join MPA M on F.FILM_MPA = M.MPA_ID";

        return jdbcOperations.query(sql, this::makeFilm);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = "select F.FILM_ID, " +
                "FILM_NAME, " +
                "FILM_DESCRIPTION, " +
                "FILM_RELEASE_DATE, " +
                "FILM_DURATION, " +
                "FILM_MPA, " +
                "M.MPA_TITLE " +
                "from FILMS F " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "join MPA M on F.FILM_MPA = M.MPA_ID " +
                "group by F.FILM_ID " +
                "having count(L.USER_ID) >= 0 " +
                "order by count(L.USER_ID) desc " +
                "limit ?";

        return jdbcOperations.query(sql, this::makeFilm, count);
    }

    @Override
    public List<Film> findFilmsByDirectorSortByYear(int directorId) {
        String sql = "SELECT f.FILM_ID, " +
                "f.FILM_NAME," +
                "f.FILM_DESCRIPTION, " +
                "f.FILM_RELEASE_DATE, " +
                "f.FILM_DURATION, " +
                "f.FILM_MPA, " +
                "m.MPA_TITLE " +
                "FROM PUBLIC.FILM_DIRECTOR fd " +
                "JOIN PUBLIC.FILMS f ON fd.FILM_ID = f.FILM_ID " +
                "JOIN PUBLIC.MPA m on f.FILM_MPA = m.MPA_ID " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "ORDER BY EXTRACT(YEAR FROM f.FILM_RELEASE_DATE)";
        try {
            return jdbcOperations.query(sql, this::makeFilm, directorId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Film> findFilmsByDirectorSortByLikes(int directorId) {
        String sql = "SELECT f.FILM_ID, " +
                "f.FILM_NAME," +
                "f.FILM_DESCRIPTION, " +
                "f.FILM_RELEASE_DATE, " +
                "f.FILM_DURATION, " +
                "f.FILM_MPA, " +
                "m.MPA_TITLE " +
                "FROM PUBLIC.FILM_DIRECTOR fd " +
                "JOIN PUBLIC.FILMS f ON fd.FILM_ID = f.FILM_ID " +
                "LEFT JOIN LIKES l on f.FILM_ID = l.FILM_ID " +
                "JOIN PUBLIC.MPA m on f.FILM_MPA = m.MPA_ID " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(l.USER_ID)";
        try {
            return jdbcOperations.query(sql, this::makeFilm, directorId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    private Film makeFilm(ResultSet resultSet, int rn) throws SQLException {
        String sql = "select FG.GENRE_ID, GENRE_TITLE " +
                "from FILM_GENRE FG " +
                "join GENRES G on G.GENRE_ID = FG.GENRE_ID " +
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
                new LinkedHashSet<>(genres.stream().sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toCollection(LinkedHashSet::new))),
                new HashSet<>());
    }

    @Override
    public List<Film> getRecommendations(int id) {
        String sql = "SELECT * FROM FILMS F " +
                        "JOIN MPA M ON F.FILM_MPA = M.MPA_ID " +
                        "WHERE F.FILM_ID IN (" +
                        "SELECT FILM_ID FROM LIKES " +
                        "WHERE USER_ID IN (" +
                        "SELECT FL1.USER_ID FROM LIKES FL1 " +
                        "RIGHT JOIN LIKES FL2 ON FL2.FILM_ID = FL1.FILM_ID " +
                        "GROUP BY FL1.USER_ID, FL2.USER_ID " +
                        "HAVING FL1.USER_ID IS NOT NULL AND " +
                        "FL1.USER_ID != ? AND " +
                        "FL2.USER_ID = ? " +
                        "ORDER BY COUNT(FL1.USER_ID) DESC " +
                        "LIMIT 3 " +
                        ") " +
                        "AND FILM_ID NOT IN (" +
                        "SELECT FILM_ID FROM LIKES " +
                        "WHERE USER_ID = ?" +
                        ")" +
                        ")";

        return jdbcOperations.query(sql, this::makeFilm, id,id,id);
    }
}

