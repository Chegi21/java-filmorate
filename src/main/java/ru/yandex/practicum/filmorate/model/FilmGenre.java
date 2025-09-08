package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = ("{id}"))
public class FilmGenre {
    @NotNull
    private Long filmId;
    @NotNull
    private Long genreId;
}
