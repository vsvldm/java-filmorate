package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.friend.InMemoryFriendStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void ifUserNameIsEmptyThenNameIsLoginValidateTest() {
        User user = new User("Login",
                LocalDate.of(1990, 1, 1),
                "nameorlogin@example.com");
        UserService userService = new UserServiceImpl(new InMemoryUserStorage(), new InMemoryFriendStorage());
        userService.createUser(user);
        assertNotNull(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void userLoginNotBeEmptyTest() {
        User user = new User(null,
                LocalDate.of(1990, 1, 1),
                "nameorlogin@example.com");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    public void userBirthdayNotBeAFutureTest() {
        User user = new User("Login",
                LocalDate.of(2100, 1, 1),
                "nameorlogin@example.com");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    public void userEmailNotBeEmptyTest() {
        User user = new User("Login",
                LocalDate.of(1990, 1, 1),
                null);
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    public void userEmailCannotBeWithoutASpecialCharacterTest() {
        User user = new User("Login",
                LocalDate.of(2100, 1, 1),
                "InvalidEmail");
        assertTrue(validator.validate(user)
                .stream()
                .anyMatch(v -> v.getMessage().equals("Email должен содержать специальный символ \"@\".")));
    }
}