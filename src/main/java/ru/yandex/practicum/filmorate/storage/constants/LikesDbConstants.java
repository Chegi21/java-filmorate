package ru.yandex.practicum.filmorate.storage.constants;

public class LikesDbConstants {
    public static final String LIKES_TABLE_NAME = "likes";

    public static final String LIKES_FILM_ID = "film_id";

    public static final String LIKES_USER_ID = "user_id";

    public static final String FIND_COUNT_LIKES =
            "SELECT COUNT(" + LIKES_USER_ID + ") " +
                    "FROM " + LIKES_TABLE_NAME + " " +
                    "WHERE " + LIKES_FILM_ID + " = ?";

    public static final String FIND_LIKES_BY_FILM_ID =
            "SELECT " +
                    LIKES_FILM_ID + ", " +
                    LIKES_USER_ID + " " +
                    "FROM " + LIKES_TABLE_NAME + " " +
                    "WHERE " + LIKES_FILM_ID + " = ?";

    public static final String INSERT_LIKE =
            "INSERT INTO " +
                    LIKES_TABLE_NAME + " (" +
                    LIKES_FILM_ID + ", " +
                    LIKES_USER_ID + ") " +
                    "VALUES (?, ?)";

    public static final String DELETE_LIKE =
            "DELETE FROM " + LIKES_TABLE_NAME + " WHERE " + LIKES_FILM_ID + " = ? " + "AND " + LIKES_USER_ID + " = ?";

    public static final String DELETE_ALL_LIKE =
            "DELETE FROM " + LIKES_TABLE_NAME + " WHERE " + LIKES_FILM_ID + " = ?";


    public static final String IS_LIKE =
            "SELECT COUNT(*) FROM " +
                    LIKES_TABLE_NAME + " WHERE " + LIKES_FILM_ID + " = ? AND " + LIKES_USER_ID + " = ?";
}
