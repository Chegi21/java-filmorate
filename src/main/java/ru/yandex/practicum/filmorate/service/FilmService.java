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

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Получен запрос от пользователя с id = {} поставить лайк фильму с id = {}", userId, filmId);

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        boolean isLiked = film.getLikes().contains(user.getId());
        if (isLiked) {
            log.warn("Пользователь с id = {} уже поставил лайк", userId);
            throw new ValidationException("Пользователь уже поставил лайк");
        }

        film.getLikes().add(user.getId());
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

    public Collection<Film> getPopular(Integer count) {
        log.info("Получен запрос на список популярных фильмов");
        if (count < 1) {
            throw new ValidationException("Количество фильмов для вывода не должно быть меньше 1");
        }

        Collection<Film> allFilms = filmStorage.findAll();
        if (allFilms.isEmpty()) {
            log.warn("Список фильмов пустой");
            throw new NotFoundException("Список фильмов пустой");
        }

        Collection<Film> topFilms = allFilms.stream()
                .filter(film -> !film.getLikes().isEmpty())
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());

        if (topFilms.isEmpty()) {
            log.warn("Список популярных фильмов пустой");
            throw new NotFoundException("Список популярных фильмов пустой");
        }

        log.info("Сформирован список из {} популярных фильмов", topFilms.size());
        return topFilms;
    }
}


