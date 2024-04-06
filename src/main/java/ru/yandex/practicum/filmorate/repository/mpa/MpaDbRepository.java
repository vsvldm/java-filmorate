package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class MpaDbRepository implements MpaRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public Mpa getById(int mpaId) {
        String sql = "select * from MPA where MPA_ID = ?";

        try {
            return jdbcOperations.queryForObject(sql, this::makeMpa, mpaId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Рейтинг с id = %d не найден.", mpaId));
        }
    }

    @Override
    public Collection<Mpa> getMpas() {
        String sql = "select * from MPA";

        return jdbcOperations.query(sql, this::makeMpa);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("MPA_ID"),
                rs.getString("MPA_TITLE"));
    }
}
