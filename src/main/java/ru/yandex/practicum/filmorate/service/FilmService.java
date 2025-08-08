package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate MIN_DATE_RELEASE = LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {
        log.info("Запрос на получение всех фильмов");

        Collection<Film> films = filmStorage.getFilms();

        log.info("Найдено {} фильмов", films.size());
        return films;
    }

    public Collection<Film> getPopular(Integer count) {
        log.info("Получен запрос на список популярных фильмов");
        if (count < 1) {
            throw new ValidationException("Количество фильмов для вывода не должно быть меньше 1");
        }

        Collection<Film> allFilms = filmStorage.getFilms();
        if (allFilms.isEmpty()) {
            log.warn("Список фильмов пустой");
            throw new NotFoundException("Список фильмов пустой");
        }

        Collection<Film> topFilms = filmStorage.getPopular(count);

        if (topFilms.isEmpty()) {
            log.warn("Список популярных фильмов пустой");
            throw new NotFoundException("Список популярных фильмов пустой");
        }

        log.info("Сформирован список из {} популярных фильмов", topFilms.size());
        return topFilms;
    }

    public Film getFilmById(Long filmId) {
        log.info("Запрос фильма c id = {}", filmId);

        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }

        log.info("Фильм с id = {} найден", filmId);
        return film;
    }

    public Film create(Film film) {
        log.info("Запрос на создание фильма {}", film.getName());

        if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE)) {
            log.warn("Попытка создать фильм с недопустимой датой релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        Film newFilm = Film.builder()
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();

        filmStorage.create(newFilm);

        log.info("Фильм {} успешно создан", newFilm.getName());
        return newFilm;
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

        Film oldFilm = filmStorage.getFilmById(newFilm.getId());
        if (oldFilm == null) {
            log.warn("Фильм с id = {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм не найден");
        }

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        if (newFilm.getLikes() == null) {
            oldFilm.setLikes(new HashSet<>());
        } else {
            oldFilm.setLikes(newFilm.getLikes());
        }

        Film updateFilm = filmStorage.update(oldFilm);

        log.info("Фильм с id = {} успешно обновлён", updateFilm.getId());
        return updateFilm;
    }

    public Film delete(Long filmId) {
        log.info("Запрос на удаление фильма с id = {}", filmId);

        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }

        Film deleteFilm = filmStorage.delete(filmId);

        log.info("Фильм с id = {} успешно удален", filmId);
        return deleteFilm;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Получен запрос от пользователя с id = {} поставить лайк фильму с id = {}", userId, filmId);

        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь по данному id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        boolean isLiked = filmStorage.isLiked(filmId, userId);
        if (isLiked) {
            log.warn("Пользователь с id = {} уже поставил лайк", userId);
            throw new ValidationException("Пользователь уже поставил лайк");
        }

        filmStorage.addLike(filmId, userId);
        log.info("Лайк успешно добавлен");
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Получен запрос от пользователя с id = {} удалить лайк фильму с id = {}", userId, filmId);

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (!film.getLikes().contains(user.getId())) {
            log.warn("Лайк от пользователя с id = {} не найден", userId);
            throw new ValidationException("Нельзя удалить лайк, которого нет");
        }

        film.getLikes().remove(user.getId());
        log.info("Лайк успешно удален");
    }
}


