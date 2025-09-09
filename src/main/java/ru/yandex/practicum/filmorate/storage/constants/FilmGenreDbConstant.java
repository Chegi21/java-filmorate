package ru.yandex.practicum.filmorate.storage.constants;

public class FilmGenreDbConstant {
    public static final String FILM_GENRE_TABLE_NAME = "film_genres";

    public static final String FILM_GENRE_FILM_ID = "film_id";

    public static final String FILM_GENRE_GENRE_ID = "genre_id";

    public static final String INSERT_FILM_GENRE =
            "INSERT INTO " +
                    FILM_GENRE_TABLE_NAME + " (" +
                    FILM_GENRE_FILM_ID + ", " +
                    FILM_GENRE_GENRE_ID + ") " +
                    "VALUES ";

    public static final String DELETE_FILM_GENRE =
            "DELETE FROM " + FILM_GENRE_TABLE_NAME + " WHERE " + FILM_GENRE_FILM_ID + " = ?";
}
