package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.storage.constants.LikesDbConstants.FIND_COUNT_LIKES;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerDaoTest {
    private final FilmService filmService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEach() {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

    }

    @Test
    public void testCreateFilm() {
        filmService.create(film());
        assertFalse(filmService.getFilms().isEmpty());
    }

    @Test
    public void testDescriptionIsLong() {
        Film film = film();
        film.setDescription(longString());

        assertThrows(DataIntegrityViolationException.class, () -> filmService.create(film));
    }

    @Test
    public void testUpdateFilm() {
        filmService.create(film());
        List<Film> filmList = filmService.getFilms().stream().toList();
        Film film = filmList.getFirst();

        Genre genre2 = genre();
        genre2.setId(2L);

        film.setGenres(Set.of(genre(), genre2));

        Film updateFilm = filmService.update(film);

        assertEquals(filmService.getFilmById(film.getId()).getName(),
                filmService.getFilmById(updateFilm.getId()).getName());
    }

    @Test
    public void testGetFilmById() {
        filmService.create(film());
        List<Film> filmList = filmService.getFilms().stream().toList();
        Film film = filmList.getFirst();

        assertEquals(film, filmService.getFilmById(film.getId()));
    }

    @Test
    public void testGetByIdNotExistFilm() {
        assertThrows(EmptyResultDataAccessException.class, () -> filmService.getFilmById(145L));
    }

    @Test
    public void getFilms_shouldReturnListOfFilms() {
        filmService.create(film());
        filmService.create(film());
        List<Film> filmList = filmService.getFilms().stream().toList();

        assertEquals(2, filmList.size());
    }

    @Test
    public void testGetEmptyListOfFilms() {
        assertTrue(filmService.getFilms().isEmpty());
    }

    @Test
    public void testGetPopularMovies() {
        filmService.create(film());
        filmService.create(film());
        userService.create(user());
        userService.create(friend());

        List<Film> filmList = filmService.getFilms().stream().toList();
        Film createFilm1 = filmList.get(0);
        Film createFilm2 = filmList.get(1);

        List<User> userList = userService.getUsers().stream().toList();
        User user1 = userList.get(0);
        User user2 = userList.get(1);

        filmService.addLike(createFilm1.getId(), user1.getId());
        filmService.addLike(createFilm2.getId(), user1.getId());
        filmService.addLike(createFilm2.getId(), user2.getId());

        List<Film> popularList = filmService.getPopular(3).stream().toList();
        Film popularFilm = popularList.getFirst();

        assertEquals(createFilm2, popularFilm);
    }

    @Test
    public void testDislike() {
        filmService.create(film());
        userService.create(user());

        List<Film> filmList = filmService.getFilms().stream().toList();
        Film film = filmList.getFirst();

        List<User> userList = userService.getUsers().stream().toList();
        User user = userList.getFirst();

        filmService.addLike(film.getId(), user.getId());
        Integer count = jdbcTemplate.queryForObject(FIND_COUNT_LIKES, Integer.class, film.getId());

        assertEquals(1, count);

        filmService.deleteLike(film.getId(), user.getId());
        Integer count2 = jdbcTemplate.queryForObject(FIND_COUNT_LIKES, Integer.class, film.getId());

        assertEquals(0, count2);
    }

    private Film film() {
        return Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895,12,29))
                .duration(100)
                .ratingMpa(ratingMpa())
                .build();
    }

    private User user() {
        return User.builder()
                .email("user@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980,1,1))
                .build();
    }

    private User friend() {
        return User.builder()
                .email("friend@mail.com")
                .login("loginFriend")
                .name("friend")
                .birthday(LocalDate.of(1981,1,1))
                .build();
    }

    private Genre genre() {
        return Genre.builder()
                .id(1L)
                .name("G-8")
                .build();
    }

    private RatingMpa ratingMpa() {
        return RatingMpa.builder()
                .id(1L)
                .name("G")
                .build();
    }

    private String longString() {
        return "a".repeat(201);
    }
}
