package com.example.eventos;

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

import com.example.eventos.modelos.Modelo_evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import adaptadorRecyclerView.AdaptadorEventosTerminados;


public class EventosTerminados extends Fragment {
    private View vista;
    private Context context;
    private SearchView searchView_eventos;
    private RecyclerView recview;
    private Query referencia;
    private ArrayList<Modelo_evento> lista;
    private ValueEventListener oyente;
    AdaptadorEventosTerminados adapador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_eventos_terminados, container, false);

        inicializando();
        return vista;
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
                        Collections.reverse(lista);
                        adapador= new AdaptadorEventosTerminados(lista);
                        recview.setAdapter(adapador);


                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView_eventos !=null){
            searchView_eventos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filtro(newText);
                    return true;
                }
            });
        }
    }


    private void filtro(String valor) {
        ArrayList<Modelo_evento> filtro =new ArrayList<>();
        for(Modelo_evento objeto:lista){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }

        AdaptadorEventosTerminados adaptador = new AdaptadorEventosTerminados(filtro);
        recview.setAdapter(adaptador);


        //Oyente de la seleccion

    }

    private void inicializando() {

        recview= vista.findViewById(R.id.recyclerView_eventos);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        searchView_eventos =vista.findViewById(R.id.searchView_filtro_eventos);
        referencia= FirebaseDatabase.getInstance().getReference().child("Eventos").orderByChild("estado").equalTo("Terminado");
    }

    @Override
    public void onResume() {
        super.onResume();
        llenandoRecyclerView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        referencia.removeEventListener(oyente);
        vista=null;
    }
}