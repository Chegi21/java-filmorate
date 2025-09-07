package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.yandex.practicum.filmorate.storage.constants.UserDbConstants.*;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong(USER_ID))
                .name(rs.getString(USER_NAME))
                .login(rs.getString(USER_LOGIN))
                .email(rs.getString(USER_EMAIL))
                .birthday(rs.getDate(USER_BIRTHDAY).toLocalDate())
                .build();
    }
}
