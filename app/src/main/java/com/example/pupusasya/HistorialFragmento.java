package com.example.pupusasya;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HistorialFragmento extends Fragment {
    View vista;
    ListView listado;
    Button boton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragmento_historial, container, false);
        //boton = vista.findViewById(R.id.btnMostrarHist);
        //listado= (ListView) inflater.inflate((R.layout.fragmento_historial), container, false);
        listado = vista.findViewById(R.id.lsHistorial);

        ObtenerDatos();

        return vista;


    }

    public void ObtenerDatos() {

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://localhost/pupusasya/historialPupuseria.php";

        RequestParams parametros = new RequestParams();


        client.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    CargaLista(obtenerDatosJSON(new String(responseBody)));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void CargaLista(ArrayList<String> datos) {
        //ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        //listado.setAdapter(adapter);
    }

    public ArrayList<String> obtenerDatosJSON(String response) {
        ArrayList<String> listado = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            String texto;
            for (int i = 0; i < jsonArray.length(); i++) {
                texto = jsonArray.getJSONObject(i).getString("IdHistorial") + " " +
                        jsonArray.getJSONObject(i).getString("Detalle") + " " +
                        jsonArray.getJSONObject(i).getString("PrecioTotal") + " " +
                        jsonArray.getJSONObject(i).getString("Delivery") + " " +
                        jsonArray.getJSONObject(i).getString("Nombre") + " " +
                        jsonArray.getJSONObject(i).getString("Estado") + " ";
                listado.add(texto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listado;
    }

}



