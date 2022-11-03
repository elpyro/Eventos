package com.example.eventos.pantallas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.eventos.R;


public class Usuarios extends Fragment {

    View vista;
    Context context;
    Spinner spinner_tipo_usuario;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        vista = inflater.inflate(R.layout.fragment_usuarios, container, false);
        spinner_tipo_usuario=vista.findViewById(R.id.spinnerTipo);

        String [] opciones= {"Promotor","Auditor","Administrador"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String >(context,R.layout.spinner_item, opciones);
        spinner_tipo_usuario.setAdapter(adapter);
    return vista;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }

}