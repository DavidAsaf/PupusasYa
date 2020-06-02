package com.example.pupusasya;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pupusasya.Adapter.Pupuserias_Adapter;
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
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PupuseriasFragmento extends Fragment {
    private ListView lista;
    private ArrayList nombre, idPupuseria, departamento, direccion, telefono, celular;
    private TextView m;
    private EditText txtBuscarPup;
    private String id, resultado;
    private boolean status = false;
    private Intent intent;
    View vista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista= inflater.inflate(R.layout.fragmento_pupuserias, container, false);

        lista = (ListView) vista.findViewById(R.id.listaPupuserias);
        txtBuscarPup = vista.findViewById(R.id.etBuscarPup);
        nombre = new ArrayList();
        departamento = new ArrayList();
        idPupuseria = new ArrayList();
        direccion = new ArrayList();
        telefono = new ArrayList();
        celular = new ArrayList();

        cargarPupuserias();

        txtBuscarPup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = txtBuscarPup.getText().toString();
                //SearchPup(name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return vista;
    }

    private void cargarPupuserias(){
        nombre.clear();
        idPupuseria.clear();
        departamento.clear();
        telefono.clear();
        celular.clear();
        direccion.clear();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando Datos...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        Conexion connect = new Conexion();
        String url = connect.getUrlDireccion() +"allPupuserias.php";
        RequestParams parametros = new RequestParams();

        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            idPupuseria.add(jsonArray.getJSONObject(i).getString("IdPupuseria"));
                            nombre.add(jsonArray.getJSONObject(i).getString("Nombre"));
                            departamento.add(jsonArray.getJSONObject(i).getString("Departamento"));
                            direccion.add(jsonArray.getJSONObject(i).getString("Direccion"));
                            telefono.add(jsonArray.getJSONObject(i).getString("Telefono"));
                            celular.add(jsonArray.getJSONObject(i).getString("Celular"));
                            //Toast.makeText(getContext(), jsonArray.getJSONObject(i).getString("Nombre"), Toast.LENGTH_LONG).show();
                        }

                        lista.setAdapter(new PupuseriasFragmento.CustonAdater(getActivity().getApplicationContext(), nombre, departamento, direccion, telefono, celular));

                        final CustonAdater CustonAdater =
                                new CustonAdater(PupuseriasFragmento.this.getActivity().getApplicationContext(), nombre, departamento, direccion, telefono, celular);
                        lista.setAdapter(CustonAdater);
                        progressDialog.dismiss();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                // Crear fragmento de tu clase
                                Fragment fragment = new MainPupuseriaFragment();

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

                                pupuseriaDB borrar = new pupuseriaDB(getContext() , "pupusasYa", null, 1);
                                SQLiteDatabase bdd = borrar.getWritableDatabase(); borrar.onUpgrade(bdd, 1, 1);
                                bdd.close();

                                pupuseriaDB transaction = new pupuseriaDB(getContext() , "pupusasYa", null, 1);
                                SQLiteDatabase bd = transaction.getWritableDatabase(); transaction.onUpgrade(bd, 1, 1);
                                ContentValues registro = new ContentValues();
                                registro.put("idPupuseria", String.valueOf(idPupuseria.get(position)));
                                bd.insert("pupuseriaSelected", null, registro);
                                bd.close();


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
        TextView etnombre, etDepartamento, etid, etDireccion, etTelefono, etCelular;
        Button btnSelect;

        public CustonAdater(Context applicationContext, ArrayList nombre, ArrayList departamento, ArrayList direccion, ArrayList telefono, ArrayList celular) {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) this.ctx.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return nombre.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.listapupusas, null);
            etnombre = (TextView) viewGroup.findViewById(R.id.etPupuseria);
            etDepartamento = (TextView) viewGroup.findViewById(R.id.etDepartamento);
            etDireccion = (TextView) viewGroup.findViewById(R.id.tvDireccion);
            etTelefono = (TextView) viewGroup.findViewById(R.id.tvTelefono);
            etCelular = (TextView) viewGroup.findViewById(R.id.tvCelular);

            etnombre.setText(nombre.get(position).toString());
            etDepartamento.setText(departamento.get(position).toString());
            etDireccion.setText(direccion.get(position).toString());
            etTelefono.setText(telefono.get(position).toString());
            etCelular.setText(celular.get(position).toString());
            return viewGroup;
        }
    }
}
