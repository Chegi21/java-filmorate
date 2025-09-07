package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmDao;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import java.time.LocalDate;
import java.util.*;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FilmControllerTest {
    private static Validator validator;
    private static UserDao userDao;
    private static FilmController controller;

    @BeforeEach
    void setUpValidator() {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        validator = factory.getValidator();
        FilmDao filmDao = new InMemoryFilmDao();
        userDao = new InMemoryUserDao();
        FilmService filmService = new FilmService(filmDao, userDao);
        controller = new FilmController(filmService);
    }

    @Test
    void testIdNotPositive() {
        Film film = createdValidFilm();
        film.setId(-1L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("отрицательным числом"));
        assertTrue(hasError);
    }

    @Test
    void testIdIsNull() {
        Film film = createdValidFilm();
        film.setId(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> controller.update(film));

        assertEquals("Ошибка валидации: передан null в качестве Id", exception.getMessage());
    }

    @Test
    void testNameIsBlank() {
        Film film = createdValidFilm();
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустым"));
        assertTrue(hasError);
    }

    @Test
    void testNameIsNull() {
        Film film = createdValidFilm();
        film.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустым"));
        assertTrue(hasError);
    }

    @Test
    void testDescriptionIsLong() {
        Film film = createdValidFilm();
        film.setDescription(longString());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("200 символов"));
        assertTrue(hasError);
    }

    @Test
    void testDescriptionIsNull() {
        Film film = createdValidFilm();
        film.setDescription(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустым"));
        assertTrue(hasError);
    }

    @Test
    void testReleaseDateBeforeMin() {
        Film film = createdValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> controller.create(film));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void testReleaseIsNull() {
        Film film = createdValidFilm();
        film.setReleaseDate(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    @Test
    void testDurationIsNegative() {
        Film film = createdValidFilm();
        film.setDuration(-5);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("положительным числом"));
        assertTrue(hasError);
    }

    @Test
    void testDurationIsNull() {
        Film film = createdValidFilm();
        film.setDuration(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    @Test
    void testFindAllFilms() {
        Film film1 = createdValidFilm();
        Film film2 = createdValidFilm();
        Film film3 = createdValidFilm();

        assertDoesNotThrow(() -> controller.create(film1));
        assertDoesNotThrow(() -> controller.create(film2));
        assertDoesNotThrow(() -> controller.create(film3));
        assertEquals(3, controller.findAll().size());
    }

    @Test
    void testNotFindAllFilms() {
        Collection<Film> filmCollection = controller.findAll();

        assertTrue(filmCollection.isEmpty());
    }

    @Test
    void testFindFilmById() {
        Film film = createdValidFilm();
        assertDoesNotThrow(() -> controller.create(film));

        List<Film> allFilms = new ArrayList<>(controller.findAll());

        Film findFilm = assertDoesNotThrow(() -> controller.getFilmById(1L));

        assertEquals(allFilms.getFirst().getId(), findFilm.getId());
        assertEquals(allFilms.getFirst().getName(), findFilm.getName());
    }

    @Test
    void testNotFindFilmById() {
        Film film = createdValidFilm();
        assertDoesNotThrow(() -> controller.create(film));

        assertNull(controller.getFilmById(2L));
    }

    @Test
    void testGetListPopularFilm() {
        Film film1 = createdValidFilm();
        film1.setName("VeryBest");

        Film film2 = createdValidFilm();
        film2.setName("Best");

        User user1 = createValidUser();
        User user2 = createValidUser();

        assertDoesNotThrow(() -> controller.create(film1));
        assertDoesNotThrow(() -> controller.create(film2));
        assertDoesNotThrow(() -> userDao.create(user1));
        assertDoesNotThrow(() -> userDao.create(user2));

        List<Film> filmList = new ArrayList<>(controller.findAll());
        Film createFilm1 = filmList.get(0);
        Film createFilm2 = filmList.get(1);

        List<User> userList = new ArrayList<>(userDao.getUsers());
        User createUser1 = userList.get(0);
        User createUser2 = userList.get(1);

        assertDoesNotThrow(() -> controller.addLike(createFilm1.getId(), createUser1.getId()));
        assertDoesNotThrow(() -> controller.addLike(createFilm1.getId(), createUser2.getId()));
        assertDoesNotThrow(() -> controller.addLike(createFilm2.getId(), createUser1.getId()));

        Collection<Film> filmCollection = assertDoesNotThrow(() -> controller.getPopular(3));
        List<Film> popularList = new ArrayList<>(filmCollection);

        assertEquals(2, popularList.size());
        assertEquals("VeryBest", popularList.get(0).getName());
        assertEquals("Best", popularList.get(1).getName());

    }

    @Test
    void testAddAndDeleteLike() {
        Film film = createdValidFilm();
        User user = createValidUser();

        assertDoesNotThrow(() -> controller.create(film));
        assertDoesNotThrow(() -> userDao.create(user));
        assertDoesNotThrow(() -> controller.addLike(1L, 1L));
        assertTrue(controller.getFilmById(1L).getLikes().size() == 1);
        assertDoesNotThrow(() -> controller.deleteLike(1L, 1L));
        assertTrue(controller.getFilmById(1L).getLikes().isEmpty());
    }

    @Test
    void testAddAndDeleteFilm() {
        Film film = createdValidFilm();

        assertDoesNotThrow(() -> controller.create(film));
        assertFalse(controller.findAll().isEmpty());
        assertDoesNotThrow(() -> controller.delete(1L));
        assertTrue(controller.findAll().isEmpty());
    }

    private Film createdValidFilm() {
        return Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(100)
                .build();
    }

    private User createValidUser() {
        return User.builder()
                .email("user@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();
    }

    private String longString() {
        return "a".repeat(201);
    }

}

