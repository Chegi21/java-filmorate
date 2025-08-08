package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
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
    public Film delete(Long id) {
        return filmMap.remove(id);
    }

    @Override
    public boolean isLiked(Long filmId, Long userId) {
        return filmMap.get(filmId).getLikes().contains(userId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmMap.get(filmId).getLikes().add(userId);
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
