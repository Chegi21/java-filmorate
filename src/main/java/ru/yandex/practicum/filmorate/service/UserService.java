package ru.yandex.practicum.filmorate.service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import java.util.*;

import static ru.yandex.practicum.filmorate.storage.constants.UserDbConstants.EMILE_EXISTS;
import static ru.yandex.practicum.filmorate.storage.constants.UserDbConstants.LOGIN_EXISTS;

@Slf4j
@Service
public class UserService  {
    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("userDaoImpl") UserDao userDao) {
        this.userDao = userDao;
    }

    public Collection<User> getUsers() {
        log.info("Запрос на получение всех пользователей");

        Collection<User> users = userDao.getUsers();
        if (users.isEmpty()) log.warn("Список друзей пустой");

        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    public Collection<User> getFriends(Long userId) {
        log.info("Запрос на список друзей пользователя с id = {}", userId);

        User user = userDao.getUserById(userId);
        if (user == null)  log.warn("Пользователь с id = {} не найден", userId);

        Collection<User> friends =  userDao.getFriends(userId);
        if (friends.isEmpty()) log.warn("Список друзей пустой");

        log.info("У пользователь с id = {} найдено друзей в количестве {}", userId, friends.size());
        return friends;
    }

    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        log.info("Запрос на общих друзей между пользователями с id = {} и id = {}", firstUserId, secondUserId);

        User firstUser = userDao.getUserById(firstUserId);
        if (firstUser == null)  log.warn("Пользователь с id = {} не найден", firstUserId);

        User secondUser = userDao.getUserById(secondUserId);
        if (secondUser == null)  log.warn("Пользователь с id = {} не найден", secondUserId);

        Collection<User> commonFriends = userDao.getCommonFriends(firstUserId, secondUserId);
        if (commonFriends.isEmpty()) {
            log.warn("Общие друзья между пользователями с id = {} и id = {} не найдены", firstUserId, secondUserId);
        } else {
            log.info("Найдено {} общих друзей между пользователями с id = {} и id = {}",
                    commonFriends.size(), firstUserId, secondUserId);
        }

        return commonFriends;
    }

    public User getUserById(Long userId) {
        log.info("Запрос пользователя c id = {}", userId);

        User user = userDao.getUserById(userId);
        if (user == null) log.warn("Пользователь с id = {} не найден", userId);

        log.info("Пользователь с id = {} найден", userId);
        return user;
    }

    public User create(User user) {
        log.info("Запрос на создание пользователя: {}", user.getName());

        if (userDao.emilExists(user)) {
            log.warn("Email {} уже существует", user.getEmail());
            throw new ValidationException("Email уже существует");
        }

        if (userDao.loginExists(user)) {
            log.warn("Login {} уже существует", user.getEmail());
            throw new ValidationException("Login уже существует");
        }

        User newUser = User.builder()
                .email(user.getEmail())
                .login(user.getLogin())
                .name(nameUser(user.getName(), user.getLogin()))
                .birthday(user.getBirthday())
                .build();

        User createUser = userDao.create(newUser);
        if (createUser == null) log.warn("Ошибка DAO при создание пользователя {}.", user.getName());

        log.info("Пользователь успешно создан: {}", newUser.getName());
        return newUser;
    }

    public User update(User newUser) {
        log.info("Запрос на обновление пользователя c id = {}", newUser.getId());

        if (newUser.getId() == null) {
            log.warn("Ошибка валидации: передан null в качестве ID");
            throw new ValidationException("Ошибка валидации: передан null в качестве ID");
        }

        User oldUser = userDao.getUserById(newUser.getId());
        if (oldUser == null) log.warn("Пользователь с ID {} не найден", newUser.getId());

        oldUser.setId(newUser.getId());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(nameUser(newUser.getName(), newUser.getLogin()));
        oldUser.setBirthday(newUser.getBirthday());
        oldUser.setEmail(newUser.getEmail());

        User updateUser = userDao.update(oldUser);
        if (updateUser == null) log.warn("Ошибка DAO при обновлении пользователя с id = {}", newUser.getId());

        userDao.deleteAllFriends(oldUser.getId());
        if (newUser.getFriends() == null || newUser.getFriends().isEmpty()) {
            updateUser.setFriends(new HashSet<>());
        } else {
            newUser.getFriends().forEach(friendId -> {
                userDao.addLinkFriends(updateUser.getId(), friendId);
            });
            updateUser.setFriends(newUser.getFriends());
        }

        log.info("Пользователь с id = {} успешно обновлён", updateUser.getId());
        return updateUser;
    }

    public User delete(Long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);

        User user = userDao.getUserById(userId);
        if (user == null) log.warn("Пользователь с id = {} не найден", userId);

        User deleteUser = userDao.delete(user);
        if (deleteUser == null) log.warn("Ошибка DAO при удаление пользователя с id = {}", user.getId());

        log.info("Пользователь с id = {} успешно удален", userId);
        return deleteUser;
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Запрос на добавления пользователя с id = {} в друзья к пользователю с id = {}", friendId, userId);

        User user = userDao.getUserById(userId);
        if (user == null)  log.warn("Пользователь с id = {} не найден", userId);

        User friend = userDao.getUserById(friendId);
        if (friend == null) log.warn("Пользователь с id = {} не найден", friendId);

        if (user.getId().equals(friend.getId())) {
            log.warn("Добавление в друзья пользователей с одинаковым id не возможно");
            throw new ValidationException("Добавление в друзья пользователей с одинаковым id не возможно");
        }
        userDao.addLinkFriends(userId, friendId);

        log.info("Пользователь с id = {} успешно добавлен в друзья к пользователю с id = {}", friendId, userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Запрос на удаление из друзей пользователя с id = {} у пользователя с id = {}", friendId, userId);

        User user = userDao.getUserById(userId);
        if (user == null) log.warn("Пользователь с id = {} не найден", userId);

        User friend = userDao.getUserById(friendId);
        if (friend == null) log.warn("Пользователь с id = {} не найден", friendId);

        if (user.getId().equals(friend.getId())) {
            log.warn("Удаление друзей с одинаковым id пользователя не возможно");
            throw new ValidationException("Удаление друзей с одинаковым id пользователя не возможно");
        }
        userDao.deleteLinkFriends(userId, friendId);

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