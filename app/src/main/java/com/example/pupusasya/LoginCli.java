package com.example.pupusasya;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.*;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class LoginCli extends AppCompatActivity {

    //UNICAES - David
    //Mostraremos como hacerlo - David

    private EditText usuario;
    private EditText clave;
    private String user, pasw, url, resultado, n, d, di, e, t, c, a;
    private boolean status = false;
    private String nameCust, lastNameCust, addressCust, phoneCust, emailCust, idCust;

    //Nuevo con firebase
    private EditText etUsuario;
    private EditText etClave;
    private Button btnEntrar;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_cli);

        firebaseAuth = FirebaseAuth.getInstance();

        etUsuario = findViewById(R.id.etUsuario);
        etClave = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnLogIn);

        progressDialog = new ProgressDialog(this);

    }

    public void LogInCli(View view) {

            try {
                if(verifyConexion() == true)initLogIn();
                if(verifyConexion() == false) mError("Ups... parece que no tienes conexión a internet");
            } catch (Exception e) { mError("Ups... Parece que hubo un problema, vuelve a intentarlo.");}

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
        final String user = etUsuario.getText().toString().trim();
        String password = etClave.getText().toString().trim();

        if (TextUtils.isEmpty(user)){
            Toast.makeText(this, "Se debe ingresar un usuario", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Iniciando Sesión");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Toast.makeText(LoginCli.this, "Bienvenido " + etUsuario.getText(), Toast.LENGTH_SHORT).show();

                            Intent openMain = new Intent(LoginCli.this, MainActivity.class);
                            openMain.putExtra("Usuario", user);
                            LoginCli.this.startActivity(openMain);
                            etUsuario.setText("");
                            etClave.setText("");
                            finish();
                        }
                        else {
                            Toast.makeText(LoginCli.this, "Usuario incorrecto " + etUsuario.getText(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
