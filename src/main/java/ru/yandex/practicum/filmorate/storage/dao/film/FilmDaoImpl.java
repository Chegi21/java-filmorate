package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.mapper.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

import static ru.yandex.practicum.filmorate.storage.constants.FilmDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.FilmGenreDbConstant.DELETE_FILM_GENRE;
import static ru.yandex.practicum.filmorate.storage.constants.FilmGenreDbConstant.INSERT_FILM_GENRE;
import static ru.yandex.practicum.filmorate.storage.constants.GenreDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.LikesDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.RatingMpaConstants.FIND_ALL_RATINGS;
import static ru.yandex.practicum.filmorate.storage.constants.RatingMpaConstants.FIND_RATING;

@Slf4j
@RequiredArgsConstructor
@Repository("filmDaoImpl")
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getFilms() {
        List<Film> films =  jdbcTemplate.query(FIND_ALL_FILM, new FilmMapper());
        for (Film film : films) {
            film.setGenres(new HashSet<>(getGenresByFilmId(film.getId())));
            film.setLikes(new HashSet<>(getLikesByFilmId(film.getId())));
        }
        return films;
    }

    @Override
    public Collection<Film> getPopular(Integer count) {
        List<Film> films = jdbcTemplate.query(FIND_POPULAR_FILM, new FilmMapper(), count);
        for (Film film : films) {
            film.setGenres(new HashSet<>(getGenresByFilmId(film.getId())));
            film.setLikes(new HashSet<>(getLikesByFilmId(film.getId())));
        }
        return films;
    }

    @Override
    public Collection<Genre> getGenres() {
        return jdbcTemplate.query(FIND_ALL_GENRE, new GenreMapper());
    }

    @Override
    public Collection<RatingMpa> getRatings() {
        return jdbcTemplate.query(FIND_ALL_RATINGS, new RatingMpaMapper());
    }

    private Collection<Genre> getGenresByFilmId(Long filmId) {
        return jdbcTemplate.query(FIND_GENRE_BY_FILM_ID, new GenreMapper(), filmId);
    }

    @Override
    public Collection<Like> getLikesByFilmId(Long filmId) {
        return jdbcTemplate.query(FIND_LIKES_BY_FILM_ID, new LikeMapper(), filmId);
    }

    @Override
    public Genre getGenresById(Long genreId) {
        return jdbcTemplate.queryForObject(FIND_GENRE_BY_ID, new GenreMapper(), genreId);
    }

    @Override
    public Film getFilmById(Long filmId) {
        Film film = jdbcTemplate.queryForObject(FIND_FILM_BY_ID, new FilmMapper(), filmId);
        film.setGenres(new HashSet<>(getGenresByFilmId(film.getId())));
        film.setLikes(new HashSet<>(getLikesByFilmId(film.getId())));
        return film;
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_FILM,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setLong(5, film.getRatingMpa().getId());
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(generatedId);

        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getRatingMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public Film delete(Film film) {
        jdbcTemplate.update(DELETE_FILM, film.getId());
        return film;
    }

    @Override
    public RatingMpa getRatingMpaById(Long ratingId) {
        return jdbcTemplate.queryForObject(FIND_RATING, new RatingMpaMapper(), ratingId);
    }

    @Override
    public boolean isLiked(Long filmId, Long userId) {
        Integer count = jdbcTemplate.queryForObject(IS_LIKE, Integer.class, filmId, userId);
        return count > 0;
    }

    @Override
    public void addLikes(Long filmId, Long userId) {
        jdbcTemplate.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public void delAllLikes(Long filmId) {
        jdbcTemplate.update(DELETE_ALL_LIKE, filmId);
    }

    @Override
    public void delLike(Long filmId, Long userId) {
        jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    @Override
    public void delLinkFilmGenres(Long filmId) {
        jdbcTemplate.update(DELETE_FILM_GENRE, filmId);
    }

    @Override
    public void addLinkFilmGenres(Long filmId, Long genreId) {
        jdbcTemplate.update(INSERT_FILM_GENRE, filmId, genreId);
    }
}
