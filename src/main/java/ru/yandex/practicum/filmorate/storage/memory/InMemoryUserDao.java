package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserDao implements UserDao {
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
    public User delete(User user) {
        return usersMap.remove(user.getId());
    }


    @Override
    public void addLinkFriends(Long userId, Set<Long> friendIds) {
    }

    @Override
    public void deleteLinkFriends(Long userId, Long friendId) {
        usersMap.get(userId).getFriends().remove(friendId);
        usersMap.get(friendId).getFriends().remove(userId);
    }

    @Override
    public void deleteAllFriends(Long userId) {
        usersMap.get(userId).getFriends().clear();
    }

    @Override
    public boolean emilExists(User user) {
        return false;
    }

    @Override
    public boolean loginExists(User user) {
        return false;
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
