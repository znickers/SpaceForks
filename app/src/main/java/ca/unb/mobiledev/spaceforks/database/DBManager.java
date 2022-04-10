package ca.unb.mobiledev.spaceforks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private final DatabaseHelper dbHelper;

    public DBManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor listAllRecords() {
        Cursor cursor = openReadOnlyDatabase().query(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMNS, null, null, null, null, "score DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int getHighScore() {
        Cursor cursor = openReadOnlyDatabase().query(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMNS, null, null, null, null, "score DESC", "1");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor.getInt(2);    // Return high score
    }

    public void insertRecord(String name, String score) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.PLAYER_NAME, name);
        contentValue.put(DatabaseHelper.SCORE, score);
        openWriteDatabase().insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public void deleteRecord(int id) {
        openWriteDatabase().delete(DatabaseHelper.TABLE_NAME,
                DatabaseHelper.ID + "=?",
                new String[]{String.valueOf(id)});
    }

    private SQLiteDatabase openReadOnlyDatabase() {
        return dbHelper.getReadableDatabase();
    }

    private SQLiteDatabase openWriteDatabase() {
        return dbHelper.getWritableDatabase();
    }
}
