package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> usersMap = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        Collection<User> users = usersMap.values();
        log.info("Получен запрос на получение всех пользователей");

        if (users.isEmpty()) {
            log.error("Список пользователей пустой");
            throw new ValidationException("Список пользователей пустой");
        }

        log.debug("Найдено {} пользователей", users.size());
        return usersMap.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);

        User newUser = User.builder()
                .id(getNextId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(nameUser(user.getName(), user.getLogin()))
                .birthday(user.getBirthday())
                .build();

        usersMap.put(newUser.getId(), newUser);
        log.info("Пользователь успешно создан: {}", newUser);
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);

        if (newUser.getId() == null) {
            log.error("Ошибка валидации: передан null в качестве ID");
            throw new ValidationException("Ошибка валидации: передан null в качестве ID");
        }

        User oldUser = usersMap.get(newUser.getId());
        if (oldUser == null) {
            log.error("Пользователь с ID {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь не найден");
        }

        String newEmail = newUser.getEmail();
        if (!newEmail.equals(oldUser.getEmail())) {
            log.debug("Изменение email: {} → {}", oldUser.getEmail(), newEmail);
            if (emailExists(newEmail)) {
                log.error("Email {} уже используется", newEmail);
                throw new ValidationException("Эта почта уже используется");
            }
            oldUser.setEmail(newEmail);
        }

        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(nameUser(newUser.getName(), newUser.getLogin()));
        oldUser.setBirthday(newUser.getBirthday());

        usersMap.put(oldUser.getId(), oldUser);
        log.info("Пользователь успешно обновлён: {}", oldUser);
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = usersMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private String nameUser(String name, String login) {
        if (name == null || name.isBlank()) {
            log.debug("Имя пользователя пустое, используется login: {}", login);
            return login;
        }
        return name;
    }

    private boolean emailExists(String email) {
        boolean exists = usersMap.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
        log.debug("Проверка существования email '{}': {}", email, exists);
        return exists;
    }
}

