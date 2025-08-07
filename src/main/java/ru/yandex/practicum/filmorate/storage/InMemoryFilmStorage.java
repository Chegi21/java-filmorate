package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmMap = new HashMap<>();
    private static final LocalDate MIN_DATE_RELEASE = LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);

    @Override
    public Collection<Film> findAll() {
        log.info("Запрос на получение всех фильмов");

        Collection<Film> films = filmMap.values();

        log.info("Найдено {} фильмов", films.size());
        return filmMap.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Запрос на создание фильма {} с id = {}", film.getName(), film.getId());

        if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE)) {
            log.warn("Попытка создать фильм с недопустимой датой релиза: {}", film.getReleaseDate());
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
        log.info("Фильм {} с id = {} успешно создан", newFilm.getName(), newFilm.getId());
        return newFilm;
    }

    @Override
    public Film update(Film film) {
        log.info("Запрос на обновление фильма: {}", film.getId());

        if (film.getId() == null) {
            log.warn("Ошибка валидации: передан null в качестве ID");
            throw new ValidationException("Ошибка валидации: передан null в качестве ID");
        }

        Film oldFilm = filmMap.get(film.getId());
        if (oldFilm == null) {
            log.warn("Фильм с id={} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }
        if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE)) {
            log.warn("Попытка обновить фильм с недопустимой датой релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());

        filmMap.put(oldFilm.getId(), oldFilm);
        log.info("Фильм с id = {} успешно обновлён", oldFilm.getId());
        return oldFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        log.info("Запрос на получение фильма с id = {}", id);

        Film film = filmMap.get(id);
        if (film == null) {
            log.warn("Фильм по данному id = {} не найден", id);
            throw new NotFoundException("Фильм не найден");
        }

        log.info("Фильм с id = {} найден", id);
        return film;
    }

    @Override
    public Film delete(Long id) {
        log.info("Запрос на удаление фильма с id = {}", id);

        Film film = filmMap.get(id);
        if (film == null) {
            log.warn("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм не найден");
        }

        log.info("Фильм с id = {} успешно удален", id);
        return filmMap.remove(id);
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
