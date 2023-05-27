package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable("id") Long id) {
        return mpaService.findMpaById(id);
    }

    @GetMapping
    public List<Mpa> findAllMpa() {
        return mpaService.findAllMpa();
    }
}
