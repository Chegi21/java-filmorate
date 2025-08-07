package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static Validator validator;
    private final UserStorage userStorage = new InMemoryUserStorage();

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNotPositiveId() {
        User user = createValidUser();
        user.setId(-1L);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("отрицательным числом"));
        assertTrue(hasError);
    }

    @Test
    void testIdIsNull() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        User user = createValidUser();
        user.setId(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Ошибка валидации: передан null в качестве ID", exception.getMessage());
    }

    @Test
    void testInvalidEmail() {
        User user = createValidUser();
        user.setEmail("bademail");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("@"));
        assertTrue(hasError);
    }

    @Test
    void testEmailIsBlank() {
        User user = createValidUser();
        user.setEmail(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустой"));
        assertTrue(hasError);
    }

    @Test
    void testEmailIsNull() {
        User user = createValidUser();
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустой"));
        assertTrue(hasError);
    }

    @Test
    void testLoginWithWhitespace() {
        User user = createValidUser();
        user.setLogin("login user");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пробелов"));
        assertTrue(hasError);
    }

    @Test
    void testLoginIsBlank() {
        User user = createValidUser();
        user.setLogin(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустым"));
        assertTrue(hasError);
    }

    @Test
    void testLoginIsNull() {
        User user = createValidUser();
        user.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пустым"));
        assertTrue(hasError);
    }

    @Test
    void testNameIsLogin() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        User user = createValidUser();
        user.setName(" ");

        User createdUser = assertDoesNotThrow(() -> controller.create(user));
        assertEquals(createdUser.getName(), createdUser.getLogin());
    }

    @Test
    void testBirthdayInFuture() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasBirthdayError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("в будущем"));
        assertTrue(hasBirthdayError);
    }

    @Test
    void testBirthdayIsNull() {
        User user = createValidUser();
        user.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("null"));
        assertTrue(hasError);
    }

    @Test
    void testFindAllUsers() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        User user1 = createValidUser();
        User user2 = createValidUser();
        User user3 = createValidUser();

        assertDoesNotThrow(() -> controller.create(user1));
        assertDoesNotThrow(() -> controller.create(user2));
        assertDoesNotThrow(() -> controller.create(user3));
        assertEquals(3, controller.findAll().size());
    }

    @Test
    void testNotFindAllUsers() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        Collection<User> userCollection = controller.findAll();

        assertTrue(userCollection.isEmpty());
    }

    @Test
    void testFindUserById() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        User user = createValidUser();
        assertDoesNotThrow(() -> controller.create(user));

        List<User> allFilms = new ArrayList<>(controller.findAll());

        User findUser = assertDoesNotThrow(() -> controller.getUserById(1L));

        assertEquals(allFilms.getFirst().getId(), findUser.getId());
        assertEquals(allFilms.getFirst().getName(), findUser.getName());
    }

    @Test
    void testNotFindUserById() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        User user = createValidUser();
        assertDoesNotThrow(() -> controller.create(user));

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> controller.getUserById(2L)
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testAddAndDeleteUser() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        User user = createValidUser();

        assertDoesNotThrow(() -> controller.create(user));
        assertFalse(controller.findAll().isEmpty());
        assertDoesNotThrow(() -> controller.delete(1L));
        assertTrue(controller.findAll().isEmpty());
    }

    @Test
    void testAddDeleteAndGetFriends() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        User user1 = createValidUser();
        User user2 = createValidUser();

        assertDoesNotThrow(() -> controller.create(user1));
        assertDoesNotThrow(() -> controller.create(user2));

        List<User> userList = new ArrayList<>(controller.findAll());
        User createUser1 = userList.get(0);
        User createUser2 = userList.get(1);

        assertDoesNotThrow(() -> controller.addFriend(createUser1.getId(), createUser2.getId()));
        assertEquals(createUser2, controller.getFriends(createUser1.getId()).iterator().next());

        assertDoesNotThrow(() -> controller.deleteFriend(createUser2.getId(), createUser1.getId()));
        assertTrue(createUser1.getFriends().isEmpty());
        assertTrue(createUser2.getFriends().isEmpty());
    }

    @Test
    void testGetCommonFriends() {
        UserService userService = new UserService(userStorage);
        UserController controller = new UserController(userStorage, userService);

        User user1 = createValidUser();
        User user2 = createValidUser();
        User user3 = createValidUser();

        assertDoesNotThrow(() -> controller.create(user1));
        assertDoesNotThrow(() -> controller.create(user2));
        assertDoesNotThrow(() -> controller.create(user3));

        List<User> userList = new ArrayList<>(controller.findAll());
        User creteUser1 = userList.get(0);
        User creteUser2 = userList.get(1);
        User createUser3 = userList.get(2);

        assertDoesNotThrow(() -> controller.addFriend(creteUser1.getId(), createUser3.getId()));
        assertDoesNotThrow(() -> controller.addFriend(creteUser2.getId(), createUser3.getId()));

        Collection<User> friendsCollection = assertDoesNotThrow(() -> controller.getCommonFriends(creteUser1.getId(), creteUser2.getId()));
        List<User> commonFriends = new ArrayList<>(friendsCollection);

        assertEquals(1, commonFriends.size());
        assertEquals(createUser3.getId(), commonFriends.getFirst().getId());
    }

    private User createValidUser() {
        return User.builder()
                .email("user@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();
    }
}
