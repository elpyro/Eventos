package com.example.eventos;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventos.modelos.Modelo_spinner_recuperarId;
import com.example.eventos.modelos.Modelo_usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class Reportes extends Fragment {
    private Context context;
    private View vista;
    private Spinner spinner_estado, spinner_evento, spinner_referido;
    private Button button_crearReporte;
    private Query referenciaEventos;
    private Query referenciaReferidos;
    ArrayList<Modelo_spinner_recuperarId> nombreEvento;
    ArrayList<Modelo_spinner_recuperarId> nombreReferido;
    private String idEventoSeleccionado, id_referidoSeleccionado, estadoSeleccionado;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_reportes, container, false);
        inicializando();
        llenandoSpinner();

        spinner_evento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idEventoSeleccionado= nombreEvento.get(i).getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner_referido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_referidoSeleccionado= nombreReferido.get(i).getId();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                estadoSeleccionado =adapterView.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return vista;
    }

    private void llenandoSpinner() {

        llenandoSpinnerEvento();
        llenandoSpinnerReferido();
        llenandoSpinnerEstado();

    }

    private void llenandoSpinnerEstado() {
        String [] estados= {"Invitado","Asistido","Firmado"};

        ArrayAdapter<String> adapter= new ArrayAdapter<>(context, R.layout.spinner_item, estados);
        spinner_estado.setAdapter(adapter);
    }

    private void llenandoSpinnerReferido() {
        referenciaReferidos= FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("nombre");

        if(referenciaReferidos!=null){
            referenciaReferidos.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                       nombreReferido = new ArrayList<>();

                        for(DataSnapshot ds:snapshot.getChildren()){
                            nombreReferido.add(new Modelo_spinner_recuperarId(Objects.requireNonNull(ds.getValue(Modelo_usuario.class)).getId(),ds.getValue(Modelo_usuario.class).getNombre()));
                        }
                        ArrayAdapter<Modelo_spinner_recuperarId> adaptadorReferido= new ArrayAdapter<>(context, R.layout.spinner_item, nombreReferido);
                        spinner_referido.setAdapter(adaptadorReferido);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void llenandoSpinnerEvento() {

        referenciaEventos= FirebaseDatabase.getInstance().getReference().child("Eventos").orderByChild("fechaHora");

        if(referenciaEventos!=null){
            referenciaEventos.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                         nombreEvento = new ArrayList<>();

                        for(DataSnapshot ds:snapshot.getChildren()){
                            nombreEvento.add(new Modelo_spinner_recuperarId(ds.getValue(Modelo_usuario.class).getId(),ds.getValue(Modelo_usuario.class).getNombre()));
                        }
                        ArrayAdapter<Modelo_spinner_recuperarId> adapterEvento= new ArrayAdapter<>(context, R.layout.spinner_item, nombreEvento);
                        spinner_evento.setAdapter(adapterEvento);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void inicializando() {
        spinner_estado=vista.findViewById(R.id.spinner_estado);
        spinner_evento=vista.findViewById(R.id.spinner_evento);
        spinner_referido=vista.findViewById(R.id.spinner_referido);
        button_crearReporte=vista.findViewById(R.id.button_crearReporte);
    }
}