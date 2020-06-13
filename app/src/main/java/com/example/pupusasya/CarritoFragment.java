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

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class CarritoFragment extends Fragment  {

    View vista;
    private ArrayList arrIdCarrito,arrIdProducto, arrPrecio, arrProducto, arrCantidad;
    private ListView lista;
    private String idPupSeleccionada;
    private Double sumaTotal=0.0;
    TextView tvTotal;
    private Button btnAvance, btnEliminarCarrito;

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
        btnEliminarCarrito = (Button) vista.findViewById(R.id.btnEliminarCarrito);

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

        btnEliminarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoBasico();
            }
        });

        return vista;



    }

    private void mostrarDialogoBasico(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmar eliminar");
        builder.setMessage("¿Deseas borrar todo el contenido agregado a esta pupuseria?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                        eliminarCarrito(idPupSeleccionada);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                    }
                }).show();
    }

    private void eliminarCarrito (final String pupuseria) {

        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String usuarioOnline = prefs.getString("usuario", "");

        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"eliminandoCarrito.php";
        RequestParams parametros = new RequestParams();
        parametros.put("idPupuseria", pupuseria);
        parametros.put("usuario", usuarioOnline);


        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {
                        Toast.makeText(getContext(), "Eliminado con éxito", Toast.LENGTH_SHORT).show();

                        Fragment fragment = new PupuseriasFragmento(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

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
                                final String envio = arrPrecio.get(position).toString().trim();
                                final String idPro = arrIdProducto.get(position).toString().trim();
                                editandoCarrito(arrProducto.get(position).toString(), idPup, envio, idPro, arrCantidad.get(position).toString().trim(), arrIdCarrito.get(position).toString().trim());


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
            tvSubTotal.setText(String.format("%.2f", (Double.parseDouble(arrPrecio.get(position).toString()))*(Double.parseDouble(arrCantidad.get(position).toString()))));



            return viewGroup;
        }

    }

    private void editandoCarrito(String especialidad, final int idPup, String precio, final String idPro, final String canti, final String idCarr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();

        vista = inflater.inflate(R.layout.edicion_carrito, null);
        builder.setView(vista);

        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView txtPupuseria = vista.findViewById(R.id.tvProdC);
        TextView txtPrecio = vista.findViewById(R.id.tvPrecioC);
        TextView txtSub = vista.findViewById(R.id.tvSubTotalC);
        final EditText etCantidad = vista.findViewById(R.id.etCantidad);

        etCantidad.setText(canti.trim());
        txtPupuseria.setText(especialidad);
        double pre = Double.parseDouble(precio.trim());
        txtPrecio.setText(String.format("%.2f", pre));
        double calcSubTotal = Double.parseDouble(precio) * Double.parseDouble(canti.trim());
        txtSub.setText(String.format("%.2f", calcSubTotal));

        etCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView pre = vista.findViewById(R.id.tvPrecioC);
                TextView sub = vista.findViewById(R.id.tvSubTotalC);
                String cantt = etCantidad.getText().toString().trim();

                if (TextUtils.isEmpty(cantt)){
                    sub.setText("");
                    sub.setText(String.format("%.2f", 0.0));
                }
                else {
                    double calculo = Double.parseDouble(etCantidad.getText().toString().trim()) * Double.parseDouble(pre.getText().toString());

                    sub.setText("");
                    sub.setText(String.format("%.2f", calculo));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        Button btnAgregar = vista.findViewById(R.id.btnEditarCarrito);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etCanti = vista.findViewById(R.id.etCantidad);
                String cantidad = etCanti.getText().toString().trim();

                AsyncHttpClient client = new AsyncHttpClient();
                Conexion connect = new Conexion();
                String url = connect.getUrlDireccion() +"editandoCarrito.php";
                RequestParams parametros = new RequestParams();
                parametros.put("idCarrito", idCarr);
                parametros.put("cantidad", cantidad);


                client.get(url, parametros, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {

                            try {
                                Toast.makeText(getContext(), "Actualizado con éxito", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });

                //lista.setAdapter(null);

                dialog.dismiss();
                Fragment fragment = new CarritoFragment(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Button btnBorrar = vista.findViewById(R.id.btnQuitarCarrito);
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncHttpClient client = new AsyncHttpClient();
                Conexion connect = new Conexion();
                String url = connect.getUrlDireccion() +"quitandoIdCarrito.php";
                RequestParams parametros = new RequestParams();
                parametros.put("idCarrito", idCarr);


                client.get(url, parametros, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {

                            try {
                                Toast.makeText(getContext(), "Quitado con éxito", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });

                //lista.setAdapter(null);

                dialog.dismiss();
                Fragment fragment = new CarritoFragment(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Button btnCancelar = vista.findViewById(R.id.btnCancelarCarrito);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

}
