package ru.yandex.practicum.filmorate.repository.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
@Primary
public class DbDirectorRepository implements DirectorRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public Director create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO PUBLIC.DIRECTORS (DIRECTOR_NAME) " +
                "VALUES(?)";
        try {
            log.info("DbDirectorRepository.create: Режиссер {} начата запись в БД",
                    director);
            jdbcOperations.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"DIRECTOR_ID"});
                        ps.setString(1, director.getName());
                        return ps;
                    }, keyHolder);
            director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            log.info("DbDirectorRepository.create: Режиссер {} запись в БД успешна", director);
        } catch (Exception e) {
            String errorMessage = Objects.requireNonNull(e.getMessage());

            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE PUBLIC.DIRECTORS SET DIRECTOR_NAME = ? " +
                "WHERE DIRECTOR_ID = ?";
        log.info("DbDirectorRepository.update: Режиссер {} начато обновление в БД",
                director);
        try {
            jdbcOperations.update(sql, director.getName(), director.getId());
        } catch (Exception e) {
            String errorMessage = Objects.requireNonNull(e.getMessage());

            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
        log.info("DbDirectorRepository.update: Режиссер {} обновление в БД успешно",
                director);
        return director;
    }

    @Override
    public Optional<Director> findById(int id) {
        String sql = "SELECT DIRECTOR_ID, DIRECTOR_NAME " +
                "FROM PUBLIC.DIRECTORS " +
                "WHERE DIRECTOR_ID = ?";
        log.info("DbDirectorRepository.findById: Поиск режиссера с id =  {}.",
                id);
        try {
            return Optional.ofNullable(jdbcOperations.queryForObject(sql, new DirectorRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            log.info("Режиссер с ID = {} не найден ", id);
            throw new NotFoundException(
                    String.format("Режиссер с ID = %d не найден ", id));
        }
    }

    @Override
    public List<Director> findAll() {
        String sql = "SELECT DIRECTOR_ID, DIRECTOR_NAME FROM PUBLIC.DIRECTORS";
        log.info("DbDirectorRepository.findAll: Начат поиск всех режиссеров .");
        try {
            List<Director> directors = jdbcOperations.query(sql, new DirectorRowMapper());
            log.info("DbDirectorRepository.findAll: Найдено {} режиссеров .", directors.size());
            return directors;
        } catch (EmptyResultDataAccessException e) {
            log.info("DbDirectorRepository.findAll: Режиссеров не найдено никого.");
            return new ArrayList<>();
        }
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM PUBLIC.DIRECTORS  " +
                "WHERE DIRECTOR_ID = ?";
        log.info("DbDirectorRepository.remove: Режиссер c id = {} начато удаление из БД",
                id);
        jdbcOperations.update(sql, id);
        log.info("DbDirectorRepository.remove: Режиссер c id = {} успешно удален из БД",
                id);
    }

    @Override
    public void removeDirectorsFromFilms(int filmId) {
        String sql = "DELETE FROM PUBLIC.FILM_DIRECTOR " +
                "WHERE  FILM_ID = ?";
        log.info("DbDirectorRepository.removeDirectorsFromFilms: Начато удаление режиссеров из фильма id = {} " +
                        "начато удаление из БД", filmId);
        jdbcOperations.update(sql, filmId);
        log.info("DbDirectorRepository.removeDirectorsFromFilms: Режиссеры из фильма id = {} " +
                "успешно удалены из БД", filmId);
    }

    @Override
    public void addDirectorsToFilm(Set<Director> directors, int filmId) {
        String sql = "MERGE INTO PUBLIC.FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) " +
                "VALUES (?, ?)";
        log.info("DbDirectorRepository.addDirectorsToFilm: Начато добавление режиссеров {} в фильма id = {} ",
                directors, filmId);
        List<Object[]> batchArgs = directors
                .stream()
                .map(director -> (new Object[]{filmId, director.getId()}))
                .collect(Collectors.toList());
        jdbcOperations.batchUpdate(sql, batchArgs);
        log.info("DbDirectorRepository.addDirectorsToFilm: Добавление режиссеров {} в фильм id = {} успешно ",
                directors, filmId);
    }

    @Override
    public List<Director> findDirectorsByFilm(int filmId) {
        String sql = "SELECT fd.DIRECTOR_ID, d.DIRECTOR_NAME " +
                "FROM PUBLIC.FILM_DIRECTOR fd " +
                "JOIN PUBLIC.DIRECTORS d " +
                "ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE fd.FILM_ID = ?";
        log.info("DbDirectorRepository.findDirectorsByFilm: Начат поиск режиссеров в фильме id = {} ",
                filmId);
        try {
            List<Director> directors = jdbcOperations.query(sql, new DirectorRowMapper(), filmId);
            log.info("DbDirectorRepository.findDirectorsByFilm: Найдено {} режиссеров .", directors.size());
            return directors;
        } catch (EmptyResultDataAccessException e) {
            log.info("DbDirectorRepository.findDirectorsByFilm: Режиссеров не найдено никого.");
            return new ArrayList<>();
        }
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