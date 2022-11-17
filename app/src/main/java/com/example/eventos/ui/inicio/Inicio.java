package com.example.eventos.ui.inicio;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventos.PantallaPrincipal;
import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

import adaptadorRecyclerView.AdaptadorProximosEventos;

public class Inicio extends Fragment {
    View vista;
    Context context;
    public static LinearLayout linearlayout_todosLosUsuarios;
    private RecyclerView recview;
    private Query referencia;
    private ImageView imagenView_banner;
    private   ArrayList<Modelo_evento> lista;
    ValueEventListener oyente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        vista = inflater.inflate(R.layout.fragment_home, container, false);
        inicializando();
        cargarBanner();

        return vista;
    }

    private void cargarBanner() {

            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Query dataQuery = myRefe.child("Banner").orderByChild("Url");
            dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            try {
                                Picasso.with(context).load(dataSnapshot.child("Url").getValue(String.class)).into(imagenView_banner);

                            }catch (Exception ignored){
                            }

                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }



    private void inicializando() {
        linearlayout_todosLosUsuarios=vista.findViewById(R.id.linearlayout_todosLosUsuarios);
        imagenView_banner=vista.findViewById(R.id.imagenView_banner);
        recview=vista.findViewById(R.id.recyclerView_proximosEventos);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        referencia= FirebaseDatabase.getInstance().getReference().child("Eventos").orderByChild("estado").equalTo("Proximamente");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (PantallaPrincipal.perfilUsaurio!=null ) if ( !PantallaPrincipal.perfilUsaurio.equals("Usuario no registrado"))linearlayout_todosLosUsuarios.setVisibility(View.VISIBLE);
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

                        //Oyente de la seleccion
                        adapador.setOnClickListener(view -> {
                            Modelo_evento evento = lista.get(recview.getChildAdapterPosition(view));
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("evento", evento);
                            Navigation.findNavController(vista).navigate(R.id.detalleEvento,bundle);

                        });


                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        oyente=null;
        vista=null;
    }
}
