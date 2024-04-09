package ru.yandex.practicum.filmorate.repository.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
@Primary
public class DbDirectorRepository implements DirectorRepository{
    private final NamedParameterJdbcTemplate jdbcTemplate;
    @Override
    public Director create(Director director) {
        Map<String, Object> params = new HashMap<>();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO PUBLIC.DIRECTORS (DIRECTOR_NAME) " +
                "VALUES(:name)";

        params.put("name", director.getName());
        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        try {
            log.debug("DB: Director {} start create into DB", director.getName());
            jdbcTemplate.update(sql, paramSource, keyHolder);
            director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            log.debug("DB: Director {} end create into DB", director.getName());
            return director;
        } catch (RuntimeException e) {
            String errorMessage = Objects.requireNonNull(e.getMessage());

            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
    }

    @Override
    public Director update(Director director) {
        Map<String, Object> params = new HashMap<>();
        String sql = "UPDATE PUBLIC.DIRECTORS SET DIRECTOR_NAME = :name " +
                "WHERE DIRECTOR_ID = :id";
        params.put("id", director.getId());
        params.put("name", director.getName());
        try {
            jdbcTemplate.update(sql, params);
        } catch (RuntimeException e) {
            String errorMessage = Objects.requireNonNull(e.getMessage());

            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
        return director;
    }

    @Override
    public Optional<Director> getById(int id) {
        Map<String, Object> params = new HashMap<>();
        String sql = "SELECT DIRECTOR_ID, DIRECTOR_NAME " +
                "FROM PUBLIC.DIRECTORS " +
                "WHERE DIRECTOR_ID = :id";

        params.put("id", id);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, new DirectorRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT DIRECTOR_ID, DIRECTOR_NAME FROM PUBLIC.DIRECTORS";

        try {
            return jdbcTemplate.query(sql, new DirectorRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void remove(int id) {
        Map<String, Object> params = new HashMap<>();
        String sql = "DELETE FROM PUBLIC.DIRECTORS  " +
                "WHERE DIRECTOR_ID = :id";

        params.put("id", id);
        jdbcTemplate.update(sql, params);
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
