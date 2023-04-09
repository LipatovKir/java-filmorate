package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class Film {

    private final Integer id;
    @NotNull
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String  description;
    private final LocalDate releaseDate;
    @Positive
    private final Integer duration;
}
