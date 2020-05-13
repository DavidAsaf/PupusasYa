package com.example.pupusasya.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pupusasya.Model.pupuserias;
import com.example.pupusasya.R;

import java.util.List;

public class Pupuserias_Adapter extends ArrayAdapter<pupuserias> {

    private List<pupuserias> pupuseriasList;
    private Context context;

    public Pupuserias_Adapter (List<pupuserias> P, Context c)
    {
        super(c, R.layout.activity_lista_pupuserias);
        this.pupuseriasList = P;
        this.context = c;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_lista_pupuserias, null, true);

        //TextView name

        return super.getView(position, convertView, parent);
    }
}
