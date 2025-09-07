package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class RatingMpaController {
    private final FilmService filmService;

    @Autowired
    public RatingMpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public Collection<RatingMpa> getRatings() {
        return filmService.getRatings();
    }

    @GetMapping("/{id}")
    public RatingMpa getMpaById(@PathVariable Long id) {
        return filmService.getRatingById(id);
    }
}
