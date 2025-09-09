package ru.yandex.practicum.filmorate.storage.constants;

import static ru.yandex.practicum.filmorate.storage.constants.FilmGenreDbConstant.*;

public class GenreDbConstants {
    public static final String GENRE_TABLE_NAME = "genres";

    public static final String GENRE_ID = "genre_id";

    public static final String GENRE_NAME = "genre_name";

    public static final String FIND_ALL_GENRE =
            "SELECT " +
                    GENRE_ID + ", " +
                    GENRE_NAME + " " +
                    "FROM " + GENRE_TABLE_NAME;

    public static final String FIND_GENRE_BY_ID =
            "SELECT " +
                    GENRE_ID + ", " +
                    GENRE_NAME + " " +
                    "FROM " + GENRE_TABLE_NAME + " " +
                    "WHERE " + GENRE_ID + " = ?";


    public static final String FIND_GENRE_BY_FILM_ID =
            "SELECT " +
                    "g." + GENRE_ID + ", " +
                    "g." + GENRE_NAME + " " +
                    "FROM " + FILM_GENRE_TABLE_NAME + " fg " +
                    "JOIN " + GENRE_TABLE_NAME + " g " +
                    "ON fg." + FILM_GENRE_GENRE_ID + " = g." + GENRE_ID + " " +
                    "WHERE fg." + FILM_GENRE_FILM_ID + " = ?";

}
