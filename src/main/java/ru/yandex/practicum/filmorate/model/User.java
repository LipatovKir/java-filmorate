package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class User {

    private final Long id;
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
    private List<Long> friends = new ArrayList<>();
}