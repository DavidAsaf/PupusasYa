package com.example.pupusasya;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SeleccionarPupActivity extends AppCompatActivity {
    private ListView lista;
    private ArrayList nombre, precio, idproduto, departamento;
    private TextView m;
    private EditText txtBuscarPup;
    private String id, resultado;
    private boolean status = false;
    private  Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_pup);

        lista = (ListView) findViewById(R.id.listaPupuserias);
        txtBuscarPup = findViewById(R.id.etBuscarPup);
        nombre = new ArrayList();
        departamento = new ArrayList();
        idproduto = new ArrayList();

        downloadData();

        txtBuscarPup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = txtBuscarPup.getText().toString();
                SearchPup(name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void downloadData() {
        //int idpupuseria = Integer.parseInt(id);
        nombre.clear();
        idproduto.clear();
        departamento.clear();

        final ProgressDialog progressDialog = new ProgressDialog(SeleccionarPupActivity.this);
        progressDialog.setMessage("Cargando Datos...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://192.168.1.8/pupusasya/allPupuserias.php";
        RequestParams parametros = new RequestParams();

        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            idproduto.add(jsonArray.getJSONObject(i).getString("IdPupuseria"));
                            nombre.add(jsonArray.getJSONObject(i).getString("Nombre"));
                            departamento.add(jsonArray.getJSONObject(i).getString("Departamento"));
                        }

                        lista.setAdapter(new CustonAdater(getApplicationContext(), nombre, departamento));

                        final CustonAdater CustonAdater = new CustonAdater(SeleccionarPupActivity.this, nombre, departamento);
                        lista.setAdapter(CustonAdater);
                        progressDialog.dismiss();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                /*intent = new Intent(view.getContext(),ProductoEditarActivity.class);
                                intent.putExtra("nombre", String.valueOf(nombre.get(position)));
                                //intent.putExtra("precio", String.valueOf(precio.get(position)));
                                intent.putExtra("id", String.valueOf(idproduto.get(position)));*/


                                //startActivity(intent);
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
//pendiente de revisar los que manda a traer
    private void SearchPup(String name) {
        idproduto.clear();
        nombre.clear();
        departamento.clear();

        final ProgressDialog progressDialog = new ProgressDialog(SeleccionarPupActivity.this);
        progressDialog.setMessage("Cargar Datos...");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://192.168.1.8/pupusasya/pupuseriasByName.php";
        RequestParams parametros = new RequestParams();
        parametros.put("name", name);

        client.get(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    progressDialog.dismiss();
                    try {

                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            idproduto.add(jsonArray.getJSONObject(i).getString("IdPupuseria"));
                            nombre.add(jsonArray.getJSONObject(i).getString("Nombre"));
                            departamento.add(jsonArray.getJSONObject(i).getString("Departamento"));

                        }

                        lista.setAdapter(new CustonAdater(getApplicationContext(), nombre, departamento));

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
        TextView etnombre, etDepartamento, etid;
        Button btnSelect;

        public CustonAdater(Context applicationContext, ArrayList nombre, ArrayList departamento) {
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
            //tnSelect = viewGroup.findViewById(R.id.btnSelect);
            //etid = (TextView) viewGroup.findViewById(R.id.etIdPupuseria);

            etnombre.setText(nombre.get(position).toString());
            //etid.setText(idproduto.get(position).toString());
            etDepartamento.setText(departamento.get(position).toString());

            return viewGroup;
        }



    }


}
