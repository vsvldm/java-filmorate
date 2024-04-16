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
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

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
    public Collection<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        List<Film> films;
        if (genreId != null || year != null) {
            if (genreId != null && year != null) {
                String sql = "SELECT f.FILM_ID, " +
                        "f.FILM_NAME, " +
                        "f.FILM_DESCRIPTION, " +
                        "f.FILM_RELEASE_DATE, " +
                        "f.FILM_DURATION, " +
                        "f.FILM_MPA, " +
                        "m.MPA_TITLE, " +
                        "COUNT(l.FILM_ID) as likes_count " +
                        "FROM FILMS f " +
                        "JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
                        "JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID " +
                        "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                        "JOIN MPA m ON f.FILM_MPA = m.MPA_ID " +
                        "WHERE g.GENRE_ID = ? AND EXTRACT(YEAR FROM f.FILM_RELEASE_DATE) = ? " +
                        "GROUP BY f.FILM_ID " +
                        "HAVING likes_count >= 0 " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?";
                films = jdbcOperations.query(sql, this::makeFilm, genreId, year, count);
            } else if (year != null) {
                String sql = "SELECT f.FILM_ID, " +
                        "f.FILM_NAME, " +
                        "f.FILM_DESCRIPTION, " +
                        "f.FILM_RELEASE_DATE, " +
                        "f.FILM_DURATION, " +
                        "f.FILM_MPA, " +
                        "m.MPA_TITLE, " +
                        "COUNT(l.FILM_ID) as likes_count " +
                        "FROM FILMS f " +
                        "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                        "JOIN MPA m ON f.FILM_MPA = m.MPA_ID " +
                        "WHERE EXTRACT(YEAR FROM f.FILM_RELEASE_DATE) = ? " +
                        "GROUP BY f.FILM_ID " +
                        "HAVING likes_count >= 0 " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?";
                films = jdbcOperations.query(sql, this::makeFilm, year, count);
            } else {
                String sql = "SELECT f.FILM_ID, " +
                        "f.FILM_NAME, " +
                        "f.FILM_DESCRIPTION, " +
                        "f.FILM_RELEASE_DATE, " +
                        "f.FILM_DURATION, " +
                        "f.FILM_MPA, " +
                        "m.MPA_TITLE, " +
                        "COUNT(l.FILM_ID) as likes_count " +
                        "FROM FILMS f " +
                        "JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
                        "JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID " +
                        "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                        "JOIN MPA m ON f.FILM_MPA = m.MPA_ID " +
                        "WHERE g.GENRE_ID = ? " +
                        "GROUP BY f.FILM_ID " +
                        "HAVING likes_count >= 0 " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?";
                films = jdbcOperations.query(sql, this::makeFilm, genreId, count);
            }
        } else {
            String sql = "SELECT f.FILM_ID, " +
                    "f.FILM_NAME, " +
                    "f.FILM_DESCRIPTION, " +
                    "f.FILM_RELEASE_DATE, " +
                    "f.FILM_DURATION, " +
                    "f.FILM_MPA, " +
                    "m.MPA_TITLE, " +
                    "COUNT(l.FILM_ID) as likes_count " +
                    "FROM FILMS f " +
                    "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                    "JOIN MPA m ON f.FILM_MPA = m.MPA_ID " +
                    "GROUP BY f.FILM_ID " +
                    "HAVING likes_count >= 0 " +
                    "ORDER BY likes_count DESC " +
                    "LIMIT ?";
            films = jdbcOperations.query(sql, this::makeFilm, count);

        }
        return films;
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

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.FILM_ID, " +
                "f.FILM_NAME," +
                "f.FILM_DESCRIPTION, " +
                "f.FILM_RELEASE_DATE, " +
                "f.FILM_DURATION, " +
                "f.FILM_MPA, " +
                "m.MPA_TITLE " +
                "FROM PUBLIC.FILMS f " +
                "LEFT JOIN PUBLIC.LIKES l on f.FILM_ID = l.FILM_ID " +
                "JOIN PUBLIC.MPA m on F.FILM_MPA = M.MPA_ID " +
                "WHERE l.USER_ID = ? AND  l.FILM_ID IN ( " +
                "                                       SELECT FILM_ID " +
                "                                       FROM PUBLIC.LIKES " +
                "                                       WHERE USER_ID = ?) " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(l.USER_ID) DESC ";

        try {
            return jdbcOperations.query(sql, this::makeFilm, userId, friendId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Film> searchFilmForDirector(String query) {
        String sql = "SELECT f.*, m.MPA_TITLE " +
                "FROM FILMS f " +
                "LEFT JOIN FILM_DIRECTOR fd ON fd.FILM_ID = f.FILM_ID " +
                "LEFT JOIN DIRECTORS d ON d.DIRECTOR_ID = fd.DIRECTOR_ID " +
                "LEFT JOIN MPA m ON f.FILM_MPA = m.MPA_ID " +
                "WHERE LOWER(d.DIRECTOR_NAME) LIKE ?";

        return jdbcOperations.query(sql, this::makeFilm, "%" + query.toLowerCase() + "%");
    }

    @Override
    public List<Film> searchFilmForTitle(String query) {
        String sql = "SELECT f.*, m.MPA_TITLE " +
                "FROM FILMS f " +
                "LEFT JOIN MPA m ON f.FILM_MPA = m.MPA_ID " +
                "WHERE LOWER(f.FILM_NAME) LIKE ?";

        return jdbcOperations.query(sql, this::makeFilm, "%" + query.toLowerCase() + "%");
    }

    @Override
    public List<Film> searchFilmForTitleAndDirector(String query) {
        String sql = "SELECT f.*, m.MPA_TITLE " +
                "FROM FILMS f " +
                "LEFT JOIN FILM_DIRECTOR fd ON fd.FILM_ID = f.FILM_ID " +
                "LEFT JOIN DIRECTORS d ON d.DIRECTOR_ID = fd.DIRECTOR_ID " +
                "LEFT JOIN MPA m ON f.FILM_MPA = m.MPA_ID " +
                "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                "WHERE LOWER(d.DIRECTOR_NAME) LIKE ? OR LOWER(f.FILM_NAME) LIKE ? " +
                "GROUP BY f.FILM_ID, m.MPA_TITLE " +
                "ORDER BY COUNT(l.USER_ID) DESC";

        return jdbcOperations.query(sql, this::makeFilm,
                "%" + query.toLowerCase() + "%", "%" + query.toLowerCase() + "%");
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

    private Film makeFilm(ResultSet rs, int rn) throws SQLException {
        return new Film(rs.getInt("FILM_ID"),
                rs.getString("FILM_NAME"),
                rs.getString("FILM_DESCRIPTION"),
                rs.getObject("FILM_RELEASE_DATE", LocalDate.class),
                rs.getLong("FILM_DURATION"),
                new Mpa(rs.getInt("FILM_MPA"),
                        rs.getString("MPA_TITLE")),
                new LinkedHashSet<>(),
                new HashSet<>());
    }
}

