package com.furan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.furan.model.DiaryEntry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "diary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DIARY = "diary_entries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_DATE = "date";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DiaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_DIARY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
        onCreate(db);
    }

    public long insertDiaryEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, entry.getTitle());
        values.put(COLUMN_CONTENT, entry.getContent());
        values.put(COLUMN_DATE, dateFormat.format(entry.getDate()));

        long id = db.insert(TABLE_DIARY, null, values);
        db.close();
        return id;
    }

    public void updateDiaryEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, entry.getTitle());
        values.put(COLUMN_CONTENT, entry.getContent());
        values.put(COLUMN_DATE, dateFormat.format(entry.getDate()));

        db.update(TABLE_DIARY, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(entry.getId())});
        db.close();
    }

    public void deleteDiaryEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DIARY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<DiaryEntry> getDiaryEntriesByDate(Date date) {
        List<DiaryEntry> entries = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dayFormat.format(date);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_DIARY +
                " WHERE date(substr(" + COLUMN_DATE + ", 1, 10)) = ? " +
                " ORDER BY " + COLUMN_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{dateString});

        if (cursor.moveToFirst()) {
            do {
                try {
                    DiaryEntry entry = new DiaryEntry(
                            cursor.getLong(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            dateFormat.parse(cursor.getString(3))
                    );
                    entries.add(entry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entries;
    }

    public DiaryEntry getDiaryEntryById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DIARY, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        DiaryEntry entry = null;
        if (cursor.moveToFirst()) {
            try {
                entry = new DiaryEntry(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        dateFormat.parse(cursor.getString(3))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        db.close();
        return entry;
    }
}