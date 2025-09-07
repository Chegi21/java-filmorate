package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"filmId", "userId"})
public class Like {
    @NotNull
    @PositiveOrZero(message = "Id фильма не может отрицательным числом")
    private Long filmId;

    @NotNull
    @PositiveOrZero(message = "Id пользователя не может отрицательным числом")
    private Long userId;
}
