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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.constants.FilmDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.FilmGenreDbConstant.DELETE_FILM_GENRE;
import static ru.yandex.practicum.filmorate.storage.constants.FilmGenreDbConstant.INSERT_FILM_GENRE;
import static ru.yandex.practicum.filmorate.storage.constants.GenreDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.LikesDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.RatingMpaConstants.*;

@Slf4j
@RequiredArgsConstructor
@Repository("filmDaoImpl")
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getFilms() {
        Map<Long, Film> filmMap = new HashMap<>();

        jdbcTemplate.query(FIND_ALL_FILM, rs -> {
            Long filmId = rs.getLong(FILM_ID);
            Film film = filmMap.computeIfAbsent(filmId, id -> {
                try {
                    return Film.builder()
                            .id(id)
                            .name(rs.getString(FILM_NAME))
                            .description(rs.getString(FILM_DESCRIPTION))
                            .duration(rs.getInt(FILM_DURATION))
                            .releaseDate(rs.getDate(FILM_RELEASE_DATE).toLocalDate())
                            .genres(new HashSet<>())
                            .likes(new HashSet<>())
                            .build();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            long ratingId = rs.getLong(RATING_ID);
            String ratingName = rs.getString(RATING_NAME);
            if (ratingId > 0 && ratingName != null) {
                RatingMpa ratingMpa = RatingMpa.builder()
                        .id(ratingId)
                        .name(ratingName)
                        .build();
                film.setRatingMpa(ratingMpa);
            }

            long genreId = rs.getLong(GENRE_ID);
            String genreName = rs.getString(GENRE_NAME);
            if (genreId > 0 && genreName != null) {
                Genre genre = Genre.builder()
                        .id(genreId)
                        .name(genreName)
                        .build();
                film.getGenres().add(genre);
            }

            Long likeUserId = rs.getLong(LIKES_USER_ID);
            Long likeFilmId = rs.getLong(LIKES_FILM_ID);
            if (likeUserId > 0 && likeFilmId.equals(film.getId())) {
                Like like = Like.builder()
                        .filmId(likeFilmId)
                        .userId(likeUserId)
                        .build();
                film.getLikes().add(like);
            } else {
                log.warn("Пропущена запись лайка: filmId={}, userId={}", likeFilmId, likeUserId);
            }
        });

        return new ArrayList<>(filmMap.values());
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
