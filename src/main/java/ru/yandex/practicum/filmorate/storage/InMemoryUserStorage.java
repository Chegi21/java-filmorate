package ru.yandex.practicum.filmorate.storage;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> usersMap = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей");

        Collection<User> users = usersMap.values();

        log.info("Найдено {} пользователей", users.size());
        return usersMap.values();
    }

    @Override
    public User create(User user) {
        log.info("Получен запрос на создание пользователя: {}", user.getLogin());

        User newUser = User.builder()
                .id(getNextId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(nameUser(user.getName(), user.getLogin()))
                .birthday(user.getBirthday())
                .build();

        usersMap.put(newUser.getId(), newUser);
        log.info("Пользователь успешно создан: {}", newUser.getLogin());
        return newUser;
    }

    @Override
    public User update(User user) {
        log.info("Получен запрос на обновление пользователя: {}", user.getLogin());

        if (user.getId() == null) {
            log.warn("Ошибка валидации: передан null в качестве ID");
            throw new ValidationException("Ошибка валидации: передан null в качестве ID");
        }

        User oldUser = usersMap.get(user.getId());
        if (oldUser == null) {
            log.warn("Пользователь с ID {} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }

        String newEmail = user.getEmail();
        if (!newEmail.equals(oldUser.getEmail())) {
            log.debug("Изменение email: {} → {}", oldUser.getEmail(), newEmail);
            if (emailExists(newEmail)) {
                log.warn("Email {} уже используется", newEmail);
                throw new ValidationException("Эта почта уже используется");
            }
            oldUser.setEmail(newEmail);
        }

        oldUser.setLogin(user.getLogin());
        oldUser.setName(nameUser(user.getName(), user.getLogin()));
        oldUser.setBirthday(user.getBirthday());

        usersMap.put(oldUser.getId(), oldUser);
        log.info("Пользователь успешно обновлён: {}", oldUser.getLogin());
        return oldUser;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Запрос на получение пользователя с id = {}", id);

        User user = usersMap.get(id);
        if (user == null) {
            log.warn("Пользователь по данному id = {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }

        log.info("Пользователь с id = {} найден", id);
        return user;
    }

    @Override
    public User delete(Long id) {
        log.info("Запрос на удаление пользователя с id = {}", id);

        User user = usersMap.get(id);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }

        log.info("Пользователь с id = {} успешно удален", id);
        return usersMap.remove(id);
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
        if (StringUtils.isBlank(name)) {
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
