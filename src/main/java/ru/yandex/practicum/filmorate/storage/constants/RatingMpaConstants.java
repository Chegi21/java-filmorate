package ru.yandex.practicum.filmorate.storage.constants;

public class RatingMpaConstants {
    public static final String RATING_MPA_TABLE_NAME = "rating_mpa";

    public static final String RATING_ID = "rating_id";

    public static final String RATING_NAME = "rating_name";

    public static final String FIND_ALL_RATINGS =
            "SELECT " + RATING_ID + ", " + RATING_NAME +
                    " FROM " + RATING_MPA_TABLE_NAME;

    public static final String FIND_RATING =
            "SELECT " + RATING_ID + ", " + RATING_NAME +
                    " FROM " + RATING_MPA_TABLE_NAME +
                    " WHERE " + RATING_ID + " = ?";
}
