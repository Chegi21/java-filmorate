package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Collection<Film> getPopular(Integer count);

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(Long filmId);

    Film delete(Long filmId);

    boolean isLiked(Long filmId, Long userId);

    void addLike(Long filmId, Long userId);

}
