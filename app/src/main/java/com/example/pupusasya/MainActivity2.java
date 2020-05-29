package com.example.pupusasya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public abstract class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvMensajes;
    private EditText etName;
    private EditText etMensaje;
    private ImageButton btnSend;

    private List<MensajeVO> lstMensajes;
    private AdapterRVMensajes mAdapterRVMensajes;

    private void setComponents(){
        rvMensajes = findViewById(R.id.rvMensajes);
        etName = findViewById(R.id.etName);
        etMensaje = findViewById(R.id.etMensaje);
        btnSend = findViewById(R.id.btnSend);

        lstMensajes = new ArrayList<>();
        mAdapterRVMensajes = new AdapterRVMensajes(lstMensajes);
        rvMensajes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvMensajes.setAdapter(mAdapterRVMensajes);
        rvMensajes.setHasFixedSize(true);

        FirebaseFirestore.getInstance().collection("Chat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange mDocumentChange : queryDocumentSnapshots.getDocumentChanges()){
                            if(mDocumentChange.getType() == DocumentChange.Type.ADDED){
                                lstMensajes.add(mDocumentChange.getDocument().toObject(MensajeVO.class));
                                mAdapterRVMensajes.notifyDataSetChanged();
                                rvMensajes.smoothScrollToPosition(lstMensajes.size());
                            }
                        }
                    }
                });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.length() == 0 || etMensaje.length() == 0)
                    return;
                MensajeVO mMensajeVO = new MensajeVO();
                mMensajeVO.setMessage(etMensaje.getText().toString());
                mMensajeVO.setName(etName.getText().toString());
                FirebaseFirestore.getInstance().collection("Chat").add(mMensajeVO);
                etMensaje.setText("");
                //etName.setText("");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmento_chat);
        setComponents();
    }


}
