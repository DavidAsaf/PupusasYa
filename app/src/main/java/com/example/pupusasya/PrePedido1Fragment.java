package com.example.pupusasya;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class PrePedido1Fragment extends Fragment {

    public PrePedido1Fragment() {
        // Required empty public constructor
    }

    View vista;
    private TextView tvTotal;
    private Button continuar;
    private RadioButton rbDelivery, rbLlevar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_pre_pedido1, container, false);

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
                    Toast.makeText(getContext(), "Deliveryyyyyyyyyyyyy", Toast.LENGTH_SHORT).show();
                }
                else {
                    mostrarDialogoBasico();
                }
            }
        });


        return vista;
    }

    private void mostrarDialogoBasico(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmar enviar pedido");
        builder.setMessage("¿Deseas enviar el pedido para ir a recogerlo?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                        Toast.makeText(getContext(), "Siiiiiiiiiii", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //cerramos dialog
                    }
                }).show();
    }
}
