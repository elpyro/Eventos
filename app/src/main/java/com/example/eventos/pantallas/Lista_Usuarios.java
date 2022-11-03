package com.example.eventos.pantallas;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.eventos.R;


public class Lista_Usuarios extends Fragment {

    View vista;
    Context context;
    Button boton_nuevo_usuario;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        vista = inflater.inflate(R.layout.fragment_lista__usuarios, container, false);


        return vista;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }

}