package com.example.eventos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditarInvitado extends Fragment {

    private Context context;
    private View vista;
    private Button button_editar;
    private EditText editText_telefono, editText_correo, editText_nombre;
    Modelo_invitacion invitacion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context= getContext();
        vista =inflater.inflate(R.layout.fragment_editar_invitado, container, false);
        inicializando();
        cargarInvitado();
        button_editar.setOnClickListener(view -> guardarCambios());
        return vista;
    }

    private void guardarCambios() {

        if (!validarDatos()) return;

        invitacion.setNombre(editText_nombre.getText().toString().trim());
        invitacion.setTelefono(editText_telefono.getText().toString().trim());
        invitacion.setCorreo(editText_correo.getText().toString().trim());

        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        myRefe.child("Invitaciones").child(invitacion.getId()).setValue(invitacion);

        MostrarInvitacion.textView_nombre_invitado.setText(editText_nombre.getText().toString().trim());
        MostrarInvitacion.textView_correo_invitado.setText(editText_correo.getText().toString().trim());
        MostrarInvitacion.textView_telefono_invitado.setText(editText_telefono.getText().toString().trim());
        MostrarInvitacion.textView_nombreEnInvitacion.setText(editText_nombre.getText().toString().trim());
        Toast.makeText(context, "Cambios aceptados", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(vista).navigateUp();
    }

    private boolean validarDatos() {

        boolean datosAceptados=true;
        if (editText_nombre.getText().toString().length()<3){
            editText_nombre.setError("Requerido");
            datosAceptados=false;
        }
        if (editText_telefono.getText().toString().length()<3){
            editText_telefono.setError("Requerido");
            datosAceptados=false;
        }
        return datosAceptados;
    }

    private void cargarInvitado() {
        Bundle bundle = getArguments();
        if (getArguments() != null) {
            invitacion =(Modelo_invitacion) bundle.getSerializable("invitacion");
            editText_nombre.setText(invitacion.getNombre());
            editText_correo.setText(invitacion.getCorreo());
            editText_telefono.setText(invitacion.getTelefono());
        }else{
            Toast.makeText(context, "Invitacion no encontrada", Toast.LENGTH_SHORT).show();
        }

    }

    private void inicializando() {
        editText_telefono=vista.findViewById(R.id.editText_telefono);
        editText_correo=vista.findViewById(R.id.editText_correo);
        editText_nombre=vista.findViewById(R.id.editText_nombre);
        button_editar =vista.findViewById(R.id.button_editar);
    }
}