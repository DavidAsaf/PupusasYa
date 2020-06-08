package com.example.pupusasya;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pupusasya.Clases.Conexion;
import com.example.pupusasya.Clases.pedidosDB;
import com.example.pupusasya.Clases.pupuseriaDB;
import com.example.pupusasya.Model.DialogoCantidad;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CarritoFragment extends Fragment {

    View vista;
    private ArrayList arrIdCarrito,arrIdProducto, arrPrecio, arrProducto, arrCantidad;
    private ListView lista;
    private String idPupSeleccionada;
    private Double sumaTotal=0.0;
    TextView tvTotal;
    private Button btnAvance;

    public CarritoFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_carrito, container, false);

        lista = (ListView) vista.findViewById(R.id.lvCarri);
        arrIdCarrito = new ArrayList();
        arrIdProducto = new ArrayList();
        arrPrecio = new ArrayList();
        arrProducto = new ArrayList();
        arrCantidad = new ArrayList();

        tvTotal = vista.findViewById(R.id.tvTotalCarrito);
        btnAvance = (Button) vista.findViewById(R.id.btnAvanzar);

        pupuseriaDB transaction = new pupuseriaDB(getContext() , "pupusasYa", null, 1);
        SQLiteDatabase bd = transaction.getWritableDatabase();

        Cursor fila = bd.rawQuery("SELECT idPupuseria FROM pupuseriaSelected ORDER BY idPupuseria DESC LIMIT 1", null);
        String retorno= "";
        if(fila.moveToFirst()){
            retorno= fila.getString(0);

            mostrarCarrito(retorno);

            //final String tot = "Total: " + String.valueOf(mostrarCarrito(retorno));

            this.idPupSeleccionada = retorno;

        }
        else {
            Toast.makeText(getContext(), "No funciona...dentro del if", Toast.LENGTH_LONG).show();
        }

        bd.close();

        //boton avance

        btnAvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle datoEnviar = new Bundle();
                //final double env = Double.parseDouble(tvTotal.getText().toString().trim());
                datoEnviar.putDouble("total", sumaTotal);

                Fragment fragment = new PrePedido1Fragment(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                fragment.setArguments(datoEnviar);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return vista;



    }

    private void mostrarCarrito(String idPupuseria) {
        arrIdCarrito.clear();
        arrIdProducto.clear();
        arrPrecio.clear();
        arrCantidad.clear();
        arrProducto.clear();
        sumaTotal = 0.0;

        final int idPup = Integer.parseInt(idPupuseria.trim());
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String usuarioOnline = prefs.getString("usuario", "");

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
                            arrPrecio.add(jsonArray.getJSONObject(i).getString("Precio"));
                            arrProducto.add(jsonArray.getJSONObject(i).getString("Producto"));
                            arrCantidad.add(jsonArray.getJSONObject(i).getString("cantidad"));
                            sumaTotal = sumaTotal + (Double.parseDouble(arrPrecio.get(i).toString().trim()) * Double.parseDouble(arrCantidad.get(i).toString().trim()));
                        }

                        lista.setAdapter(new CarritoFragment.CustonAdater(getActivity().getApplicationContext(),
                                arrIdCarrito, arrIdProducto, arrPrecio, arrProducto, arrCantidad));

                        final CarritoFragment.CustonAdater CustonAdater =
                                new CarritoFragment.CustonAdater(CarritoFragment.this.getActivity().getApplicationContext(),
                                        arrIdCarrito, arrIdProducto, arrPrecio, arrProducto, arrCantidad);
                        lista.setAdapter(CustonAdater);
                        progressDialog.dismiss();

                        tvTotal.setText("Total: $" + String.format("%.2f", sumaTotal));

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                //final String envio = arrPrecio.get(position).toString().trim();
                                //final String idPro = arrIdProducto.get(position).toString().trim();
                                //insertar_aCarrito(arrNombre.get(position).toString(), idPup, envio, idPro);


                            }
                        });
                        //tvTotal.setText("");


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


    private class CustonAdater extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        TextView tvIdCarrito, tvIdProducto, tvPrecio, tvProducto, tvCantidad, tvSubTotal;
                                                    //arrIdCarrito, arrIdProducto, arrPrecio, arrProducto, arrCantidad
        public CustonAdater(Context applicationContext, ArrayList arrIdCarrito, ArrayList arrIdProducto,
                            ArrayList arrPrecio, ArrayList arrProducto, ArrayList arrCantidad) {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) this.ctx.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return arrProducto.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.lista_carrito, null);
            tvIdCarrito = (TextView) viewGroup.findViewById(R.id.tvIdCarrito);
            tvIdProducto = (TextView) viewGroup.findViewById(R.id.tvIdProducto);
            tvPrecio = (TextView) viewGroup.findViewById(R.id.tvPrecioCarrito);
            tvCantidad = (TextView) viewGroup.findViewById(R.id.tvCantidad);
            tvSubTotal = (TextView) viewGroup.findViewById(R.id.tvSubTotal);
            tvProducto = (TextView) viewGroup.findViewById(R.id.tvProducto);
            tvTotal = (TextView) viewGroup.findViewById(R.id.tvTotalCarrito);

            tvIdCarrito.setText(arrIdCarrito.get(position).toString());
            tvIdProducto.setText(arrIdProducto.get(position).toString());
            tvPrecio.setText(arrPrecio.get(position).toString());
            tvCantidad.setText(arrCantidad.get(position).toString());
            tvProducto.setText(arrProducto.get(position).toString());
            //sumaTotal = sumaTotal + (Double.parseDouble(arrPrecio.get(position).toString()))*(Double.parseDouble(arrCantidad.get(position).toString()));
            tvSubTotal.setText(String.valueOf((Double.parseDouble(arrPrecio.get(position).toString()))*(Double.parseDouble(arrCantidad.get(position).toString()))));



            return viewGroup;
        }

    }

}
