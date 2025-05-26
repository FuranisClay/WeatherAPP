package com.furan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserManager {

    private static UserManager instance;
    private Context context;
    private UserDbHelper dbHelper;
    private String currentUser;

    private UserManager(Context context) {
        this.context = context;
        dbHelper = new UserDbHelper(context);
    }

    public static UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null, "username=? AND password=?",
                new String[]{username, password}, null, null, null);
        boolean success = cursor.moveToFirst();
        if (success) {
            currentUser = username;
        }
        cursor.close();
        db.close();
        return success;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void register(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        db.insert("users", null, values);
        db.close();
    }
}
