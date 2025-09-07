package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.yandex.practicum.filmorate.storage.constants.LikesDbConstants.LIKES_FILM_ID;
import static ru.yandex.practicum.filmorate.storage.constants.LikesDbConstants.LIKES_USER_ID;

public class LikeMapper implements RowMapper<Like> {
    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Like.builder()
                .filmId(rs.getLong(LIKES_FILM_ID))
                .userId(rs.getLong(LIKES_USER_ID))
                .build();
    }
}
