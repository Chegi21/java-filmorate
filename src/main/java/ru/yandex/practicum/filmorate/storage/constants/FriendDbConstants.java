package ru.yandex.practicum.filmorate.storage.constants;

import static ru.yandex.practicum.filmorate.storage.constants.UserDbConstants.USER_ID;
import static ru.yandex.practicum.filmorate.storage.constants.UserDbConstants.USER_TABLE_NAME;

public class FriendDbConstants {
    public static final String FRIEND_TABLE_NAME = "friends";

    public static final String FRIEND_USER_ID = "user_id";

    public static final String FRIEND_FRIEND_ID = "friend_id";

    public static final String FRIEND_STATUS = "status";

    public static final String FIND_FRIENDS_ID =
            "SELECT " + FRIEND_FRIEND_ID + " " + "FROM " + FRIEND_TABLE_NAME + " " + "WHERE " + FRIEND_USER_ID + " = ?";

    public static final String FIND_ALL_FRIENDS_USER =
            "SELECT u.* " +
                    "FROM " + USER_TABLE_NAME + " u " +
                    "JOIN " + FRIEND_TABLE_NAME + " f " +
                    "ON " +
                    "u." + USER_ID + " = " + "f." + FRIEND_FRIEND_ID + " " +
                    "WHERE " +
                    "f." + FRIEND_USER_ID + " = ?";

    public static final String FIND_COMMON_FRIENDS =
            "SELECT u.* " +
                    "FROM " + USER_TABLE_NAME + " u " +
                    "JOIN " + FRIEND_TABLE_NAME + " f1 " +
                    "ON " +
                    "u." + USER_ID + " = " + "f1." + FRIEND_FRIEND_ID + " " +
                    "JOIN " + FRIEND_TABLE_NAME + " f2 " +
                    "ON " +
                    "u." + USER_ID + " = " + "f2." + FRIEND_FRIEND_ID + " " +
                    "WHERE " +
                    "f1." + USER_ID + " = ? " +
                    "AND " +
                    "f2." + FRIEND_USER_ID + " = ?";

    public static final String INSERT_FRIENDS =
            "INSERT INTO " +
                    FRIEND_TABLE_NAME + " (" +
                    FRIEND_USER_ID + ", " +
                    FRIEND_FRIEND_ID + ", " +
                    FRIEND_STATUS + ") " +
                    "VALUES (?, ?, false)";

    public static final String DELETE_ALL_FRIENDS =
            "DELETE FROM " +
                    FRIEND_TABLE_NAME + " " +
                    "WHERE " +
                    FRIEND_USER_ID + " = ? " +
                    "OR " + FRIEND_FRIEND_ID + " = ?";

    public static final String DELETE_FRIEND =
            "DELETE FROM " +
                    FRIEND_TABLE_NAME + " " +
                    "WHERE " +
                    FRIEND_USER_ID + " = ? " +
                    "AND " + FRIEND_FRIEND_ID + " = ?";

}
