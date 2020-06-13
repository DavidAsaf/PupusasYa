package com.example.pupusasya;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pupusasya.Clases.Conexion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DetalleCompra extends Fragment {

    private ListView lista;
    private static ArrayList Cantidad;
    private static ArrayList Nombre;

    private Intent intent;
    View vista;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista= inflater.inflate(R.layout.fragment_detalle_compra, container, false);

        lista = (ListView) vista.findViewById(R.id.lstCompra);

        Cantidad= new ArrayList();
        Nombre = new ArrayList();


        cargarDetalle();


        return vista;
    }

    public void cargarDetalle(){
        Cantidad.clear();
        Nombre.clear();


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando Datos...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"detalleCompra.php";
        RequestParams parametros = new RequestParams();

        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Cantidad.add(jsonArray.getJSONObject(i).getString("Cantidad"));
                            Nombre.add(jsonArray.getJSONObject(i).getString("Nombre"));
                              }

                        lista.setAdapter(new DetalleCompra.CustonAdater(getActivity().getApplicationContext(),Cantidad, Nombre));

                        final DetalleCompra.CustonAdater CustonAdater =
                                new CustonAdater(DetalleCompra.this.getActivity().getApplicationContext(),  Cantidad, Nombre);
                        lista.setAdapter(CustonAdater);
                        progressDialog.dismiss();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                            }
                        });

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


    static class CustonAdater extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        TextView etCantidad, etNombre;


        public CustonAdater(Context applicationContext, ArrayList Cantidad, ArrayList Nombre) {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) this.ctx.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return Cantidad.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            ViewGroup viewGroup1 = (ViewGroup) layoutInflater.inflate(R.layout.detallehist, null);


            //etDetalle.setText(Detalle.get(position).toString());
            etCantidad.setText(Cantidad.get(position).toString());
            etNombre.setText(Nombre.get(position).toString());

                      return viewGroup1;
        }
    }
}







