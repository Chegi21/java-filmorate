package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.storage.constants.FriendDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.UserDbConstants.*;

@RequiredArgsConstructor
@Repository("userDaoImpl")
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getUsers() {
        List<User> users = jdbcTemplate.query(FIND_ALL_USER, new UserMapper());
        for (User user : users) {
            user.setFriends(new HashSet<>(getFriendsId(user.getId())));
        }
        return users;
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        List<User> users = jdbcTemplate.query(FIND_ALL_FRIENDS_USER, new UserMapper(), userId);
        for (User user : users) {
            user.setFriends(new HashSet<>(getFriendsId(user.getId())));
        }
        return users;
    }

    @Override
    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        List<User> users = jdbcTemplate.query(FIND_COMMON_FRIENDS, new UserMapper(), firstUserId, secondUserId);
        for (User user : users) {
            user.setFriends(new HashSet<>(getFriendsId(user.getId())));
        }
        return users;
    }

    @Override
    public User getUserById(Long userId) {
        User user = jdbcTemplate.queryForObject(FIND_USER_BY_ID, new UserMapper(), userId);
        user.setFriends(new HashSet<>(getFriendsId(user.getId())));
        return user;
    }

    @Override
    public User create(User user) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        INSERT_USER,
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);

            Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            user.setId(generatedId);

            return user;
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public User update(User user) {
        try {
            jdbcTemplate.update(
                    UPDATE_USER,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User delete(User user) {
        int row = jdbcTemplate.update(DELETE_USER, user.getId());
        if (row > 0) {
            return user;
        }
        return null;
    }

    @Override
    public boolean emailExists(String email) {
        Integer count = jdbcTemplate.queryForObject(EMILE_EXISTS, Integer.class, email);
        return count > 0;
    }

    @Override
    public void addLinkFriends(Long userId, Long friendId) {
        jdbcTemplate.update(INSERT_FRIENDS, userId, friendId);
    }

    @Override
    public void deleteLinkFriends(Long userId, Long friendId) {
        jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
    }

    @Override
    public void deleteAllFriends(Long userId) {
        jdbcTemplate.update(DELETE_ALL_FRIENDS, userId, userId);
    }

    private Collection<Long> getFriendsId(Long userId) {
        return jdbcTemplate.queryForList(FIND_FRIENDS_ID, Long.class, userId);
    }
}
