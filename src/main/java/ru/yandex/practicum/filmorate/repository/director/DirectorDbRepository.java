package ru.yandex.practicum.filmorate.repository.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DirectorDbRepository implements DirectorRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public Director create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO DIRECTORS (DIRECTOR_NAME) " +
                "VALUES(?)";
        try {
            log.info("DirectorDbRepository.create: Режиссер {} начата запись в БД",
                    director);
            jdbcOperations.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"DIRECTOR_ID"});
                        ps.setString(1, director.getName());
                        return ps;
                    }, keyHolder);
            director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            log.info("DirectorDbRepository.create: Режиссер {} запись в БД успешна", director);
        } catch (Exception e) {
            String errorMessage = Objects.requireNonNull(e.getMessage());

            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? " +
                "WHERE DIRECTOR_ID = ?";
        log.info("DirectorDbRepository.update: Режиссер {} начато обновление в БД",
                director);
        try {
            jdbcOperations.update(sql, director.getName(), director.getId());
        } catch (Exception e) {
            String errorMessage = Objects.requireNonNull(e.getMessage());

            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
        log.info("DirectorDbRepository.update: Режиссер {} обновление в БД успешно",
                director);
        return director;
    }

    @Override
    public Director findById(int id) {
        String sql = "SELECT DIRECTOR_ID, DIRECTOR_NAME " +
                "FROM DIRECTORS " +
                "WHERE DIRECTOR_ID = ?";
        log.info("DirectorDbRepository.findById: Поиск режиссера с id =  {}.",
                id);
        try {
            return jdbcOperations.queryForObject(sql, new DirectorRowMapper(), id);
        } catch (DataAccessException e) {
            log.info("Режиссер с ID = {} не найден ", id);
            throw new NotFoundException(
                    String.format("Режиссер с ID = %d не найден ", id));
        }
    }

    @Override
    public List<Director> findAll() {
        String sql = "SELECT DIRECTOR_ID, DIRECTOR_NAME FROM DIRECTORS";
        log.info("DirectorDbRepository.findAll: Начат поиск всех режиссеров .");
        List<Director> directors = jdbcOperations.query(sql, new DirectorRowMapper());
        log.info("DirectorDbRepository.findAll: Найдено {} режиссеров .", directors.size());
        return directors;
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM DIRECTORS  " +
                "WHERE DIRECTOR_ID = ?";
        log.info("DirectorDbRepository.remove: Режиссер c id = {} начато удаление из БД",
                id);
        jdbcOperations.update(sql, id);
        log.info("DirectorDbRepository.remove: Режиссер c id = {} успешно удален из БД",
                id);
    }

    @Override
    public void removeDirectorsFromFilms(int filmId) {
        String sql = "DELETE FROM FILM_DIRECTOR " +
                "WHERE  FILM_ID = ?";
        log.info("DirectorDbRepository.removeDirectorsFromFilms: Начато удаление режиссеров из фильма id = {} " +
                        "начато удаление из БД", filmId);
        jdbcOperations.update(sql, filmId);
        log.info("DirectorDbRepository.removeDirectorsFromFilms: Режиссеры из фильма id = {} " +
                "успешно удалены из БД", filmId);
    }

    @Override
    public void addDirectorsToFilm(Set<Director> directors, int filmId) {
        String sql = "MERGE INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) " +
                "VALUES (?, ?)";
        log.info("DirectorDbRepository.addDirectorsToFilm: Начато добавление режиссеров {} в фильма id = {} ",
                directors, filmId);
        List<Object[]> batchArgs = directors
                .stream()
                .map(director -> (new Object[]{filmId, director.getId()}))
                .collect(Collectors.toList());
        jdbcOperations.batchUpdate(sql, batchArgs);
        log.info("DirectorDbRepository.addDirectorsToFilm: Добавление режиссеров {} в фильм id = {} успешно ",
                directors, filmId);
    }

    @Override
    public List<Director> findDirectorsByFilm(int filmId) {
        String sql = "SELECT FD.DIRECTOR_ID, D.DIRECTOR_NAME " +
                "FROM FILM_DIRECTOR FD " +
                "JOIN DIRECTORS D " +
                "ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE FD.FILM_ID = ?";
        log.info("DirectorDbRepository.findDirectorsByFilm: Начат поиск режиссеров в фильме id = {} ",
                filmId);
        List<Director> directors = jdbcOperations.query(sql, new DirectorRowMapper(), filmId);
        log.info("DirectorDbRepository.findDirectorsByFilm: Найдено {} режиссеров .", directors.size());
        return directors;
    }

    private static class DirectorRowMapper implements RowMapper<Director> {
        @Override
        public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Director.builder()
                    .id(rs.getInt("DIRECTOR_ID"))
                    .name(rs.getString("DIRECTOR_NAME"))
                    .build();
        }
    }
}