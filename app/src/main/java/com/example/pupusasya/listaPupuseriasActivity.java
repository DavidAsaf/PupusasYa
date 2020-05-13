package com.example.pupusasya;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pupusasya.Model.pupuserias;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class listaPupuseriasActivity extends AppCompatActivity {


    private ListView lvDatos;
    private ArrayList nombre, precio, idproduto;
    private TextView m;
    private EditText txtBuscarPup;
    private String id, resultado;
    private boolean status = false;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pupuserias);

        lvDatos = (ListView) findViewById(R.id.listaPupuserias);
        //txtBuscarPup = findViewById(R.id.etBuscarPup);
        nombre = new ArrayList();
        //precio = new ArrayList();
        idproduto = new ArrayList();
        obtenerPupuserias();


    }

    private void obtenerPupuserias(){
        String url = "http://192.168.1.8/pupusasya/allPupuserias.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200) {
                    listarPupusas(new String (responseBody));
                    Toast.makeText(listaPupuseriasActivity.this, "Exito 200", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(listaPupuseriasActivity.this, "Fallo 200", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void listarPupusas(String respuesta) {
        ArrayList<pupuserias> lista = new ArrayList<pupuserias>();
        try {
                JSONArray jsonArray = new JSONArray(respuesta);
                for (int i=0; i < jsonArray.length(); i++){
                    pupuserias p = new pupuserias();
                    p.setId(jsonArray.getJSONObject(i).getInt("IdPupuseria"));
                    p.setPupuseria(jsonArray.getJSONObject(i).getString("Nombre"));

                    lista.add(p);

                }

            ArrayAdapter<pupuserias> a = new ArrayAdapter(this,R.layout.activity_lista_pupuserias);
                lvDatos.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
