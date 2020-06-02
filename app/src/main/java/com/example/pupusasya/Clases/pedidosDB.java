package com.example.pupusasya.Clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class pedidosDB extends SQLiteOpenHelper {

    public pedidosDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS pedido (idPedido integer primary key, usuario integer, " +
                "idPupuseria integer, idProducto integer, cantidad integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pedido");
        db.execSQL("CREATE TABLE pedido (idPedido integer primary key, usuario integer, idPupuseria integer, " +
                "idProducto integer, cantidad integer)");
    }
}
