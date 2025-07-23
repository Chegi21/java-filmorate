package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> filmMap = new HashMap<>();
    private static final LocalDate MIN_DATE_RELEASE = LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение всех фильмов");

        Collection<Film> films = filmMap.values();
        if (films.isEmpty()) {
            log.error("Список фильмов пустой");
            throw new ValidationException("Список фильмов пустой");
        }

        log.info("Найдено {} фильмов", films.size());
        return filmMap.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на создание фильма: {}", film);

        if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE)) {
            log.error("Попытка создать фильм с недопустимой датой релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        Film newFilm = Film.builder()
                .id(getNextId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();

        filmMap.put(newFilm.getId(), newFilm);
        log.info("Фильм успешно создан: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Запрос на обновление фильма: {}", newFilm);

        if (newFilm.getId() == null) {
            log.error("Ошибка валидации: передан null в качестве ID");
            throw new ValidationException("Ошибка валидации: передан null в качестве ID");
        }

        Film oldFilm = filmMap.get(newFilm.getId());
        if (oldFilm == null) {
            log.error("Фильм с id={} не найден", newFilm.getId());
            throw new NotFoundException("Фильм не найден");
        }
        if (newFilm.getReleaseDate().isBefore(MIN_DATE_RELEASE)) {
            log.error("Попытка обновить фильм с недопустимой датой релиза: {}", newFilm.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        filmMap.put(oldFilm.getId(), oldFilm);
        log.info("Фильм с id={} успешно обновлён", oldFilm.getId());
        return oldFilm;
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
