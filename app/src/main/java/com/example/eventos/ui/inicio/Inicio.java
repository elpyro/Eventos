package com.example.eventos.ui.inicio;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

import adaptadorRecyclerView.AdaptadorEventos;
import adaptadorRecyclerView.AdaptadorProximosEventos;

public class Inicio extends Fragment {
    View vista;
    Context context;

    private  SearchView searchView_eventos;
    private RecyclerView recview;
    private Query referencia;
    private   ArrayList<Modelo_evento> lista;
    private   ValueEventListener oyente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        vista = inflater.inflate(R.layout.fragment_home, container, false);
        inicializando();
//
//        recibirArgumentos();
//
//        boton_guardar.setOnClickListener((View v) -> guardar());

        return vista;
    }

    private void inicializando() {
        recview=vista.findViewById(R.id.recyclerView_proximosEventos);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        referencia= FirebaseDatabase.getInstance().getReference().child("Eventos").orderByChild("estado").equalTo("Proximamente");
    }

    @Override
    public void onResume() {
        super.onResume();
        llenandoRecyclerView();
    }


    private void llenandoRecyclerView() {

        if(referencia!=null){
            oyente= referencia.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista.add(ds.getValue(Modelo_evento.class));
                        }
                        lista.sort(Comparator.comparing(Modelo_evento::getFechaHora));
                        AdaptadorProximosEventos adapador= new AdaptadorProximosEventos(lista);
                        recview.setAdapter(adapador);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
