package ca.unb.mobiledev.spaceforks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database information
    private static final String DATABASE_NAME = "SF_HIGHSCORES.DB";
    private static final int DATABASE_VERSION = 1;

    // Table columns
    public static final String ID = "_id";
    public static final String PLAYER_NAME = "name";
    public static final String SCORE = "score";
    public static String[] COLUMNS = {ID, PLAYER_NAME, SCORE};

    // Table creation statement
    public static final String TABLE_NAME = "HIGHSCORES";
    private static final String TABLE_CREATE =
                    "CREATE TABLE " + TABLE_NAME + " (" + ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PLAYER_NAME + " TEXT, " + SCORE + " INT);";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
