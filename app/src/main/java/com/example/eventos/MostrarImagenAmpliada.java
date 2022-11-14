package com.example.eventos;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class MostrarImagenAmpliada extends Fragment {

    private Context context;
    private View vista;
    private ImageView imagen_ampliada;
    private String url_imagen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_mostrar_imagen_ampliada, container, false);
        inicializando();
        recibirArgumentos();
        mostraImagen();
        return vista;
    }

    private void mostraImagen() {
        Picasso.with(context).load(url_imagen).into(imagen_ampliada);
    }

    private void recibirArgumentos() {
        Bundle bundle = getArguments();
        if (getArguments() != null) {
            url_imagen=bundle.getString("url_imagen");
        }
    }
    private void inicializando() {
        imagen_ampliada=vista.findViewById(R.id.imagen_ampliada);
    }
}