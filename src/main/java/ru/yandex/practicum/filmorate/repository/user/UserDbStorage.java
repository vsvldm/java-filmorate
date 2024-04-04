package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage{
    private final JdbcOperations jdbcOperations;

    @Override
    public void add(User user) {
        String sql = "insert into USERS(USER_NAME, USER_LOGIN, USER_BIRTHDAY, USER_EMAIL)" +
                "values (?, ?, ?, ?)";

        jdbcOperations.update(sql,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail());
    }

    @Override
    public boolean update(User user) {
        String sql = "update USERS set " +
                "USER_NAME = ?, USER_LOGIN = ?, USER_BIRTHDAY = ?, USER_EMAIL = ? " +
                "where USER_ID = ?";

        return jdbcOperations.update(sql,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail(),
                user.getId()) > 0;
    }

    @Override
    public void remove(int userId) {
        String sql = "delete from USERS where USER_ID = ?";

        jdbcOperations.update(sql, userId);
    }

    @Override
    public User getById(int userId) {
        String sql = "select * from USERS where USER_ID = ?";

        try {
            return jdbcOperations.queryForObject(sql, this::makeUser, userId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Пользователя с id = %d не существует.", userId));
        }
    }

    @Override
    public Collection<User> values() {
        String sql = "select * from USERS";

        return jdbcOperations.query(sql, this::makeUser);
    }

    @Override
    public User getLast() {
        String sqlQuery = "select * from USERS order by USER_ID desc limit 1";

        try {
            return jdbcOperations.queryForObject(sqlQuery,this::makeUser);
        } catch (DataAccessException e) {
            throw new NotFoundException("Список пользователей пуст.");
        }
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(resultSet.getInt("USER_ID"),
                resultSet.getString("USER_NAME"),
                resultSet.getString("USER_LOGIN"),
                resultSet.getObject("USER_BIRTHDAY", LocalDate.class),
                resultSet.getString("USER_EMAIL"));
    }
}
