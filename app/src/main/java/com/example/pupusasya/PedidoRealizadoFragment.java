package com.example.pupusasya;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class PedidoRealizadoFragment extends Fragment {

    public PedidoRealizadoFragment() {
        // Required empty public constructor
    }

    View vista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_pedido_realizado, container, false);

        return vista;
    }
}
