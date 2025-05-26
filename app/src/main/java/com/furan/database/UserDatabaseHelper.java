package com.furan.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "user.db";
    private static final int DB_VERSION = 1;

    public UserDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, loginTime TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public long loginUser(String name, String email, String loginTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("loginTime", loginTime);
        return db.insert("users", null, values);
    }

    public Cursor getLoggedInUser() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM users ORDER BY id DESC LIMIT 1", null);
    }

    public void logout() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM users");
    }
}
