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
    private static final int DATABASE_VERSION = 2; // 升级版本号

    private static final String TABLE_DIARY = "diary_entries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_USERNAME = "userName";  // 新增字段

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
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_USERNAME + " TEXT NOT NULL" +  // 新增字段，非空
                ")";
        db.execSQL(createTable);
    }

    // 版本升级时增加userName字段
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 添加userName字段，默认值为空字符串
            db.execSQL("ALTER TABLE " + TABLE_DIARY + " ADD COLUMN " + COLUMN_USERNAME + " TEXT NOT NULL DEFAULT ''");
        }
    }

    public long insertDiaryEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, entry.getTitle());
        values.put(COLUMN_CONTENT, entry.getContent());
        values.put(COLUMN_DATE, dateFormat.format(entry.getDate()));
        values.put(COLUMN_USERNAME, entry.getUserName()); // 存储用户名

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
        values.put(COLUMN_USERNAME, entry.getUserName());

        // 加入userName作为更新条件，确保只更新对应用户的数据
        db.update(TABLE_DIARY, values,
                COLUMN_ID + " = ? AND " + COLUMN_USERNAME + " = ?",
                new String[]{String.valueOf(entry.getId()), entry.getUserName()});
        db.close();
    }

    public void deleteDiaryEntry(long id, String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 加入userName条件，防止删除别的用户数据
        db.delete(TABLE_DIARY, COLUMN_ID + " = ? AND " + COLUMN_USERNAME + " = ?", new String[]{String.valueOf(id), userName});
        db.close();
    }

    public List<DiaryEntry> getDiaryEntriesByDate(Date date, String userName) {
        List<DiaryEntry> entries = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dayFormat.format(date);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_DIARY +
                " WHERE date(substr(" + COLUMN_DATE + ", 1, 10)) = ? AND " + COLUMN_USERNAME + " = ? " +
                " ORDER BY " + COLUMN_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{dateString, userName});

        if (cursor.moveToFirst()) {
            do {
                try {
                    DiaryEntry entry = new DiaryEntry(
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                            dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))  // 读取用户名
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

    public DiaryEntry getDiaryEntryById(long id, String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DIARY, null,
                COLUMN_ID + " = ? AND " + COLUMN_USERNAME + " = ?",
                new String[]{String.valueOf(id), userName}, null, null, null);

        DiaryEntry entry = null;
        if (cursor.moveToFirst()) {
            try {
                entry = new DiaryEntry(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                        dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
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
