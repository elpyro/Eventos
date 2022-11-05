package com.example.eventos.usuario;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

import adaptadorRecyclerView.AdaptadorUsuarios;


public class Lista_Usuarios extends Fragment {

    private SearchView searchView_usuarios;
    private RecyclerView recview;
    private DatabaseReference referencia;
    private ArrayList<Modelo_usuario> lista;
    private View vista;
    private Context context;
    private ValueEventListener oyente;
    private Button boton_nuevo_usuario;


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
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista.add(ds.getValue(Modelo_usuario.class));
                        }
                        lista.sort(Comparator.comparing(Modelo_usuario::getNombre));
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
        referencia.removeEventListener(oyente);
        vista=null;
    }

}