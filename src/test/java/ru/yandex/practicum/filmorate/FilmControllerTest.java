package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testIdNotPositive() {
        Film film = Film.builder()
                .id(-1L)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("отрицательным числом"));
        assertTrue(hasError);
    }

    @Test
    void testIdIsNull() {
        FilmController controller = new FilmController();

        Film film = Film.builder()
                .id(null)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(100)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> controller.update(film));

        assertEquals("Ошибка валидации: передан null в качестве ID", exception.getMessage());
    }

    @Test
    void testNameIsBlank() {
        Film film = Film.builder()
                .id(1L)
                .name(" ")
                .description("description")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустым"));
        assertTrue(hasError);
    }

    @Test
    void testNameIsNull() {
        Film film = Film.builder()
                .id(1L)
                .name(null)
                .description("description")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    @Test
    void testDescriptionIsLong() {
        Film film = Film.builder()
                .id(1L)
                .name("name")
                .description(longString())
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("200 символов"));
        assertTrue(hasError);
    }

    @Test
    void testDescriptionIsNull() {
        Film film = Film.builder()
                .id(1L)
                .name("name")
                .description(null)
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    @Test
    void testReleaseDateBeforeMin() {
        FilmController controller = new FilmController();

        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(120)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> controller.create(film));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void testReleaseIsNull() {
        Film film = Film.builder()
                .id(1L)
                .name("name")
                .description("description")
                .releaseDate(null)
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    @Test
    void testDurationIsNegative() {
        Film film = Film.builder()
                .id(1L)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(-5)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("положительным числом"));
        assertTrue(hasError);
    }

    @Test
    void testDurationIsNull() {
        Film film = Film.builder()
                .id(1L)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(null)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    private String longString () {
        return "a".repeat(201);
    }

}

