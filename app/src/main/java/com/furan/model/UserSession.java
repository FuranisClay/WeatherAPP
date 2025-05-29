package com.furan.model;

public class UserSession {
    private static String currentUserName = "unknown";

    public static void setCurrentUserName(String userName) {
        currentUserName = userName;
    }
    public static String getCurrentUserName() {
        return currentUserName;
    }
}