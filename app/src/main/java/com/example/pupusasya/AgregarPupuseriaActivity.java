package com.example.pupusasya;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pupusasya.Clases.Conexion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AgregarPupuseriaActivity extends AppCompatActivity {

    private Button btnAgregar, btnVolver;
    private TextView tvPupuseria, tvDireccion, tvEstado, tvTelefono, tvCelular;
    private String resultado, cantidad;
    private boolean status, verificando, variable;
    private String usuarioOnline;
    private int c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_pupuseria);

        //btnAgregar = findViewById(R.id.btnAgregar);
        btnVolver = findViewById(R.id.btnSelect);
        tvPupuseria = findViewById(R.id.tvPupuseria);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvCelular = findViewById(R.id.tvCelular);
        tvEstado = findViewById(R.id.tvEstado);
        tvEstado.setText("");

        Intent i = this.getIntent();

        tvPupuseria.setText(i.getStringExtra("Pupuseria"));
        tvDireccion.setText(i.getStringExtra("Direccion"));
        tvTelefono.setText(i.getStringExtra("Telefono"));
        tvCelular.setText(i.getStringExtra("Celular"));

        SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        usuarioOnline = prefs.getString("usuario", "");

        variable = verificar();

        if (verificando == false){
            //btnAgregar.setEnabled(variable==false);
            tvEstado.setText("Ya ha sido agregada.");
        }

    }

    private boolean verificar(){
        verificando = false;
        final String nombrePupuseria =tvPupuseria.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"verificandoExistencia.php";
        RequestParams parametros = new RequestParams();
        parametros.put("usuario", usuarioOnline);
        parametros.put("pupuseria", nombrePupuseria);

        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            cantidad = (jsonArray.getJSONObject(i).getString("Cantidad"));
                            c = Integer.parseInt(cantidad);

                        }
                        if (c == 0){
                            verificando = true;
                        }
                        else {
                            verificando = false;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        return verificando;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent openLista = new Intent(AgregarPupuseriaActivity.this, SeleccionarPupActivity.class);
        AgregarPupuseriaActivity.this.startActivity(openLista);

        finish();
    }

    public void agregarPupuseria(View view) {

        final String nombrePupuseria =tvPupuseria.getText().toString();
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        //String usuario = "admin";


        try {
            final ProgressDialog progressDialog = new ProgressDialog(AgregarPupuseriaActivity.this);
            progressDialog.setMessage("Agregando...");
            progressDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            Conexion connect = new Conexion();
            String url = connect.getUrlDireccion() + "pupPorUsuario.php";
            RequestParams parametros = new RequestParams();
            parametros.put("usuario", usuarioOnline);
            parametros.put("pupuseria", nombrePupuseria);

            client.post(url, parametros, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200){
                        try {
                            String respuesta = new String(responseBody);
                            JSONObject json = new JSONObject(respuesta);
                            if (json.names().get(0).equals("exito")){
                                resultado = json.getString("exito");
                                progressDialog.dismiss();
                                Intent openLista = new Intent(AgregarPupuseriaActivity.this, SeleccionarPupActivity.class);
                                AgregarPupuseriaActivity.this.startActivity(openLista);

                                finish();

                            }
                            else {
                                resultado = "Acceso incorrecto";
                                progressDialog.dismiss();
                                Toast.makeText(AgregarPupuseriaActivity.this, "Error. " + resultado + " " + usuarioOnline +" " + nombrePupuseria, Toast.LENGTH_LONG).show();
                                status = false;
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });

        }
        catch (Exception e) {
            Toast.makeText(AgregarPupuseriaActivity.this, "Revisar conexión a internet", Toast.LENGTH_LONG).show();
        }
    }

    public void volver(View view) {
        finish();
    }
}
