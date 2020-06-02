package com.example.pupusasya.Clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class pupuseriaDB extends SQLiteOpenHelper {
    public pupuseriaDB (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pupuseriaSelected (idPupuseria integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pupuseriaSelected");
        db.execSQL("CREATE TABLE pupuseriaSelected (idPupuseria integer)");
    }
}
