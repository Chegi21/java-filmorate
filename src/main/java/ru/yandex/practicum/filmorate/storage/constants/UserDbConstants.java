package ru.yandex.practicum.filmorate.storage.constants;

import static ru.yandex.practicum.filmorate.storage.constants.FriendDbConstants.*;

public class UserDbConstants {
    public static final String USER_TABLE_NAME = "users";

    public static final String USER_ID = "user_id";

    public static final String USER_NAME = "user_name";

    public static final String USER_LOGIN = "login";

    public static final String USER_EMAIL = "email";

    public static final String USER_BIRTHDAY = "birthday";

    public static final String FIND_ALL_USER =
            "SELECT " +
                    "u." + USER_ID + ", " +
                    "u." + USER_EMAIL + ", " +
                    "u." + USER_LOGIN + ", " +
                    "u." + USER_NAME + ", " +
                    "u." + USER_BIRTHDAY + ", " +
                    "f." + FRIEND_FRIEND_ID + " " +
                    "FROM " + USER_TABLE_NAME + " u " +
                    "LEFT JOIN " + FRIEND_TABLE_NAME +
                    " f ON u." + USER_ID + " = f." + FRIEND_USER_ID + " " +
                    "ORDER BY u." + USER_ID;


    public static final String FIND_USER_BY_ID = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_ID + " = ?";

    public static final String INSERT_USER =
            "INSERT INTO " +
                    USER_TABLE_NAME + " (" +
                    USER_EMAIL + ", " +
                    USER_LOGIN + ", " +
                    USER_NAME + ", " +
                    USER_BIRTHDAY + ") " +
                    "VALUES (?, ?, ?, ?)";

    public static final String UPDATE_USER =
            "UPDATE " + USER_TABLE_NAME + " SET " +
                    USER_EMAIL + " = ?, " +
                    USER_LOGIN + " = ?, " +
                    USER_NAME + " = ?, " +
                    USER_BIRTHDAY + " = ? " +
                    "WHERE " +
                    USER_ID + " = ?";

    public static final String DELETE_USER =
            "DELETE FROM " + USER_TABLE_NAME + " WHERE " + USER_ID + " = ?";

    public static final String EMILE_EXISTS =
            "SELECT COUNT(*) FROM " + USER_TABLE_NAME + " WHERE " + USER_EMAIL + " = ?";

    public static final String LOGIN_EXISTS =
            "SELECT COUNT(*) FROM " + USER_TABLE_NAME + " WHERE " + USER_LOGIN + " = ?";
}
