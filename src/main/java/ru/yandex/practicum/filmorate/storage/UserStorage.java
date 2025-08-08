package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long firstUserId, Long secondUserId);

    User create(User user);

    User update(User user);

    User getUserById(Long userId);

    User delete(Long userId);

    boolean emailExists(String email);

    void addFriends(Long userId, Long friendId);

    void deleteFriends(Long userId, Long friendId);

}
