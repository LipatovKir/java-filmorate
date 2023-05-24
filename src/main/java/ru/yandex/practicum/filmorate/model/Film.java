package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private final Long id;
    @NotNull
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final Integer duration;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private final Mpa mpa;
}
