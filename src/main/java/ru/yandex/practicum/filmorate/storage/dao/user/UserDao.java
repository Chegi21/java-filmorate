package ru.yandex.practicum.filmorate.storage.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserDao {
    Collection<User> getUsers();

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long firstUserId, Long secondUserId);

    User getUserById(Long userId);

    User create(User user);

    User update(User user);

    User delete(User user);

    void addLinkFriends(Long userId, Long friendId);

    void deleteLinkFriends(Long userId, Long friendId);

    void deleteAllFriends(Long userId);

}
