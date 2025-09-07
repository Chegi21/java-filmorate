package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class RatingMpa {
    @NotNull(message = "Id не должен быть null")
    @PositiveOrZero(message = "Id не может быть отрицательным числом")
    private Long id;

    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String name;
}
