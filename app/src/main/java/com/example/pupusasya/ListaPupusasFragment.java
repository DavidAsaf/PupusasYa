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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pupusasya.Clases.Conexion;
import com.example.pupusasya.Clases.pedidosDB;
import com.example.pupusasya.Clases.pupuseriaDB;
import com.example.pupusasya.Model.DialogoCantidad;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;



public class ListaPupusasFragment extends Fragment {

    public ListaPupusasFragment() {
        // Required empty public constructor
    }
    View vista;
    private static ArrayList arrIdProducto;
    private static ArrayList arrNombre;
    private static ArrayList arrPrecio;
    private ListView lista;
    private String idPupSeleccionada;
    private TextView tvTitulo;
    private ImageView mImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista= inflater.inflate(R.layout.fragment_lista_pupusas, container, false);

        lista = (ListView) vista.findViewById(R.id.lvEspecialidades);
        arrIdProducto = new ArrayList();
        arrNombre = new ArrayList();
        arrPrecio = new ArrayList();
        tvTitulo = (TextView) vista.findViewById(R.id.tvIndicador);

        Bundle datosRecuperados = getArguments();
        int idCateg = datosRecuperados.getInt("categoria");
        final String titulo = datosRecuperados.getString("nombre");
        final String idCat = String.valueOf(idCateg);
        tvTitulo.setText(titulo);

        pupuseriaDB transaction = new pupuseriaDB(getContext() , "pupusasYa", null, 1);
        SQLiteDatabase bd = transaction.getWritableDatabase();

        Cursor fila = bd.rawQuery("SELECT idPupuseria FROM pupuseriaSelected ORDER BY idPupuseria DESC LIMIT 1", null);
        String retorno= "";
        if(fila.moveToFirst()){
            retorno= fila.getString(0);

            cargarEspecialidades(retorno, idCat);
            this.idPupSeleccionada = retorno;
        }
        else {
            Toast.makeText(getContext(), "No funciona...dentro del if", Toast.LENGTH_LONG).show();
        }

        bd.close();


        mImageView = (ImageView) vista.findViewById(R.id.fotoEspecialidad);
        if (idCateg == 1){
            mImageView.setImageResource(R.drawable.pupusas);
        }
        else if(idCateg == 2) {
            mImageView.setImageResource(R.drawable.cocacola);
        }
        else if (idCateg == 3) {
            mImageView.setImageResource(R.drawable.tiramisu);
        }
        else {
            mImageView.setImageResource(R.drawable.panconpollo);
        }


        FloatingActionButton fab = vista.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Boton de carrito", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                Fragment fragment = new CarritoFragment(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return vista;
    }


    private void cargarEspecialidades(final String idPupuseria, final String idCategoria) {
        arrIdProducto.clear();
        arrNombre.clear();
        arrPrecio.clear();
        final int idPup = Integer.parseInt(idPupuseria.trim());

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando Datos...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"showEspecialidades.php";
        RequestParams parametros = new RequestParams();
        parametros.put("idPupuseria", idPupuseria.trim());
        parametros.put("idTipo", idCategoria.trim());

        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrIdProducto.add(jsonArray.getJSONObject(i).getString("IdProducto"));
                            arrNombre.add(jsonArray.getJSONObject(i).getString("Nombre"));
                            arrPrecio.add(jsonArray.getJSONObject(i).getString("Precio"));

                        }

                        lista.setAdapter(new ListaPupusasFragment.CustonAdater(getActivity().getApplicationContext(),
                                arrIdProducto, arrNombre, arrPrecio));

                        final CustonAdater CustonAdater =
                                new CustonAdater(ListaPupusasFragment.this.getActivity().getApplicationContext(),
                                        arrIdProducto, arrNombre, arrPrecio);
                        lista.setAdapter(CustonAdater);
                        progressDialog.dismiss();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                final String envio = arrPrecio.get(position).toString().trim();
                                final String idPro = arrIdProducto.get(position).toString().trim();
                                insertar_aCarrito(arrNombre.get(position).toString(), idPup, envio, idPro);


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
        TextView etIdPupusa, etPupusa, etPrecio;

        public CustonAdater(Context applicationContext, ArrayList arrIdProducto, ArrayList arrNombre, ArrayList arrPrecio) {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) this.ctx.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return arrNombre.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.listaespecialidades, null);
            etIdPupusa = (TextView) viewGroup.findViewById(R.id.tvIdPupusa);
            etPupusa = (TextView) viewGroup.findViewById(R.id.tvEspecialidad);
            etPrecio = (TextView) viewGroup.findViewById(R.id.tvPrecio);

            double take = Double.parseDouble(arrPrecio.get(position).toString());

            etIdPupusa.setText(arrIdProducto.get(position).toString());
            etPupusa.setText(arrNombre.get(position).toString());
            etPrecio.setText(String.format("%.2f", take));

            return viewGroup;
        }
    }

    private void mostrarDialogoBasico(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Titulo");
        builder.setMessage("Agregado al carrito exitosamente")
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                    }
                }).show();
    }

    private void insertar_aCarrito(String especialidad, final int idPup, String precio, final String idPro) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();

        vista = inflater.inflate(R.layout.agregar_cantidad, null);
        builder.setView(vista);

        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView txtPupuseria = vista.findViewById(R.id.tvProd);
        TextView txtPrecio = vista.findViewById(R.id.tvPrecio);
        TextView txtSubTotal = vista.findViewById(R.id.tvSubTotal);
        txtPupuseria.setText(especialidad);
        txtPrecio.setText(precio);
        double calcSubTotal = Double.parseDouble(precio) * 1;
        final Spinner spinn = vista.findViewById(R.id.spinner4);
        //Spinner spin = vista.findViewById(R.id.spinner4);


        spinn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView pre = vista.findViewById(R.id.tvPrecio);
                double calculo = Double.parseDouble(spinn.getSelectedItem().toString()) * Double.parseDouble(pre.getText().toString());
                TextView sub = vista.findViewById(R.id.tvSubTotal);
                sub.setText("");
                sub.setText(String.format("%.2f",calculo));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnAgregar = vista.findViewById(R.id.btnAgregarCarrito);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = vista.findViewById(R.id.spinner4);
                String cantidad = spinner.getSelectedItem().toString();
                SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                String usuarioOnline = prefs.getString("usuario", "");
                String idPedido = "";

                AsyncHttpClient client = new AsyncHttpClient();
                Conexion connect = new Conexion();
                String url = connect.getUrlDireccion() +"insertarCarrito.php";
                RequestParams parametros = new RequestParams();
                parametros.put("usuario", usuarioOnline);
                parametros.put("pupuseria", idPup);
                parametros.put("producto", idPro);
                parametros.put("cantidad", cantidad);


                client.get(url, parametros, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {

                            try {
                                Toast.makeText(getContext(), "Agregado con éxito", Toast.LENGTH_SHORT).show();
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
        });

        Button btnCancelar = vista.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

}
