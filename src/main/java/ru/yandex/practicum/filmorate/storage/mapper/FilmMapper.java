package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.yandex.practicum.filmorate.storage.constants.FilmDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.RatingMpaConstants.RATING_ID;
import static ru.yandex.practicum.filmorate.storage.constants.RatingMpaConstants.RATING_NAME;

public class FilmMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        RatingMpa ratingMpa = RatingMpa.builder()
                .id(rs.getLong(RATING_ID))
                .name(rs.getString(RATING_NAME))
                .build();

        return Film.builder()
                .id(rs.getLong(FILM_ID))
                .name(rs.getString(FILM_NAME))
                .description(rs.getString(FILM_DESCRIPTION))
                .releaseDate(rs.getDate(FILM_RELEASE_DATE).toLocalDate())
                .duration(rs.getInt(FILM_DURATION))
                .ratingMpa(ratingMpa)
                .build();
    }
}
