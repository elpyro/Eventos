package com.example.eventos;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ListaEventos extends Fragment {

    View vista;
    Context context;
    Button button_nuevo_evento;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista= inflater.inflate(R.layout.fragment_lista_eventos, container, false);
        button_nuevo_evento=vista.findViewById(R.id.button_nuevoEvento);

        button_nuevo_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(vista).navigate(R.id.evento);
            }
        });
        return vista;
    }
}