package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
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
public class FilmDbRepository implements FilmRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public int add(Film film) {
        String sql = "INSERT INTO FILMS(FILM_NAME," +
                    " FILM_DESCRIPTION," +
                    " FILM_RELEASE_DATE," +
                    " FILM_DURATION," +
                    " FILM_MPA)" +
                "VALUES (?, ?, ?, ?, ?)";
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
        String sql = "UPDATE FILMS SET " +
                "FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?, FILM_DURATION = ?, FILM_MPA = ?" +
                "WHERE FILM_ID = ?";

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
        String sql = "SELECT F.FILM_ID," +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION," +
                "F.FILM_RELEASE_DATE," +
                "F.FILM_DURATION," +
                "F.FILM_MPA," +
                "M.MPA_TITLE " +
                "FROM FILMS F " +
                "JOIN MPA M ON F.FILM_MPA = M.MPA_ID " +
                "WHERE F.FILM_ID = ?";
        try {
            return jdbcOperations.queryForObject(sql, this::makeFilm, filmId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Филма с id = %d не существует.", filmId));
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT F.FILM_ID," +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION," +
                "F.FILM_RELEASE_DATE," +
                "F.FILM_DURATION," +
                "F.FILM_MPA," +
                "M.MPA_TITLE " +
                "FROM FILMS F " +
                "JOIN MPA M ON F.FILM_MPA = M.MPA_ID";

        return jdbcOperations.query(sql, this::makeFilm);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = "SELECT F.FILM_ID, " +
                "F.FILM_NAME, " +
                "F.FILM_DESCRIPTION, " +
                "F.FILM_RELEASE_DATE, " +
                "F.FILM_DURATION, " +
                "F.FILM_MPA, " +
                "M.MPA_TITLE, " +
                "COUNT(L.FILM_ID) AS likes_count " +
                "FROM FILMS F " +
                "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                "JOIN MPA M ON F.FILM_MPA = M.MPA_ID " +
                "GROUP BY F.FILM_ID " +
                "HAVING likes_count >= 0 " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcOperations.query(sql, this::makeFilm, count);
    }

    @Override
    public List<Film> findFilmsByDirectorSortByYear(int directorId) {
        String sql = "SELECT F.FILM_ID, " +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION, " +
                "F.FILM_RELEASE_DATE, " +
                "F.FILM_DURATION, " +
                "F.FILM_MPA, " +
                "M.MPA_TITLE " +
                "FROM PUBLIC.FILM_DIRECTOR FD " +
                "JOIN PUBLIC.FILMS F ON FD.FILM_ID = F.FILM_ID " +
                "JOIN PUBLIC.MPA M on F.FILM_MPA = M.MPA_ID " +
                "WHERE FD.DIRECTOR_ID = ? " +
                "ORDER BY EXTRACT(YEAR FROM F.FILM_RELEASE_DATE)";
        try {
            return jdbcOperations.query(sql, this::makeFilm, directorId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Film> findFilmsByDirectorSortByLikes(int directorId) {
        String sql = "SELECT F.FILM_ID, " +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION, " +
                "F.FILM_RELEASE_DATE, " +
                "F.FILM_DURATION, " +
                "F.FILM_MPA, " +
                "M.MPA_TITLE " +
                "FROM PUBLIC.FILM_DIRECTOR FD " +
                "JOIN PUBLIC.FILMS F ON FD.FILM_ID = F.FILM_ID " +
                "LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                "JOIN PUBLIC.MPA M on F.FILM_MPA = M.MPA_ID " +
                "WHERE FD.DIRECTOR_ID = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.USER_ID)";
        try {
            return jdbcOperations.query(sql, this::makeFilm, directorId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        String sql = "SELECT F.FILM_ID, " +
                "F.FILM_NAME," +
                "F.FILM_DESCRIPTION, " +
                "F.FILM_RELEASE_DATE, " +
                "F.FILM_DURATION, " +
                "F.FILM_MPA, " +
                "M.MPA_TITLE " +
                "FROM FILMS F " +
                "LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID " +
                "JOIN PUBLIC.MPA M on F.FILM_MPA = M.MPA_ID " +
                "WHERE L.USER_ID = ? AND L.FILM_ID IN ( " +
                "                                       SELECT FILM_ID " +
                "                                       FROM LIKES " +
                "                                       WHERE USER_ID = ?) " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.USER_ID) DESC ";

            return jdbcOperations.query(sql, this::makeFilm, userId, friendId);
    }

    @Override
    public List<Film> searchFilmForDirector(String query) {
        String sql = "SELECT F.*, M.MPA_TITLE " +
                "FROM FILMS F " +
                "LEFT JOIN FILM_DIRECTOR FD ON FD.FILM_ID = F.FILM_ID " +
                "LEFT JOIN DIRECTORS D ON D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "LEFT JOIN MPA M ON F.FILM_MPA = M.MPA_ID " +
                "WHERE LOWER(D.DIRECTOR_NAME) LIKE ?";

        return jdbcOperations.query(sql, this::makeFilm, "%" + query.toLowerCase() + "%");
    }

    @Override
    public List<Film> searchFilmForTitle(String query) {
        String sql = "SELECT F.*, M.MPA_TITLE " +
                "FROM FILMS F " +
                "LEFT JOIN MPA M ON F.FILM_MPA = M.MPA_ID " +
                "WHERE LOWER(F.FILM_NAME) LIKE ?";

        return jdbcOperations.query(sql, this::makeFilm, "%" + query.toLowerCase() + "%");
    }

    @Override
    public List<Film> searchFilmForTitleAndDirector(String query) {
        String sql = "SELECT F.*, M.MPA_TITLE " +
                "FROM FILMS F " +
                "LEFT JOIN FILM_DIRECTOR FD ON FD.FILM_ID = F.FILM_ID " +
                "LEFT JOIN DIRECTORS D ON D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "LEFT JOIN MPA M ON F.FILM_MPA = M.MPA_ID " +
                "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                "WHERE LOWER(D.DIRECTOR_NAME) LIKE ? OR LOWER(F.FILM_NAME) LIKE ? " +
                "GROUP BY F.FILM_ID, M.MPA_TITLE " +
                "ORDER BY COUNT(L.USER_ID) DESC";

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
                "LIMIT 3) " +
                "AND FILM_ID NOT IN (" +
                "SELECT FILM_ID FROM LIKES " +
                "WHERE USER_ID = ?))";

        return jdbcOperations.query(sql, this::makeFilm, id,id,id);
    }

    @Override
    public Collection<Film> getPopularFilmsByYearAndGenres(int count, Integer genreId, Integer year) {
        String sql = "SELECT F.FILM_ID, " +
                "F.FILM_NAME, " +
                "F.FILM_DESCRIPTION, " +
                "F.FILM_RELEASE_DATE, " +
                "F.FILM_DURATION, " +
                "F.FILM_MPA, " +
                "M.MPA_TITLE, " +
                "COUNT(L.FILM_ID) AS likes_count " +
                "FROM FILMS F " +
                "JOIN FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID " +
                "JOIN GENRES G ON FG.GENRE_ID = G.GENRE_ID " +
                "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                "JOIN MPA M ON F.FILM_MPA = M.MPA_ID " +
                "WHERE ((G.GENRE_ID = ? OR ? IS NULL) " +
                "AND (EXTRACT(YEAR FROM F.FILM_RELEASE_DATE) = ? OR ? IS NULL)) " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcOperations.query(sql, this::makeFilm, genreId, genreId, year, year, count);
    }

    private Film makeFilm(ResultSet rs, int rn) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("FILM_DESCRIPTION"))
                .releaseDate(rs.getObject("FILM_RELEASE_DATE", LocalDate.class))
                .duration(rs.getLong("FILM_DURATION"))
                .mpa(new Mpa(rs.getInt("FILM_MPA"),
                        rs.getString("MPA_TITLE")))
                .build();
    }
}

