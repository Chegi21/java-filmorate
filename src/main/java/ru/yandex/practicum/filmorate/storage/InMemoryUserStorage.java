package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> usersMap = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return usersMap.values();
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return usersMap.get(userId)
                .getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        return usersMap.get(firstUserId).getFriends().stream()
                .filter(usersMap.get(secondUserId).getFriends()::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        return usersMap.get(id);
    }

    @Override
    public User create(User newUser) {
        newUser.setId(getNextId());
        usersMap.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User update(User user) {
        return usersMap.put(user.getId(), user);
    }

    @Override
    public User delete(Long id) {
        return usersMap.remove(id);
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        usersMap.get(userId).getFriends().add(friendId);
        usersMap.get(friendId).getFriends().add(userId);
    }

    @Override
    public void deleteFriends(Long userId, Long friendId) {
        usersMap.get(userId).getFriends().remove(friendId);
        usersMap.get(friendId).getFriends().remove(userId);
    }

    public boolean emailExists(String email) {
        return usersMap.values().stream().anyMatch(
                user -> user.getEmail().equalsIgnoreCase(email)
        );
    }

    private long getNextId() {
        long currentMaxId = usersMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
