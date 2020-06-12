package com.example.pupusasya;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pupusasya.Clases.Conexion;
import com.example.pupusasya.Clases.pupuseriaDB;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class PrePedido1Fragment extends Fragment {

    public PrePedido1Fragment() {
        // Required empty public constructor
    }

    View vista;
    private TextView tvTotal;
    private Button continuar;
    private RadioButton rbDelivery, rbLlevar;
    private ArrayList arrIdCarrito,arrIdProducto, arrPrecio, arrProducto, arrCantidad;
    private String idPupSeleccionadaPre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_pre_pedido1, container, false);

        arrIdCarrito = new ArrayList();
        arrIdProducto = new ArrayList();
        arrPrecio = new ArrayList();
        arrProducto = new ArrayList();
        arrCantidad = new ArrayList();

        Bundle datosRecuperados = getArguments();
        double total = datosRecuperados.getDouble("total");

        tvTotal = vista.findViewById(R.id.tvTotalPre);
        tvTotal.setText("Total: $" + String.format("%.2f", total));

        rbDelivery = vista.findViewById(R.id.rdDelivery);
        rbLlevar = vista.findViewById(R.id.rdLlevar);
        continuar = vista.findViewById(R.id.btnContinuar);

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rbDelivery.isChecked()){
                    mostrarDialogoBasico2();
                }
                else {
                    mostrarDialogoBasico();
                }
            }
        });

        pupuseriaDB transaction = new pupuseriaDB(getContext() , "pupusasYa", null, 1);
        SQLiteDatabase bd = transaction.getWritableDatabase();

        Cursor fila = bd.rawQuery("SELECT idPupuseria FROM pupuseriaSelected ORDER BY idPupuseria DESC LIMIT 1", null);
        String retorno= "";
        if(fila.moveToFirst()){
            retorno= fila.getString(0);

            this.idPupSeleccionadaPre = retorno;

        }
        else {
            Toast.makeText(getContext(), "No funciona...dentro del if", Toast.LENGTH_LONG).show();
        }

        bd.close();


        return vista;
    }

    private void mostrarDialogoBasico(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmar realizar pedido");
        builder.setMessage("¿Deseas enviar el pedido para ir a recogerlo?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                        //Toast.makeText(getContext(), "Siiiiiiiiiii", Toast.LENGTH_SHORT).show();
                        hacerPedido("0");
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                    }
                }).show();
    }

    private void mostrarDialogoBasico2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmar realizar pedido");
        builder.setMessage("¿Deseas que tu pedido sea por delivery?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                        hacerPedido("1");
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                    }
                }).show();
    }

    private void hacerPedido(String deliv) {
        guardarPedido(deliv, "0.0");
        traerCarrito(this.idPupSeleccionadaPre, deliv);
    }

    private void traerCarrito(String idPupuseria, final String deliv) {
        arrIdCarrito.clear();
        arrIdProducto.clear();
        arrPrecio.clear();
        arrCantidad.clear();
        arrProducto.clear();
        //sumaTotal = 0.0;

        final int idPup = Integer.parseInt(idPupuseria.trim());
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        final String usuarioOnline = prefs.getString("usuario", "");

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando Datos...");
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"mostrarCarrito.php";
        RequestParams parametros = new RequestParams();
        parametros.put("usuario", usuarioOnline.trim());
        parametros.put("idPupuseria", idPupuseria.trim());


        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrIdCarrito.add(jsonArray.getJSONObject(i).getString("idCarrito"));
                            arrIdProducto.add(jsonArray.getJSONObject(i).getString("idProducto"));
                            arrCantidad.add(jsonArray.getJSONObject(i).getString("cantidad"));
                            guardarDetallePedido(arrCantidad.get(i).toString(), arrIdProducto.get(i).toString(),
                                    usuarioOnline, arrIdCarrito.get(i).toString());
                        }

                        progressDialog.dismiss();
                        final int enviar = Integer.parseInt(deliv.trim());
                        mostrarPantallaExito(enviar);

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

    private void guardarPedido (String delivery, String total) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Spinner spinner = vista.findViewById(R.id.spinner4);
        //String cantidad = spinner.getSelectedItem().toString();
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String usuarioOnline = prefs.getString("usuario", "");
        String idPedido = "";

        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"insertarPedido.php";
        RequestParams parametros = new RequestParams();
        //'$usuario', $idPupuseria, $delivery, $total
        parametros.put("usuario", usuarioOnline);
        parametros.put("idPupuseria", this.idPupSeleccionadaPre);
        parametros.put("delivery", delivery.trim());
        parametros.put("total", total);


        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {
                        //Toast.makeText(getContext(), "Agregado con éxito", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


        dialog.dismiss();
    }

    private void guardarDetallePedido(String cantidad, String idPro, String usu, String idCarrito) {
        //'$usuario', $cantidad, $idProducto

        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"insertarDetallePedido.php";
        RequestParams parametros = new RequestParams();

        parametros.put("usuario", usu.trim());
        parametros.put("cantidad", cantidad.trim());
        parametros.put("idProducto", idPro.trim());
        parametros.put("idCarrito", idCarrito.trim());


        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {
                        //Toast.makeText(getContext(), "Agregado con éxito", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void mostrarPantallaExito(int funcion) {
        Bundle datoEnviar = new Bundle();
        //final double env = Double.parseDouble(tvTotal.getText().toString().trim());
        datoEnviar.putInt("funcion", funcion);

        Fragment fragment = new PedidoRealizadoFragment();
        fragment.setArguments(datoEnviar);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}
