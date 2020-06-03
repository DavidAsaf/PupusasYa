package com.example.pupusasya.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.pupusasya.Model.Historial;
import com.example.pupusasya.Model.pupuserias;
import com.example.pupusasya.R;

import java.util.List;

public class HistorialAdapter extends ArrayAdapter<Historial> {
    private List<Historial> historialList;
    private Context context;

    public HistorialAdapter (List<Historial> H, Context c)
    {
        super(c, R.layout.fragmento_historial);
        this.historialList = H;
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragmento_historial, null, true);

        //TextView name

        return super.getView(position, convertView, parent);
    }
}
