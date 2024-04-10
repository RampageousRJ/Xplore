package com.example.xplore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "xplore.db";
    private static final int DB_VERSION = 1;
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE USERS (id integer primary key autoincrement, name text, email text, phone text, password text)";
        db.execSQL(query);
        query = "CREATE TABLE FEEDBACK (id integer primary key autoincrement, owner_name text, email text, feedback text, rating text)";
        db.execSQL(query);
    }

    public void insertUser(String name, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("password", password);
        db.insert("USERS", null, values);
        db.close();
    }

    public boolean validateUser(Context context ,String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;

        }
        cursor.close();
        db.close();
        Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS FEEDBACK");
        onCreate(db);
    }
}