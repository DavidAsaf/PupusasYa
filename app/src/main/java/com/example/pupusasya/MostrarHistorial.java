package com.example.pupusasya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MostrarHistorial extends AppCompatActivity {
Button boton;


    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragmento_historial);
    boton= (Button) findViewById(R.id.btnHistorial);
    boton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MostrarHistorial.this, MostrarHistorial2.class ));
        }
    });


}
}
