package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.yandex.practicum.filmorate.storage.constants.GenreDbConstants.GENRE_ID;
import static ru.yandex.practicum.filmorate.storage.constants.GenreDbConstants.GENRE_NAME;

public class GenreMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong(GENRE_ID))
                .name(rs.getString(GENRE_NAME))
                .build();
    }
}
