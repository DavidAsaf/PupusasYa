package com.example.pupusasya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pupusasya.Clases.Conexion;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private TextView m;
    private EditText k;
    private String nameCust, lastNameCust, addressCust, phoneCust, emailCust, idCust;
    public String usuarioOnline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.abrir_navegacion, R.string.cerrar_navegacion);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState==null){

            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                    new PupuseriasFragmento()).commit();
            navigationView.setCheckedItem(R.id.nav_restaurante);
        }

        Intent i = this.getIntent();
        nameCust = i.getStringExtra("Usuario");
        //m.setText("Hola " + nameCust + " " + lastNameCust + "!");



        View hView = navigationView.getHeaderView(0);
        TextView user = (TextView) hView.findViewById(R.id.userNameMenu);
        user.setText(nameCust);

        TextView email = (TextView) hView.findViewById(R.id.userEmailMenu);
        email.setText(nameCust);
        navigationView.setNavigationItemSelectedListener(this);

        //Esto es como un tipo variable de sesion, se guardan los datos para usarlos en otro activity.
        SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("usuario", nameCust);
        editor.commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_bienvenida:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new BienvenidaFragmento()).commit();
                break;

            case R.id.nav_restaurante:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new PupuseriasFragmento()).commit();
                break;
            case R.id.nav_historial:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new HistorialFragmento()).commit();
                //cargarPupuserias();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new ChatFragmento()).commit();
                break;
            case R.id.nav_salir:
                Intent open = new Intent(getApplicationContext(), LoginCli.class);
                startActivity(open);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        }

}
