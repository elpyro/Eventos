package com.example.eventos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventos.modelos.Modelo_evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class DetalleEvento extends Fragment {

    private ImageView imageView_imagen_evento;
    private TextView textView_nombre_evento,textView_descripcion,textView_fecha, textView_boletos,textView_disponibles,textView_costo,textView_ciudad,textView_direccion;
    private Context context;
    private View vista;
    String id_evento;
    private Button button_firmar, bottom_asistencia,button_crearInvitacion;
    private Modelo_evento evento;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_detalle_evento, container, false);

        incicializando();

        Bundle bundle = getArguments();
        if (getArguments() != null) {

            evento =(Modelo_evento) bundle.getSerializable("evento");
            cargarEvento(evento.getId());
        }
        button_crearInvitacion.setOnClickListener(view -> abrirCrearInvitacion());
        return vista;
    }

    private void abrirCrearInvitacion() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("evento",evento);
        Navigation.findNavController(vista).navigate(R.id.crearInvitacion,bundle);
    }

    private void cargarEvento(String id) {
        id_evento = id;
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Query dataQuery = myRefe.child("Eventos").orderByChild("id").equalTo(id_evento).limitToFirst(1);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_evento evento = userSnapshot.getValue(Modelo_evento.class);
                        assert evento != null;

                        textView_nombre_evento.setText(evento.getNombre());
                        textView_descripcion.setText(evento.getDescripcion());
                        textView_ciudad.setText(evento.getCiudad());
                        textView_direccion.setText(evento.getDireccion());
                        String fecha=evento.getFecha();
                        textView_fecha.setText(fecha);
                        textView_costo.setText(evento.getCosto());
                        textView_boletos.setText(evento.getBoletos());
                        int boletosDisponibles= Integer.parseInt(evento.getBoletos())-Integer.parseInt(evento.getBoletosVendidos());
                        textView_disponibles.setText(boletosDisponibles+"");



                        try {
                            Picasso.with(context).load(evento.getUrlImagen()).into(imageView_imagen_evento);
                        }catch (Exception ignored){
                        }
                    }
                } else {
                    Toast.makeText(context,getString(R.string.EventoInexistente) , Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, getString(R.string.problemasConexion), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incicializando() {

        imageView_imagen_evento=vista.findViewById(R.id.imageView_imagen_evento);
        textView_nombre_evento=vista.findViewById(R.id.textView_nombre_evento);
        textView_descripcion=vista.findViewById(R.id.textView_descripcion);
        textView_fecha=vista.findViewById(R.id.textView_fecha);
        textView_costo=vista.findViewById(R.id.textView_costo);
        textView_ciudad=vista.findViewById(R.id.textView_ciudad);
        textView_direccion=vista.findViewById(R.id.textView_direccion);
        textView_boletos=vista.findViewById(R.id.textView_boletos);
        textView_disponibles=vista.findViewById(R.id.textView_disponibles);
        button_firmar=vista.findViewById(R.id.button_firmar);
        button_crearInvitacion=vista.findViewById(R.id.button_crearInvitacion);
        bottom_asistencia=vista.findViewById(R.id.bottom_asistencia);

    }
}