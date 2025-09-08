package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.yandex.practicum.filmorate.storage.constants.FilmGenreDbConstant.FILM_GENRE_FILM_ID;
import static ru.yandex.practicum.filmorate.storage.constants.FilmGenreDbConstant.FILM_GENRE_GENRE_ID;

public class FilmGenreMapper implements RowMapper<FilmGenre> {
    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmGenre.builder()
                .filmId(rs.getLong(FILM_GENRE_FILM_ID))
                .genreId(rs.getLong(FILM_GENRE_GENRE_ID))
                .build();
    }
}
