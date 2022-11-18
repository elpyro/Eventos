package com.example.eventos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import adaptadorRecyclerView.AdaptadorConfirmarAsistencia;

public class ConfirmarAsistencia extends Fragment {

    public static Modelo_invitacion codigoEncontrado;
    private Context context;
    private View vista;
    private Button button_escanear;

    private SearchView search_invitados;
    private TextView textView_cuenta;
    private String id_evento;
    private Query referencia;
    private RecyclerView recview;
    private   ValueEventListener oyente;
    public static ArrayList<Modelo_invitacion> listaInvitados;
    AdaptadorConfirmarAsistencia adaptador=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_confirmar_asistencia, container, false);
        inicializando();
        recibirArgumentos();
        button_escanear.setOnClickListener(view -> abrirScanner());
        return vista;
    }

    private void abrirScanner() {

        Intent  intent = new Intent(getContext(),LectorDeCodigosQr.class);
        intent.putExtra("desdeFragmento" , "ConfirmarAsistencia" );
        startActivity(intent);

    }


    private void recibirArgumentos() {
        Bundle bundle = getArguments();
        if (getArguments() != null) {
            id_evento= bundle.getString("evento");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarAsistencia(id_evento);
    }

    private void inicializando() {

        search_invitados=vista.findViewById(R.id.search_invitados);
        textView_cuenta=vista.findViewById(R.id.textView_cuenta);
        button_escanear=vista.findViewById(R.id.button_escanear);

        recview=vista.findViewById(R.id.recyclerView_asistencia);
        recview= vista.findViewById(R.id.recyclerView_asistencia);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));

    }

    private void cargarAsistencia(String id_evento) {

        referencia= FirebaseDatabase.getInstance().getReference().child("Invitaciones").orderByChild("id_evento").equalTo(id_evento);

        oyente= referencia.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int asistencia=0;
                    int asistenciaTotal=0;
                    listaInvitados=new ArrayList<>();
                    for(DataSnapshot ds:snapshot.getChildren()){
                        listaInvitados.add(ds.getValue(Modelo_invitacion.class));
                        if (Objects.requireNonNull(ds.getValue(Modelo_invitacion.class)).getEstado().equals("Asistido")|| Objects.requireNonNull(ds.getValue(Modelo_invitacion.class)).getEstado().equals("Firmado")) asistencia=asistencia+1;
                        asistenciaTotal=asistenciaTotal+1;

                    }
                    textView_cuenta.setText(asistencia+" / "+ asistenciaTotal);
                    listaInvitados.sort(Comparator.comparing(Modelo_invitacion::getEstado));
                    Collections.reverse(listaInvitados);
                    adaptador= new AdaptadorConfirmarAsistencia(listaInvitados);
                    recview.setAdapter(adaptador);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
            }
        });
        if (search_invitados!=null){
            search_invitados.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        ArrayList<Modelo_invitacion> filtro =new ArrayList<>();
        for(Modelo_invitacion objeto:listaInvitados){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        adaptador = new AdaptadorConfirmarAsistencia(filtro);
        recview.setAdapter(adaptador);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            referencia.removeEventListener(oyente);
        }catch (Exception ignored){}
        vista=null;
    }
}