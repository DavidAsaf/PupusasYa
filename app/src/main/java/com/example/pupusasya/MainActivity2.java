package com.example.pupusasya;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private TextView m;
    private EditText k;
    private String nameCust, lastNameCust, addressCust, phoneCust, emailCust, idCust;

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
                    new BienvenidaFragmento()).commit();
            navigationView.setCheckedItem(R.id.nav_bienvenida);
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_bienvenida:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new BienvenidaFragmento()).commit();
                break;
            case R.id.nav_inscripcion:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new InscripcionFragmento()).commit();
                break;
            case R.id.nav_restaurante:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new PupuseriasFragmento()).commit();
                break;
            case R.id.nav_historial:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new HistorialFragmento()).commit();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                        new ChatFragmento()).commit();
                break;
            case R.id.nav_salir:
                Toast.makeText(this, "Salir", Toast.LENGTH_SHORT).show();
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

    public void listaPupuserias(View view) {
        Intent openMain = new Intent(MainActivity2.this, SeleccionarPupActivity.class);
        MainActivity2.this.startActivity(openMain);

    }
}
