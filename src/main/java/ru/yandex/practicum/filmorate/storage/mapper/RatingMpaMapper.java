package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.yandex.practicum.filmorate.storage.constants.RatingMpaConstants.RATING_ID;
import static ru.yandex.practicum.filmorate.storage.constants.RatingMpaConstants.RATING_NAME;

public class RatingMpaMapper implements RowMapper<RatingMpa> {
    @Override
    public RatingMpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return RatingMpa.builder()
                .id(rs.getLong(RATING_ID))
                .name(rs.getString(RATING_NAME))
                .build();
    }
}
