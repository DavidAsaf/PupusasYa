package com.example.pupusasya;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pupusasya.Clases.Conexion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegistroActivity extends AppCompatActivity
        //implements View.OnClickListener
        {
    private EditText textcorreo;
    private EditText textcontra;
    private EditText textnombre;
    private EditText textapellido;
    private EditText textdireccion;
    private EditText texttelefonno;
    private String user, pasw, url, resultado, n, a;
    private boolean status = false;

    private Button btnRegistrar;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        //inicializamos el objeto firebaseauth
        firebaseAuth = FirebaseAuth.getInstance();
        textcorreo = (EditText) findViewById(R.id.txtcorreo);
        textcontra = (EditText) findViewById(R.id.txtcontra);
        textnombre = (EditText) findViewById(R.id.txtnombre);
        textapellido = (EditText) findViewById(R.id.txtapellido);
        textdireccion = (EditText)findViewById(R.id.txtdireccion);
        texttelefonno = (EditText)findViewById(R.id.txttelefono);
        btnRegistrar = (Button) findViewById(R.id.btnregistro);
        progressDialog = new ProgressDialog(this);
    //    btnRegistrar.setOnClickListener(this);
    }

    private void registrarUsuario() {
    //obtenemos el email y contra desde las cajas de texto
    String email = textcorreo.getText().toString();
       String password = textcontra.getText().toString();
     //   varifiacion de cajas de texto
        if (TextUtils.isEmpty(email)) {
           Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }
       if (TextUtils.isEmpty(password)) {
         Toast.makeText(this, "Falta ingresar una contraseña", Toast.LENGTH_LONG).show();
       return;
       }
       progressDialog.setMessage("Realizando registro");
        progressDialog.show();

        //creando el nuevo usuario

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistroActivity.this, "Se ha registrado el email", Toast.LENGTH_LONG).show();
                  Intent open = new Intent(RegistroActivity.this, BienvenidaActivity.class);
                    RegistroActivity.this.startActivity(open);
                    finish();
                } else {
                    Toast.makeText(RegistroActivity.this, "No se pudo registrar el usuario, posiblemente ya existe", Toast.LENGTH_LONG).show();
               }
                progressDialog.dismiss();
            }
       });
    }
     public void SignUpCli1(View view) {
           Intent open = new Intent(RegistroActivity.this, LoginCli.class);
           RegistroActivity.this.startActivity(open);
       }

 //  @Override
//public void onClick(View v) {
      //invocamos al metodo
   //     registrarUsuario();
   //   try {
     //          if(veryfyEt() == true)
    //           initSignUp();
     //  } catch (Exception e) { mensaje("Ups... Parece que no tienes conexión a internet"); }

  //  }
    public void Save(View view) {
        registrarUsuario();
        try {
            if(veryfyEt() == true)
                initSignUp();
        } catch (Exception e) { mensaje("Ups... Parece que no tienes conexión a internet"); }

    }

    //mysql

    private boolean veryfyEt(){
        boolean v;
        if (textnombre.getText().toString().isEmpty() || textapellido.getText().toString().isEmpty() || textdireccion.getText().toString().isEmpty()
                || texttelefonno.getText().toString().isEmpty() || textcontra.getText().toString().isEmpty() || textcorreo.getText().toString().isEmpty()
        ){

            mensaje("Uno de los campos está vacío.");
            v = false;
        }
        else  {
            v = true;
        }

        return v;
    }

    private void mensaje(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);
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
    //public void Save(View view) {
      //  try {
        //    if(veryfyEt() == true ) initSignUp();
       // } catch (Exception e) { mensaje("Ups... Parece que no tienes conexión a internet"); }

    //}
    private void initSignUp(){
        String name = textnombre.getText().toString();
        String lastName = textapellido.getText().toString();
        String email = textcorreo.getText().toString();
        String usuario = textcorreo.getText().toString();
        String phone = texttelefonno.getText().toString();
        String address = textdireccion.getText().toString();
        String password = textcontra.getText().toString();

            try {
                AsyncHttpClient client = new AsyncHttpClient();
                Conexion connect = new Conexion();
                String url = connect.getUrlDireccion() + "guardarregistro.php";
                RequestParams parametros = new RequestParams();
                parametros.put("nombre", name);
                parametros.put("apellido", lastName);
                parametros.put("direccion", address);
                parametros.put("celular", phone);
                parametros.put("email", email);
                parametros.put("usuario",usuario);
                parametros.put("contrasennia", password);

                client.post(url, parametros, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200){
                            try {
                                String respuesta = new String(responseBody);
                                JSONObject json = new JSONObject(respuesta);
                                if (json.names().get(0).equals("exito")){
                                    resultado = json.getString("exito");
                                    //Toast.makeText(SignUpCli.this, resultado, Toast.LENGTH_LONG).show();
                                    status = true;
                                }
                                else {
                                    resultado = "Acceso incorrecto";
                                    Toast.makeText(RegistroActivity.this, resultado, Toast.LENGTH_LONG).show();
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

                if (status == true){
                  mensaje("Usuario creado con éxito.");
                    Intent openLogin = new Intent(RegistroActivity.this, LoginCli.class);
                    RegistroActivity.this.startActivity(openLogin);
                    finish();
                }
            }
            catch (Exception e) {
                Toast.makeText(RegistroActivity.this, "Revisar conexión a internet", Toast.LENGTH_LONG).show();
            }
        }

    }


