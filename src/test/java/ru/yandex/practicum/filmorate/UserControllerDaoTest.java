package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerDaoTest {
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM films");
    }

    @Test
    public void testCreateUser() {
        userService.create(user());
        assertFalse(userService.getUsers().isEmpty());
    }

    @Test
    public void createUserDuplicateEmail() {
       User user = userService.create(user());
       User createDuplicate = user();
       createDuplicate.setEmail(user.getEmail());

       assertThrows(Exception.class, () -> userService.create(createDuplicate));
    }

    @Test
    public void createUserDuplicateLogin() {
        User user = userService.create(user());
        User createDuplicate = user();
        createDuplicate.setEmail(user.getLogin());

        assertThrows(Exception.class, () -> userService.create(createDuplicate));
    }

    @Test
    public void testUpdateUser() {
        User user = userService.create(user());
        List<User> createUser = userService.getUsers().stream().toList();
        User updatedUser = userService.update(createUser.getFirst());

        assertEquals(user.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void testUpdateNotExistUser() {
        User user = user();
        user.setId(1L);
        assertThrows(Exception.class, () -> userService.update(user));
    }

    @Test
    public void testGetById() {
        User user = userService.create(user());
        List<User> userList = userService.getUsers().stream().toList();
        User getUserById = userService.getUserById(userList.getFirst().getId());

        assertEquals(user.getEmail(), getUserById.getEmail());
    }

    @Test
    public void testGetByIdNotExistUser() {
        assertThrows(Exception.class, () -> userService.getUserById(1L));
    }

    @Test
    public void testAddFriend() {
        userService.create(user());
        userService.create(friend());
        List<User> userList = userService.getUsers().stream().toList();

        User getUserById = userService.getUserById(userList.get(0).getId());
        User getFriendById = userService.getUserById(userList.get(1).getId());

        userService.addFriend(getUserById.getId(), getFriendById.getId());
        userService.addFriend(getFriendById.getId(), getUserById.getId());

        assertFalse(userService.getFriends(getFriendById.getId()).isEmpty());
        assertFalse(userService.getFriends(getUserById.getId()).isEmpty());
    }

    @Test
    public void testAddFriendWithNotValidId() {
        userService.create(user());
        List<User> userList = userService.getUsers().stream().toList();
        User creteUser = userList.getFirst();

        assertThrows(Exception.class,
                () -> userService.addFriend(creteUser.getId(), creteUser.getId()));
    }

    @Test
    public void testDeleteFriend() {
        userService.create(user());
        userService.create(friend());

        List<User> userList = userService.getUsers().stream().toList();
        User createUser = userList.get(0);
        User createFriend = userList.get(1);

        userService.addFriend(createUser.getId(), createFriend.getId());
        userService.addFriend(createFriend.getId(), createUser.getId());

        assertEquals(1, userService.getFriends(createUser.getId()).size());

        userService.deleteFriend(createUser.getId(), createFriend.getId());

        assertTrue(userService.getFriends(createUser.getId()).isEmpty());
    }

    @Test
    public void testGetCommonFriends() {
        userService.create(user());
        userService.create(friend());
        userService.create(friendOfBoth());

        List<User> userList = userService.getUsers().stream().toList();
        User createUser = userList.get(0);
        User createFriend = userList.get(1);
        User createFriendOfBoth = userList.get(2);

        userService.addFriend(createUser.getId(), createFriend.getId());
        userService.addFriend(createFriend.getId(), createUser.getId());
        userService.addFriend(createUser.getId(), createFriendOfBoth.getId());
        userService.addFriend(createFriend.getId(), createFriendOfBoth.getId());

        assertTrue(userService.getCommonFriends(createUser.getId(), createFriend.getId())
                .contains(createFriendOfBoth));
    }

    @Test
    public void testGetEmptyListsOfCommonFriends() {
        userService.create(user());
        userService.create(friend());

        List<User> userList = userService.getUsers().stream().toList();
        User createUser = userList.get(0);
        User createFriend = userList.get(1);

        assertTrue(userService.getFriends(createUser.getId()).isEmpty());
        assertTrue(userService.getFriends(createFriend.getId()).isEmpty());

    }

    private User user() {
        return User.builder()
                .email("user@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();
    }

    private User friend() {
        return User.builder()
                .email("friend@mail.com")
                .login("loginFriend")
                .name("friend")
                .birthday(LocalDate.of(1981,1,1))
                .build();
    }

    private User friendOfBoth() {
        return User.builder()
                .email("common@mail.com")
                .login("login123")
                .name("common")
                .birthday(LocalDate.of(1985,1,1))
                .build();
    }


}
