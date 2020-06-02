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


/**
 * A simple {@link Fragment} subclass.
 */
public class ListaPupusasFragment extends Fragment {

    public ListaPupusasFragment() {
        // Required empty public constructor
    }
    View vista;
    private ArrayList arrIdProducto, arrNombre, arrPrecio;
    private ListView lista;
    private String idPupSeleccionada;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista= inflater.inflate(R.layout.fragment_lista_pupusas, container, false);

        lista = (ListView) vista.findViewById(R.id.lvEspecialidades);
        arrIdProducto = new ArrayList();
        arrNombre = new ArrayList();
        arrPrecio = new ArrayList();

        pupuseriaDB transaction = new pupuseriaDB(getContext() , "pupusasYa", null, 1);
        SQLiteDatabase bd = transaction.getWritableDatabase();

        Cursor fila = bd.rawQuery("SELECT idPupuseria FROM pupuseriaSelected ORDER BY idPupuseria DESC LIMIT 1", null);
        String retorno= "";
        if(fila.moveToFirst()){
            retorno= fila.getString(0);

            cargarEspecialidades(retorno);
            this.idPupSeleccionada = retorno;
        }
        else {
            Toast.makeText(getContext(), "No funciona...dentro del if", Toast.LENGTH_LONG).show();
        }

        bd.close();

        return vista;
    }


    private void cargarEspecialidades(final String idPupuseria) {
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
        parametros.put("idTipo", "1");

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
                                mostrarDialogoPersonalizado(arrNombre.get(position).toString(), idPup, envio, idPro);


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


    private class CustonAdater extends BaseAdapter {
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

            etIdPupusa.setText(arrIdProducto.get(position).toString());
            etPupusa.setText(arrNombre.get(position).toString());
            etPrecio.setText(arrPrecio.get(position).toString());

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

    private void mostrarDialogoPersonalizado(String especialidad, int idPup, String precio, final String idPro) {
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
                sub.setText(String.valueOf(calculo));
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

                pedidosDB transaction = new pedidosDB(getContext() , "pupusasYa", null, 1);
                SQLiteDatabase bd = transaction.getWritableDatabase();
                //transaction.onUpgrade(bd, 1, 1);
                Cursor fila = bd.rawQuery("SELECT MAX(idPedido) FROM pedido", null);
                String retorno= "";
                if (fila != null && fila.getCount() > 0){
                    if(fila.moveToFirst()){
                        retorno= fila.getString(0);
                        //if (retorno.isEmpty()) retorno = "0";
                        retorno = String.valueOf((Integer.parseInt(retorno)) + 1);
                        Toast.makeText(getContext(), "retorno dentro = " + retorno,  Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "No funciona...dentro del if", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    retorno = "1";
                    Toast.makeText(getContext(), "valor = " + retorno,  Toast.LENGTH_LONG).show();
                }

                bd.close();

                //idPedido integer primary key, usuario integer, idPupuseria integer, idProducto integer, cantidad integer
                //DB trans = new pupuseriaDB(getContext() , "pupusasYa", null, 1);
                SQLiteDatabase bDatos = transaction.getWritableDatabase();
                //transaction.onUpgrade(bd, 1, 1);
                ContentValues registro = new ContentValues();
                registro.put("idPedido", "8");
                registro.put("usuario", usuarioOnline);
                registro.put("idPupuseria", idPupSeleccionada);
                registro.put("idProducto", idPro);
                registro.put("cantidad", cantidad);
                bDatos.insert("pedido", null, registro);
                bDatos.close();

                //para prueba


                pedidosDB transaction2 = new pedidosDB(getContext() , "pupusasYa", null, 1);
                SQLiteDatabase bData = transaction2.getWritableDatabase();
                Cursor fila2 = bData.rawQuery("SELECT MAX(idPedido) FROM pedido", null);
                String retor= "";
                if (fila2 != null && fila2.getCount() > 0){
                    if(fila2.moveToFirst()){
                        retor= fila2.getString(0);
                        //Toast.makeText(getContext(), "valor encontrado = " + retor,  Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "No funciona...dentro del if", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    retor = "Ninguno";
                    Toast.makeText(getContext(), "valor = " + retor,  Toast.LENGTH_LONG).show();
                }

                bData.close();

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
