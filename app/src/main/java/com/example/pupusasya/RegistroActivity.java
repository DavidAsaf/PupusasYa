package com.example.pupusasya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {
 private EditText textcorreo;
 private EditText textcontra;
 private Button btnRegistrar;
 private ProgressDialog progressDialog;
 private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
//inicializamos el objeto firebaseauth
firebaseAuth = FirebaseAuth.getInstance();
textcorreo = (EditText)findViewById(R.id.txtcorreo);
textcontra = (EditText) findViewById(R.id.txtcontra);
btnRegistrar = (Button)findViewById(R.id.btnregistro);
progressDialog = new ProgressDialog(this);
btnRegistrar.setOnClickListener(this);
    }

       private void registrarUsuario(){
        //obtenemos el email y contra desde las cajas de texto
        String email = textcorreo.getText().toString();
        String password = textcontra.getText().toString();
      //varifiacion de cajas de texto
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
       return;
        }
           if (TextUtils.isEmpty(password)){
               Toast.makeText(this,"Falta ingresar una contraseña",Toast.LENGTH_LONG).show();
               return;
           }
           progressDialog.setMessage("Realizando registro");
           progressDialog.show();

           //creando el nuevo usuario

           firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful()){
                       Toast.makeText(RegistroActivity.this,"Se ha registrado el email",Toast.LENGTH_LONG).show();
                   }else{
                       Toast.makeText(RegistroActivity.this,"No se pudo registrar el usuario, posiblemente ya existe",Toast.LENGTH_LONG).show();
                   }
                   progressDialog.dismiss();
               }
           });
}
   // public void SignUpCli1(View view) {
 //       Intent open = new Intent(RegistroActivity.this, LoginCli.class);
 //       RegistroActivity.this.startActivity(open);
 //   }

    @Override
    public void onClick(View v) {
        //invocamos al metodo
        registrarUsuario();
    }
}
