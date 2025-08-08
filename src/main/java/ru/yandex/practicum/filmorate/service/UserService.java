package ru.yandex.practicum.filmorate.service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        log.info("Запрос на получение всех пользователей");

        Collection<User> users = userStorage.getUsers();

        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    public Collection<User> getFriends(Long userId) {
        log.info("Запрос на список друзей пользователя с id = {}", userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        Collection<User> friends =  userStorage.getFriends(userId);

        log.info("У пользователь с id = {} найдено друзей в количестве {}", userId, friends.size());
        return friends;
    }

    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        log.info("Запрос на общих друзей между пользователями с id = {} и id = {}", firstUserId, secondUserId);

        User firstUser = userStorage.getUserById(firstUserId);
        if (firstUser == null) {
            log.warn("Пользователь с id = {} не найден", firstUserId);
            throw new NotFoundException("Пользователь с id = " + firstUserId + " не найден");
        }

        User secondUser = userStorage.getUserById(secondUserId);
        if (secondUser == null) {
            log.warn("Пользователь с id = {} не найден", secondUserId);
            throw new NotFoundException("Пользователь с id = " + secondUserId + " не найден");
        }

        Collection<User> commonFriends = userStorage.getCommonFriends(firstUserId, secondUserId);

        if (commonFriends.isEmpty()) {
            log.warn("Общие друзья между пользователями с id = {} и id = {} не найдены", firstUserId, secondUserId);
            throw new NotFoundException("Общих друзей между пользователями не найдено");
        } else {
            log.info("Найдено {} общих друзей между пользователями с id = {} и id = {}",
                    commonFriends.size(), firstUserId, secondUserId);
        }

        return commonFriends;
    }

    public User getUserById(Long userId) {
        log.info("Запрос пользователя c id = {}", userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        log.info("Пользователь с id = {} найден", userId);
        return user;
    }

    public User create(User user) {
        log.info("Запрос на создание пользователя: {}", user.getName());

        User newUser = User.builder()
                .email(user.getEmail())
                .login(user.getLogin())
                .name(nameUser(user.getName(), user.getLogin()))
                .birthday(user.getBirthday())
                .build();

        userStorage.create(newUser);

        log.info("Пользователь успешно создан: {}", newUser.getName());
        return newUser;
    }

    public User update(User newUser) {
        log.info("Запрос на обновление пользователя c id = {}", newUser.getId());

        if (newUser.getId() == null) {
            log.warn("Ошибка валидации: передан null в качестве ID");
            throw new ValidationException("Ошибка валидации: передан null в качестве ID");
        }

        User oldUser = userStorage.getUserById(newUser.getId());
        if (oldUser == null) {
            log.warn("Пользователь с ID {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь не найден");
        }

        boolean emailExists = userStorage.emailExists(newUser.getEmail());
        if (emailExists) {
            log.warn("Email {} уже используется", newUser.getEmail());
            throw new ValidationException("Эта почта уже используется");
        }

        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(nameUser(newUser.getName(), newUser.getLogin()));
        oldUser.setBirthday(newUser.getBirthday());
        oldUser.setEmail(newUser.getEmail());

        if (newUser.getFriends() == null) {
            oldUser.setFriends(new HashSet<>());
        } else {
            oldUser.setFriends(newUser.getFriends());
        }

        User updateUser = userStorage.update(oldUser);

        log.info("Пользователь с id = {} успешно обновлён", updateUser.getId());
        return oldUser;
    }

    public User delete(Long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        User deleteUser = userStorage.delete(userId);

        log.info("Пользователь с id = {} успешно удален", userId);
        return deleteUser;
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Запрос на добавления пользователя с id = {} в друзья к пользователю с id = {}", friendId, userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        userStorage.addFriends(userId, friendId);

        log.info("Пользователь с id = {} успешно добавлен в друзья к пользователю с id = {}", friendId, userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Запрос на удаление из друзей пользователя с id = {} у пользователя с id = {}", friendId, userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        userStorage.deleteFriends(userId, friendId);

        log.info("Пользователь с id = {} успешно удален из друзей у пользователю с id = {}", friendId, userId);
    }

    private String nameUser(String name, String login) {
        if (StringUtils.isBlank(name)) {
            log.debug("Имя пользователя пустое, используется login: {}", login);
            return login;
        }
        return name;
    }
}