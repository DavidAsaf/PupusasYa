package com.example.pupusasya;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    EditText nomP, appP, corrP, teleP, contraP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nomP = findViewById(R.id.etnombre);
        appP = findViewById(R.id.etapellido);
        corrP = findViewById(R.id.etcorreo);
        teleP = findViewById(R.id.ettelefono);
        contraP = findViewById(R.id.etcontra);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = corrP.getText().toString();
        String telefono = teleP.getText().toString();
        String contrasena = contraP.getText().toString();

        switch (item.getItemId()){
            case R.id.btnregistro: {
                if (nombre.equals(" ")| apellido.equals(" ")| correo.equals(" ")| telefono.equals(" ")|contrasena.equals(" ")){
                    validacion();
                    break;
                }
                else{
                    Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
                    limpiarcajas();
                    break;
                }


            }
        }
        return true;
    }

    private void limpiarcajas() {
        nomP.setText("");
        appP.setText(" ");
        corrP.setText("");
        teleP.setText("");
        contraP.setText("");
    }

    private void validacion() {
        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = corrP.getText().toString();
        String telefono = teleP.getText().toString();
        String contrasena = contraP.getText().toString();
        if (nombre.equals(" ")){
            nomP.setError("Required");
        }
        else if (apellido.equals(" ")){
            appP.setError("Requerido");
        }
        else if (correo.equals(" ")){
            corrP.setError("Requerido");
        }
        else if (telefono.equals(" ")){
            teleP.setError("Requerido");
        }
        else if (contrasena.equals(" ")){
            contraP.setError("Requerido");
        }

    }
}
