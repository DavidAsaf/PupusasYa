package com.example.pupusasya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class LoginCli extends AppCompatActivity {

    private EditText usuario;
    private EditText clave;
    private String user, pasw, url, resultado, n, d, di, e, t, c, a;
    private boolean status = false;
    private String nameCust, lastNameCust, addressCust, phoneCust, emailCust, idCust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_cli);


        usuario = findViewById(R.id.etUsuario);
        clave = findViewById(R.id.etPassword);

    }

    public void LogInCli(View view) {

        if (usuario.getText().toString().isEmpty()){
            mError("No ha introducido el nombre de usuario.");
        }
        else if (clave.getText().toString().isEmpty()){
            mError("No ha introducido su contraseña.");
        }
        else {
            try {
                if(verifyConexion() == true)initLogIn();
                if(verifyConexion() == false) mError("Ups... parece que no tienes conexión a internet");
            } catch (Exception e) { mError("Ups... Parece que hubo un problema, vuelve a intentarlo.");}
        }
    }

    public void SignUpCli(View view) {
        //Intent open = new Intent(LoginCli.this, SignUpCli.class);
        //LoginCli.this.startActivity(open);
    }

    private boolean verifyConexion(){
        boolean r = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) r = true;
        return r;
    }

    private void mError(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginCli.this);
        builder.setTitle("Aviso").setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initLogIn(){
        user = usuario.getText().toString();
        pasw = clave.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        url = "https://pupusasya.000webhostapp.com/LogIn.php";
        RequestParams parametros = new RequestParams();
        parametros.put("usu", user);
        parametros.put("pas", pasw);
        client.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        String respuesta = new String(responseBody);

                        JSONObject json = new JSONObject(respuesta);
                        if (json.names().get(0).equals("exito")){
                            nameCust = json.getString("Nombre");
                            lastNameCust = json.getString("Apellido");
                            addressCust = json.getString("Direccion");
                            phoneCust = json.getString("Celular");
                            emailCust = json.getString("Email");
                            idCust = json.getString("IdCliente");
                            status = true;
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginCli.this);
                            builder.setTitle("Aviso").setMessage("Usuario o contraseña incorrectos.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            status = false;
                        }

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure (int statusCode, Header[] headers, byte[] responseBody, Throwable error){

            }
        });

        if (status == true){
            Intent openMain = new Intent(LoginCli.this, MainActivity.class);
            openMain.putExtra("Nombre", nameCust);
            openMain.putExtra("Apellido", lastNameCust);
            openMain.putExtra("Direccion", addressCust);
            openMain.putExtra("Telefono", phoneCust);
            openMain.putExtra("Email", emailCust);
            openMain.putExtra("IdCliente", idCust);
            LoginCli.this.startActivity(openMain);
            usuario.setText("");
            clave.setText("");
            finish();
        }
    }

}
