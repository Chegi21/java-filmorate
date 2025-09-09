package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmDao implements FilmDao {
    private final Map<Long, Film> filmMap = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return filmMap.values();
    }

    public Collection<Film> getPopular(Integer count) {
        return filmMap.values().stream()
                .filter(film -> !film.getLikes().isEmpty())
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Like> getLikesByFilmId(Long filmId) {
        return filmMap.get(filmId).getLikes();
    }

    @Override
    public Collection<Genre> getGenres() {
        return List.of();
    }

    @Override
    public Collection<RatingMpa> getRatings() {
        return List.of();
    }

    @Override
    public Genre getGenresById(Long genreId) {
        return null;
    }

    @Override
    public RatingMpa getRatingMpaById(Long ratingId) {
        return null;
    }

    @Override
    public Film getFilmById(Long id) {
        return filmMap.get(id);
    }

    @Override
    public Film create(Film newFilm) {
        newFilm.setId(getNextId());
        filmMap.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        return filmMap.remove(film.getId());
    }

    @Override
    public boolean isLiked(Long filmId, Long userId) {
        Like like = Like.builder().filmId(filmId).userId(userId).build();
        return filmMap.get(filmId).getLikes().contains(like);
    }

    @Override
    public void addLikes(Long filmId, List<Long> userId) {

    }


    @Override
    public void delAllLikes(Long filmId) {

    }

    @Override
    public void delLike(Long filmId, Long userId) {
        Like like = Like.builder().filmId(filmId).userId(userId).build();
        filmMap.get(filmId).getLikes().remove(like);
    }

    @Override
    public void addLinkFilmGenres(Long filmId, List<Long> genreId) {

    }


    @Override
    public void delLinkFilmGenres(Long genreId) {

    }

    private long getNextId() {
        long currentMaxId = filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
