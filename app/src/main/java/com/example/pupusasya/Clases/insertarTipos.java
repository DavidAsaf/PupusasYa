package com.example.pupusasya.Clases;

public class insertarTipos {
    private int idImagen;
    private String textoTitulo;

    public insertarTipos (int idImagen, String textoTituloM) {
        this.idImagen = idImagen;
        this.textoTitulo = textoTituloM;
    }

    public String get_textoTitulo() {
        return textoTitulo;
    }


    public int get_idImagen() {
        return idImagen;
    }
}
