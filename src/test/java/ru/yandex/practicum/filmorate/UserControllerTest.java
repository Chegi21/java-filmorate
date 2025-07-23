package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNotPositiveId() {
        User user = User.builder()
                .id(-1L)
                .email("user@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("отрицательным числом"));
        assertTrue(hasError);
    }

    @Test
    void testIdIsNull() {
        UserController controller = new UserController();

        User user = User.builder()
                .id(null)
                .email("user@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Ошибка валидации: передан null в качестве ID", exception.getMessage());
    }

    @Test
    void testInvalidEmail() {
        User user = User.builder()
                .id(1L)
                .email("bademail")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("@"));
        assertTrue(hasError);
    }

    @Test
    void testEmailIsNull() {
        User user = User.builder()
                .id(1L)
                .email(null)
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    @Test
    void testEmailIsBlank() {
        User user = User.builder()
                .id(1L)
                .email(" ")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустой"));
        assertTrue(hasError);
    }

    @Test
    void testLoginWithWhitespace() {
        User user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .login("login user")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пробелов"));
        assertTrue(hasError);
    }

    @Test
    void testLoginIsBlank() {
        User user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .login(" ")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустым"));
        assertTrue(hasError);
    }

    @Test
    void testLoginIsNull() {
        User user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .login(null)
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    @Test
    void testNameIsLogin() {
        UserController controller = new UserController();

        User user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .login("login")
                .name(" ")
                .birthday(LocalDate.of(1980,1,1))
                .build();

        User createdUser = controller.create(user);
        assertEquals(createdUser.getName(), createdUser.getLogin());
    }

    @Test
    void testBirthdayInFuture() {
        User user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasBirthdayError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("в будущем"));
        assertTrue(hasBirthdayError);
    }

    @Test
    void testBirthdayIsNull() {
        User user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .login("login")
                .name("name")
                .birthday(null)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }
}
