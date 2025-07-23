package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @PositiveOrZero(message = "Id не может отрицательным числом")
    private Long id;

    @NotNull(message = "Электронная почта не может быть null")
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна содержать символ '@'")
    private String email;

    @NotNull(message = "Логин не может быть null")
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелов")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть null")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
