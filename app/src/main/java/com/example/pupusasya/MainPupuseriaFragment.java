package com.example.pupusasya;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pupusasya.Adapter.tiposAdapter;
import com.example.pupusasya.Clases.insertarTipos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainPupuseriaFragment extends Fragment {

    private ListView lista;
    View vista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista= inflater.inflate(R.layout.fragment_main_pupuseria, container, false);

        ArrayList<insertarTipos> datos = new ArrayList<insertarTipos>();
        datos.add(new insertarTipos(R.drawable.pupusas, "Pupusas"));
        datos.add(new insertarTipos(R.drawable.cocacola, "Bebidas"));
        datos.add(new insertarTipos(R.drawable.tiramisu, "Postres"));
        datos.add(new insertarTipos(R.drawable.panconpollo, "Otros"));

        lista = (ListView) vista.findViewById(R.id.listaMenuTipo);
        lista.setAdapter(new tiposAdapter(getActivity().getApplicationContext(), R.layout.lv_main_pupuserias , datos){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView texto_superior_entrada = (TextView) view.findViewById(R.id.tvTipo);
                    if (texto_superior_entrada != null)
                        texto_superior_entrada.setText(((insertarTipos) entrada).get_textoTitulo());

                    ImageView imagen_entrada = (ImageView) view.findViewById(R.id.imgViewTipo);
                    if (imagen_entrada != null)
                        imagen_entrada.setImageResource(((insertarTipos) entrada).get_idImagen());
                }

            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
                insertarTipos elegido = (insertarTipos) pariente.getItemAtPosition(posicion);

                String texto = elegido.get_textoTitulo().toString();

                if (texto.equals("Pupusas")){
                    Bundle datoEnviar = new Bundle();
                    datoEnviar.putInt("categoria", 1);
                    datoEnviar.putString("nombre", "Sabores de Pupusas");
                    Fragment fragment = new ListaPupusasFragment(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                    fragment.setArguments(datoEnviar);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }
                else if (texto.equals("Bebidas")){
                    Bundle datoEnviar = new Bundle();
                    datoEnviar.putInt("categoria", 2);
                    datoEnviar.putString("nombre", "Bebidas disponibles");
                    Fragment fragment = new ListaPupusasFragment(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                    fragment.setArguments(datoEnviar);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }
                else if (texto.equals("Postres")){
                    Bundle datoEnviar = new Bundle();
                    datoEnviar.putInt("categoria", 3);
                    datoEnviar.putString("nombre", "Postres a disfrutar");
                    Fragment fragment = new ListaPupusasFragment(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                    fragment.setArguments(datoEnviar);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }
                else if (texto.equals("Otros")){
                    Bundle datoEnviar = new Bundle();
                    datoEnviar.putInt("categoria", 4);
                    datoEnviar.putString("nombre", "Y además...");
                    Fragment fragment = new ListaPupusasFragment(); //listaPupusasFragment es el nombre de mi fragmento a abrir
                    fragment.setArguments(datoEnviar);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }
            }
        });

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
}
