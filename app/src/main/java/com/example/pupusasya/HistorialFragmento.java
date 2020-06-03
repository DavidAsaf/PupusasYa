package com.example.pupusasya;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.PrecomputedText;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pupusasya.Clases.Conexion;
import com.example.pupusasya.Clases.pupuseriaDB;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HistorialFragmento extends Fragment {
    private ListView lista;
    private static ArrayList Detalle;
    private ArrayList IdHistorial;
    private static ArrayList PrecioTotal;
    private static ArrayList Delivery;
    private static ArrayList Nombre;
    private static ArrayList Estado;
    private TextView m;
    private EditText txtBuscarHist;
    private String id, resultado;
    private boolean status = false;
    private Intent intent;
    View vista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista= inflater.inflate(R.layout.fragmento_historial, container, false);

        lista = (ListView) vista.findViewById(R.id.listaHistorial);
        txtBuscarHist = vista.findViewById(R.id.etBuscarHist);
        IdHistorial = new ArrayList();
        Detalle = new ArrayList();
        PrecioTotal = new ArrayList();
        Delivery = new ArrayList();
        Nombre = new ArrayList();
        Estado = new ArrayList();

        cargarPupuserias();

        txtBuscarHist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = txtBuscarHist.getText().toString();
                //SearchPup(name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return vista;
    }

    public void cargarPupuserias(){
        IdHistorial.clear();
        Detalle.clear();
        PrecioTotal.clear();
        Delivery.clear();
        Nombre.clear();
        Estado.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando Datos...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"historialPupuseria.php";
        RequestParams parametros = new RequestParams();

        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            IdHistorial.add(jsonArray.getJSONObject(i).getString("IdHistorial"));
                            Detalle.add(jsonArray.getJSONObject(i).getString("Detalle"));
                            PrecioTotal.add(jsonArray.getJSONObject(i).getString("PrecioTotal"));
                            Delivery.add(jsonArray.getJSONObject(i).getString("Delivery"));
                            Nombre.add(jsonArray.getJSONObject(i).getString("Nombre"));
                            Estado.add(jsonArray.getJSONObject(i).getString("Estado"));
                            //Toast.makeText(getContext(), jsonArray.getJSONObject(i).getString("Nombre"), Toast.LENGTH_LONG).show();
                        }

                        lista.setAdapter(new HistorialFragmento.CustonAdater(getActivity().getApplicationContext(), Detalle, PrecioTotal, Delivery, Nombre, Estado));

                        final HistorialFragmento.CustonAdater CustonAdater =
                                new HistorialFragmento.CustonAdater(HistorialFragmento.this.getActivity().getApplicationContext(), Detalle, PrecioTotal, Delivery, Nombre, Estado);
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
        TextView etDetalle, etPrecioTo, etid, etDelivery, etNombre, etEstado;
        Button btnSelect;

        public CustonAdater(Context applicationContext, ArrayList Detalle, ArrayList PrecioTotal, ArrayList Delivery, ArrayList Nombre, ArrayList Estado) {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) this.ctx.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return Nombre.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.listahistorial, null);
            etDetalle = (TextView) viewGroup.findViewById(R.id.tvDetalle);
            etPrecioTo = (TextView) viewGroup.findViewById(R.id.tvPrecioTo);
            etDelivery = (TextView) viewGroup.findViewById(R.id.tvDelivery);
            etNombre = (TextView) viewGroup.findViewById(R.id.etNombre2);
            etEstado = (TextView) viewGroup.findViewById(R.id.etEstado);

            etDetalle.setText(Detalle.get(position).toString());
            etPrecioTo.setText(PrecioTotal.get(position).toString());
            etDelivery.setText(Delivery.get(position).toString());
            etNombre.setText(Nombre.get(position).toString());
            etEstado.setText(Estado.get(position).toString());
            return viewGroup;
        }
    }
}
