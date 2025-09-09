package ru.yandex.practicum.filmorate.storage.constants;

import static ru.yandex.practicum.filmorate.storage.constants.FilmGenreDbConstant.*;
import static ru.yandex.practicum.filmorate.storage.constants.GenreDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.LikesDbConstants.*;
import static ru.yandex.practicum.filmorate.storage.constants.RatingMpaConstants.*;

public class FilmDbConstants {
    public static final String FILM_TABLE_NAME = "films";

    public static final String FILM_ID = "film_id";

    public static final String FILM_NAME = "film_name";

    public static final String FILM_DESCRIPTION = "description";

    public static final String FILM_DURATION = "duration";

    public static final String FILM_RELEASE_DATE = "release_date";

    public static final String FILM_RATING_ID = "rating_id";

    public static final String FIND_ALL_FILM =
            "SELECT " +
                    "f." + FILM_ID + ", " +
                    "f." + FILM_NAME + ", " +
                    "f." + FILM_DESCRIPTION + ", " +
                    "f." + FILM_RELEASE_DATE + ", " +
                    "f." + FILM_DURATION + ", " +
                    "r." + RATING_ID + ", " +
                    "r." + RATING_NAME + ", " +
                    "g." + GENRE_ID + ", " +
                    "g." + GENRE_NAME + ", " +
                    "l." + LIKES_USER_ID + " " +
                    "FROM " + FILM_TABLE_NAME + " f " +
                    "LEFT JOIN " + RATING_MPA_TABLE_NAME + " r ON f." + FILM_RATING_ID + " = r." + RATING_ID + " " +
                    "LEFT JOIN " + FILM_GENRE_TABLE_NAME + " fg ON f." + FILM_ID + " = fg." + FILM_GENRE_FILM_ID + " " +
                    "LEFT JOIN " + GENRE_TABLE_NAME + " g ON fg." + FILM_GENRE_GENRE_ID + " = g." + GENRE_ID + " " +
                    "LEFT JOIN " + LIKES_TABLE_NAME + " l ON f." + FILM_ID + " = l." + FILM_ID + " " +
                    "ORDER BY f." + FILM_ID;

    public static final String FIND_POPULAR_FILM =
            "SELECT f." + FILM_ID + ", " +
                    "f." + FILM_NAME + ", " +
                    "f." + FILM_DESCRIPTION + ", " +
                    "f." + FILM_DURATION + ", " +
                    "f." + FILM_RELEASE_DATE + ", " +
                    "r." + RATING_ID + ", " +
                    "r." + RATING_NAME + ", " +
                    "COUNT(DISTINCT l." + LIKES_USER_ID + ") AS likes_count " +
                    "FROM " + FILM_TABLE_NAME + " f " +
                    "LEFT JOIN " + LIKES_TABLE_NAME + " l ON f." + FILM_ID + " = l." + LIKES_FILM_ID + " " +
                    "LEFT JOIN " + RATING_MPA_TABLE_NAME + " r ON f." + FILM_RATING_ID + " = r." + RATING_ID + " " +
                    "GROUP BY f." + FILM_ID + ", " +
                    "f." + FILM_NAME + ", " +
                    "f." + FILM_DESCRIPTION + ", " +
                    "f." + FILM_DURATION + ", " +
                    "f." + FILM_RELEASE_DATE + ", " +
                    "r." + RATING_ID + ", " +
                    "r." + RATING_NAME + " " +
                    "ORDER BY likes_count DESC " +
                    "LIMIT ?";

    public static final String FIND_FILM_BY_ID =
            "SELECT f." + FILM_ID + ", " +
                    "f." + FILM_NAME + ", " +
                    "f." + FILM_DESCRIPTION + ", " +
                    "f." + FILM_RELEASE_DATE + ", " +
                    "f." + FILM_DURATION + ", " +
                    "r." + RATING_ID + ", " +
                    "r." + RATING_NAME + " " +
                    "FROM " + FILM_TABLE_NAME + " f " +
                    "LEFT JOIN " + RATING_MPA_TABLE_NAME + " r " +
                    "ON f." + FILM_RATING_ID + " = r." + RATING_ID + " " +
                    "WHERE f." + FILM_ID + " = ?";

    public static final String FIND_FILM_BY_ALL_ARG =
            "SELECT " +
                    FILM_ID + ", " +
                    FILM_NAME + ", " +
                    FILM_DESCRIPTION + ", " +
                    FILM_RELEASE_DATE + ", " +
                    FILM_DURATION + ", " +
                    FILM_RATING_ID + " " +
                    "FROM " + FILM_TABLE_NAME + " " +
                    "WHERE " + FILM_NAME + " = ? " +
                    "AND " + FILM_DESCRIPTION + " = ? " +
                    "AND " + FILM_RELEASE_DATE + " =? " +
                    "AND " + FILM_DURATION + " = ? " +
                    "AND " + FILM_RATING_ID + " = ?";

    public static final String INSERT_FILM =
            "INSERT INTO " +
                    FILM_TABLE_NAME + " (" +
                    FILM_NAME + ", " +
                    FILM_DESCRIPTION + ", " +
                    FILM_DURATION + ", " +
                    FILM_RELEASE_DATE + ", " +
                    FILM_RATING_ID + ") " +
                    "VALUES (?, ?, ?, ?, ?)";

    public static final String UPDATE_FILM =
            "UPDATE " + FILM_TABLE_NAME + " SET " +
                    FILM_NAME + " = ?, " +
                    FILM_DESCRIPTION + " = ?, " +
                    FILM_DURATION + " = ?, " +
                    FILM_RELEASE_DATE + " = ?, " +
                    FILM_RATING_ID + " = ? " +
                    "WHERE film_id = ?";

    public static final String DELETE_FILM =
            "DELETE FROM " + FILM_TABLE_NAME + " WHERE " +  FILM_ID + " = ?";

}
