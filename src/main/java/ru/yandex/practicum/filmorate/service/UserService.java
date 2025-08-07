package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Получен запрос на добавления в друзья пользователя с id = {} к пользователю с id = {}", friendId, userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        User friend = userStorage.getUserById(friendId);
        if (friendId == null) {
            log.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Пользователь с id = {} успешно добавлен в друзья к пользователю с id = {}", friendId, userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Получен запрос на удаление из друзей пользователя с id = {} у пользователя с id = {}", friendId, userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        User friend = userStorage.getUserById(friendId);
        if (friendId == null) {
            log.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователь с id = {} успешно удален из друзей у пользователю с id = {}", friendId, userId);
    }

    public Collection<User> getFriends(Long userId) {
        log.info("Получен запрос на список друзей пользователя с id = {}", userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        Collection<User> friends =  user.getFriends().stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("У пользователь с id = {} найдено друзей в количестве {}", userId, friends.size());
        return friends;
    }

    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        log.info("Получен запрос на общих друзей между пользователями с id = {} и id = {}", firstUserId, secondUserId);

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

        Collection<User> commonFriends = firstUser.getFriends().stream()
                .filter(secondUser.getFriends()::contains)
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (commonFriends.isEmpty()) {
            log.warn("Общие друзья между пользователями с id = {} и id = {} не найдены", firstUserId, secondUserId);
            throw new NotFoundException("Общих друзей между пользователями не найдено");
        } else {
            log.info("Найдено {} общих друзей между пользователями с id = {} и id = {}",
                    commonFriends.size(), firstUserId, secondUserId);
        }

        return commonFriends;
    }

}
