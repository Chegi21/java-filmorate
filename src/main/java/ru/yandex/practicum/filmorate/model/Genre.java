package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class Genre {
    @PositiveOrZero(message = "Id не может отрицательным числом")
    private Long id;

    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}
