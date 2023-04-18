package ru.yandex.practicum.filmorate.model;

import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    private final Integer id;
    @NotNull
    @NotBlank
    @Email
    private final String email;
    public final String name;
    @NotNull
    @NotBlank
    private final String login;
    @PastOrPresent
    private final LocalDate birthday;
}