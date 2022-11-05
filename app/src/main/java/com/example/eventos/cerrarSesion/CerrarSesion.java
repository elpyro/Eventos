package com.example.eventos.cerrarSesion;

import android.content.Context;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventos.PantallaPrincipal;
import com.example.eventos.R;
import com.firebase.ui.auth.AuthUI;



public class CerrarSesion extends Fragment {

    Context context;
    View vista;
    public TextView textView_usuario, textView_perfil;
    Button button_cerrarSesion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        vista = inflater.inflate(R.layout.fragment_cerrar_sesion, container, false);
       
        inicializando();

        pintadoFragmento();
        
        button_cerrarSesion.setOnClickListener(view -> cerrarSesion());
        
        return vista;
    }

    private void pintadoFragmento() {
        textView_usuario.setText(PantallaPrincipal.nombreUsuario);
        textView_perfil.setText(PantallaPrincipal.perfilUsaurio);
    }

    private void cerrarSesion() {
        AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener(task -> {
            Toast.makeText(getContext(), "Sesion Cerrada", Toast.LENGTH_LONG).show();
            requireActivity().onBackPressed();
        });
    }

    private void inicializando() {
        textView_usuario=vista.findViewById(R.id.textView_Usuario);
        textView_perfil=vista.findViewById(R.id.textView_perfil);
        button_cerrarSesion=vista.findViewById(R.id.button_cerrarSesion);
    }
}