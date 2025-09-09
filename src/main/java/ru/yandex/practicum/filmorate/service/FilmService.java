package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmDao filmDao;
    private final UserDao userDao;
    private static final LocalDate MIN_DATE_RELEASE = LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);

    @Autowired
    public FilmService(@Qualifier("filmDaoImpl") FilmDao filmDao, @Qualifier("userDaoImpl") UserDao userDao) {
        this.filmDao = filmDao;
        this.userDao = userDao;
    }

    public Collection<Film> getFilms() {
        log.info("Запрос на получение всех фильмов");

        Collection<Film> films = filmDao.getFilms();
        if (films.isEmpty()) log.warn("Список фильмов пустой");

        log.info("Найдено {} фильмов", films.size());
        return films;
    }

    public Collection<Film> getPopular(Integer count) {
        log.info("Получен запрос на список популярных фильмов");
        if (count < 1) {
            throw new ValidationException("Количество фильмов для вывода не должно быть меньше 1");
        }

        Collection<Film> topFilms = filmDao.getPopular(count);
        if (topFilms.isEmpty()) log.warn("Список популярных фильмов пустой");

        log.info("Сформирован список из {} популярных фильмов", topFilms.size());
        return topFilms;
    }

    public Collection<Genre> getGenres() {
        log.info("Получен запрос на список всех жанров");

        Collection<Genre> genres = filmDao.getGenres();
        if (genres.isEmpty()) log.warn("Список жанров пустой");

        log.info("Найдено {} жанров", genres.size());
        return genres;
    }

    public Collection<RatingMpa> getRatings() {
        log.info("Запрос на получение всех рейтингов");

        Collection<RatingMpa> ratings = filmDao.getRatings();
        if (ratings.isEmpty()) log.warn("Список рейтингов пустой");

        log.info("Найдено {} рейтингов", ratings.size());
        return ratings;
    }

    public Genre getGenreById(Long genreId) {
        log.info("Запрос жанра с id = {}", genreId);

        Genre genre = filmDao.getGenresById(genreId);

        log.info("Жанр с id = {} найден", genreId);
        return genre;
    }

    public RatingMpa getRatingById(Long ratingId) {
        log.info("Запрос на рейтинг с id = {}", ratingId);

        RatingMpa rating = filmDao.getRatingMpaById(ratingId);

        log.info("Рейтинг с id = {} успешно найден", ratingId);
        return rating;
    }

    public Film getFilmById(Long filmId) {
        log.info("Запрос фильма c id = {}", filmId);

        Film film = filmDao.getFilmById(filmId);

        log.info("Фильм с id = {} найден", filmId);
        return film;
    }

    public Film create(Film film) {
        log.info("Запрос на создание фильма {}", film.getName());

        if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE)) {
            log.warn("Попытка создать фильм с недопустимой датой релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getRatingMpa() == null) {
            film.setRatingMpa(customRatingMpa());
        } else {
            RatingMpa ratingMpa = filmDao.getRatingMpaById(film.getRatingMpa().getId());
            film.setRatingMpa(ratingMpa);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> sortedGenres = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                Genre findGenre = filmDao.getGenresById(genre.getId());
                sortedGenres.add(findGenre);
            }
            sortedGenres.sort(Comparator.comparingLong(Genre::getId));
            film.setGenres(new LinkedHashSet<>(sortedGenres));
        } else {
            film.setGenres(new HashSet<>());
        }

        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            film.setLikes(new HashSet<>());
        }

        Film newFilm = Film.builder()
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .ratingMpa(film.getRatingMpa())
                .build();

        Film createFilm = filmDao.create(newFilm);

        for (Genre genre : film.getGenres()) {
            filmDao.addLinkFilmGenres(createFilm.getId(), genre.getId());
        }
        createFilm.setGenres(film.getGenres());

        for (Like like : film.getLikes()) {
            filmDao.addLikes(createFilm.getId(), like.getUserId());
        }
        createFilm.setLikes(film.getLikes());



        log.info("Фильм {} успешно создан", createFilm.getName());
        return createFilm;
    }

    public Film update(Film newFilm) {
        log.info("Запрос на обновление фильма c id = {}", newFilm.getId());

        if (newFilm.getId() == null) {
            log.warn("Ошибка валидации: передан null в качестве Id");
            throw new ValidationException("Ошибка валидации: передан null в качестве Id");
        }

        if (newFilm.getReleaseDate().isBefore(MIN_DATE_RELEASE)) {
            log.warn("Попытка обновить фильм с недопустимой датой релиза: {}", newFilm.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (newFilm.getRatingMpa() == null) {
            newFilm.setRatingMpa(customRatingMpa());
        } else {
            RatingMpa ratingMpa = filmDao.getRatingMpaById(newFilm.getRatingMpa().getId());
            newFilm.setRatingMpa(ratingMpa);
        }

        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            List<Genre> sortedGenres = new ArrayList<>();
            for (Genre genre : newFilm.getGenres()) {
                Genre findGenre = filmDao.getGenresById(genre.getId());
                sortedGenres.add(findGenre);
            }
            sortedGenres.sort(Comparator.comparingLong(Genre::getId));
            newFilm.setGenres(new LinkedHashSet<>(sortedGenres));
        } else {
            newFilm.setGenres(new HashSet<>());
        }

        if (newFilm.getLikes() == null || newFilm.getLikes().isEmpty()) {
            newFilm.setLikes(new HashSet<>());
        }

        Film oldFilm = filmDao.getFilmById(newFilm.getId());
        oldFilm.setId(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setRatingMpa(newFilm.getRatingMpa());

        Film updateFilm = filmDao.update(oldFilm);

        filmDao.delLinkFilmGenres(oldFilm.getId());
        for (Genre genre : newFilm.getGenres()) {
            filmDao.addLinkFilmGenres(updateFilm.getId(), genre.getId());
        }
        updateFilm.setGenres(newFilm.getGenres());

        filmDao.delAllLikes(newFilm.getId());
        for (Like like : newFilm.getLikes()) {
            filmDao.addLikes(updateFilm.getId(), like.getUserId());
        }
        updateFilm.setLikes(newFilm.getLikes());

        log.info("Фильм с id = {} успешно обновлён", updateFilm.getId());
        return updateFilm;
    }

    public Film delete(Long filmId) {
        log.info("Запрос на удаление фильма с id = {}", filmId);

        Film film = filmDao.getFilmById(filmId);
        if (film == null) {
            log.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }

        Film deleteFilm = filmDao.delete(film);
        if (deleteFilm == null) {
            log.warn("Ошибка DAO при удаление фильма с id = {}", film.getId());
            throw new ValidationException("Ошибка при обновлении фильма");
        }

        log.info("Фильм с id = {} успешно удален", filmId);
        return deleteFilm;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Получен запрос от пользователя с id = {} поставить лайк фильму с id = {}", userId, filmId);

        if (filmDao.getFilmById(filmId) == null) {
            log.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }

        if (userDao.getUserById(userId) == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        if (filmDao.isLiked(filmId, userId)) {
            log.warn("Пользователь с id = {} уже поставил лайк", userId);
            throw new ValidationException("Пользователь уже поставил лайк");
        }

        filmDao.addLikes(filmId, userId);
        log.info("Лайк успешно добавлен");
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Получен запрос от пользователя с id = {} удалить лайк фильму с id = {}", userId, filmId);

        if (filmDao.getFilmById(filmId) == null) {
            log.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }

        if (userDao.getUserById(userId) == null) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        if (!filmDao.isLiked(filmId, userId)) {
            log.warn("Лайк от пользователя с id = {} не найден", userId);
            throw new ValidationException("Нельзя удалить лайк, которого нет");
        }

        filmDao.delLike(filmId, userId);
        log.info("Лайк успешно удален");
    }

    private RatingMpa customRatingMpa() {
        return RatingMpa.builder()
                .id(1L)
                .name("G")
                .build();
    }
}


