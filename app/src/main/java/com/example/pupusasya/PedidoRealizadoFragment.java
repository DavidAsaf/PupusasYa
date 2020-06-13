package com.example.pupusasya;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pupusasya.Clases.Conexion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class PedidoRealizadoFragment extends Fragment {

    public PedidoRealizadoFragment() {
        // Required empty public constructor
    }

    View vista;
    private Button btnIr;
    private ArrayList arrTotal,arrPupuseria;
    private TextView txtTotal, txtPupuseria, txtFuncion;
    private String pupuseria;
    private Double total=0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_pedido_realizado, container, false);

        txtTotal = vista.findViewById(R.id.tvTotalPedido);
        txtPupuseria = vista.findViewById(R.id.tvPupuseriaPedido);
        txtFuncion = vista.findViewById(R.id.tvFuncion);
        arrTotal = new ArrayList();
        arrPupuseria = new ArrayList();
        btnIr = vista.findViewById(R.id.btnIrPedidos);

        Bundle datosRecuperados = getArguments();
        int total = datosRecuperados.getInt("funcion");

        if (total == 0) {
            txtFuncion.setText("A recoger");
        }
        else {
            txtFuncion.setText("Delivery");
        }

        btnIr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new HistorialFragmento();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        traerDatos();

        return vista;
    }


    private void traerDatos() {
        arrTotal.clear();
        arrPupuseria.clear();

        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        final String usuarioOnline = prefs.getString("usuario", "");

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"mostrarDetallePedido.php";
        RequestParams parametros = new RequestParams();
        parametros.put("usuario", usuarioOnline.trim());


        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrTotal.add(jsonArray.getJSONObject(i).getString("Total"));
                            arrPupuseria.add(jsonArray.getJSONObject(i).getString("Pup"));
                            total = Double.parseDouble(arrTotal.get(i).toString().trim());
                            pupuseria = arrPupuseria.get(i).toString();
                        }
                        //"Total: $" + String.format("%.2f", sumaTotal)
                        txtTotal.setText("$" + String.format("%.2f", total));
                        txtPupuseria.setText(pupuseria);

                        progressDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }


}
