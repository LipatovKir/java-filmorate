package ru.yandex.practicum.filmorate.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class Mappers {
    public static final RowMapper<User> USER_MAPPER = (ResultSet rs, int rowNum) -> new User(rs.getLong("user_id"), rs.getString("email"), rs.getString("name"), rs.getString("login"), rs.getDate("birthday").toLocalDate());

    public static final RowMapper<Film> FILM_MAPPER = (ResultSet rs, int rowNum) -> new Film(rs.getLong("film_id"), rs.getString("name"), rs.getString("description"), rs.getDate("releaseDate").toLocalDate(), rs.getInt("duration"), new Mpa(rs.getLong("mpa_id")));

    public static final RowMapper<Mpa> MPA_MAPPER = (ResultSet rs, int rowNum) -> new Mpa(rs.getLong("mpa_id"), rs.getString("name"));

    public static final RowMapper<Friendship> FRIENDSHIP_MAPPER = (ResultSet rs, int rowNum) -> new Friendship(rs.getLong("first_user_id"), rs.getLong("second_user_id"));

    public static final RowMapper<Genre> GENRE_MAPPER = (ResultSet rs, int rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("name"));

    public static final RowMapper<Like> LIKE_MAPPER = (ResultSet rs, int rowNum) -> new Like(rs.getLong("film_id"), rs.getLong("user_id"));
}
