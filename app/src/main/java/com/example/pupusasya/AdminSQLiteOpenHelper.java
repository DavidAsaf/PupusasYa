package com.example.pupusasya;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {


    //ESTO ES NUEVOOOOOOOOOOOOOOOOOOOOOOOOO PARA VER SI FUNCIONA EL CARRITO DE COMPRAS. 

    //llamamos al constructor
    public AdminSQLiteOpenHelper(Context context, String nombre,
                                 CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    //creamos la tabla
    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL("CREATE TABLE usuario(idUser integer primary key autoincrement, username text, password text)");
       db.execSQL("CREATE TABLE fondo(id integer primary key autoincrement, imagen integer)");
       db.execSQL("CREATE TABLE item(id integer primary key autoincrement, descripcion text, precio text, pedido text)");

    }
    //borrar la tabla y crear la nueva tabla
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte,int versionNue) {

        db.execSQL("drop table if exists usuario");
        db.execSQL("CREATE TABLE usuario(idUser integer primary key autoincrement, username text, password text)");

        db.execSQL("drop table if exists fondo");
        db.execSQL("CREATE TABLE fondos(id integer primary key autoincrement, imagen integer)");

        db.execSQL("drop table if exists item");
        db.execSQL("CREATE TABLE item(id integer primary key autoincrement, descripcion text, precio text, pedido text)");


    }
}

