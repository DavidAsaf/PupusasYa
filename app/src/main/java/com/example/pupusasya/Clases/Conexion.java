package com.example.pupusasya.Clases;

import java.sql.Connection;

public class Conexion
{
    public String getUrlDireccion() {
        return urlDireccion;
    }

    String urlDireccion = "http://localhost/pupusasya/";
static Connection con;


}
