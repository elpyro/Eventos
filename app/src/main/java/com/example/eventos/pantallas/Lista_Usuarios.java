package com.example.eventos.pantallas;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_usuario;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adaptadorRecyclerView.AdaptadorUsuarios;


public class Lista_Usuarios extends Fragment {

    SearchView searchView_usuarios;
    RecyclerView recview;
    DatabaseReference referencia;
    ArrayList<Modelo_usuario> lista;
    View vista;
    Context context;
    ValueEventListener oyente;
    Button boton_nuevo_usuario;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        vista = inflater.inflate(R.layout.fragment_lista__usuarios, container, false);

        inicializando();
        
        llenandoRecyclerView();


        boton_nuevo_usuario.setOnClickListener(view -> Navigation.findNavController(vista).navigate(R.id.usuarios));

        return vista;
    }


    private void llenandoRecyclerView() {

        if(referencia!=null){
            oyente= referencia.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista.add(ds.getValue(Modelo_usuario.class));
                        }
                        AdaptadorUsuarios adapador= new AdaptadorUsuarios(lista);
                        recview.setAdapter(adapador);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView_usuarios!=null){
            searchView_usuarios.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        ArrayList<Modelo_usuario> filtro =new ArrayList<>();
        for(Modelo_usuario objeto:lista){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        AdaptadorUsuarios adaptador = new AdaptadorUsuarios(filtro);
        recview.setAdapter(adaptador);
    }

    private void inicializando() {

        boton_nuevo_usuario=vista.findViewById(R.id.button_nuevoUsuario);
        recview= vista.findViewById(R.id.recyclerView_usuarios);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        searchView_usuarios=vista.findViewById(R.id.searchView_filtro_usuarios);
        referencia= FirebaseDatabase.getInstance().getReference().child("Usuarios");
    }



    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }

}