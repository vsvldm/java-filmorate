package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserDbRepository implements UserRepository {
    private final JdbcOperations jdbcOperations;

    @Override
    public int add(User user) {
        String sql = "INSERT INTO USERS(USER_NAME, USER_LOGIN, USER_BIRTHDAY, USER_EMAIL)" +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"USER_ID"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setDate(3, Date.valueOf(user.getBirthday()));
            ps.setString(4, user.getEmail());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE USERS SET " +
                "USER_NAME = ?, USER_LOGIN = ?, USER_BIRTHDAY = ?, USER_EMAIL = ? " +
                "WHERE USER_ID = ?";

        return jdbcOperations.update(sql,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail(),
                user.getId()) > 0;
    }

    @Override
    public boolean deleteById(Integer userID) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";

        return jdbcOperations.update(sqlQuery, userID) > 0;
    }

    @Override
    public boolean deleteAll() {
        String sqlQuery = "DELETE FROM USERS";

        return jdbcOperations.update(sqlQuery) > 0;
    }

    @Override
    public User getById(int userId) {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";

        try {
            return jdbcOperations.queryForObject(sql, this::makeUser, userId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", userId));
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM USERS";

        return jdbcOperations.query(sql, this::makeUser);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("USER_ID"))
                .name(rs.getString("USER_NAME"))
                .login(rs.getString("USER_LOGIN"))
                .birthday(rs.getObject("USER_BIRTHDAY", LocalDate.class))
                .email(rs.getString("USER_EMAIL"))
                .build();
    }
}
