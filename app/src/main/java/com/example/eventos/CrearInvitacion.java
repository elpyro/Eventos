package com.example.eventos;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventos.modelos.Modelo_evento;
import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.UUID;

public class CrearInvitacion extends Fragment {
    private Context context;
    private View vista;
    private Button button_invitar,button_habeasData;
    private EditText editText_telefono, editText_correo, editText_nombre;
    private Modelo_evento evento;
    private CheckBox checkBox_habeasData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_crear_invitacion, container, false);

        inicializando();

        Bundle bundle = getArguments();
        if (getArguments() != null) {
            evento =(Modelo_evento) bundle.getSerializable("evento");
        }

        button_invitar.setOnClickListener(view -> guardarInvitacion());
        button_habeasData.setOnClickListener(view -> abrirHabeasData());
        return  vista;
    }

    private void abrirHabeasData() {
        Navigation.findNavController(vista).navigate(R.id.habeasData);
    }


    private void guardarInvitacion() {

        if (!validarDatos()) return;

            button_invitar.setEnabled(false);

            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Modelo_invitacion invitacion = new Modelo_invitacion();
            String id_invitacion=(UUID.randomUUID().toString());
            invitacion.setId(id_invitacion);
            invitacion.setNombre(editText_nombre.getText().toString().trim());
            invitacion.setTelefono(editText_telefono.getText().toString().trim());
            invitacion.setCorreo(editText_correo.getText().toString().trim());
            invitacion.setEstado("Invitado");
            invitacion.setId_usuario_referido(PantallaPrincipal.idUsuario);
            invitacion.setNombre_referido(PantallaPrincipal.nombreUsuario);
            invitacion.setId_evento(evento.getId());
            invitacion.setNombre_evento(evento.getNombre());
            invitacion.setFecha(evento.getFecha());
            invitacion.setCiudad(evento.getCiudad());
            invitacion.setCosto(evento.getCosto());
            invitacion.setDireccion(evento.getDireccion());
            myRefe.child("Invitaciones").child(invitacion.getId()).setValue(invitacion);

            editText_nombre.setText("");
            editText_telefono.setText("");
            editText_correo.setText("");
            
            Bundle bundle = new Bundle();
            bundle.putString("id_invitacion",id_invitacion);
            Navigation.findNavController(vista).navigate(R.id.mostrarInvitacion,bundle);




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
        if (!checkBox_habeasData.isChecked()){
            Toast.makeText(context, "Debe aceptar los Habeas Data", Toast.LENGTH_SHORT).show();
            datosAceptados=false;
        }
        return datosAceptados;
    }

    private void inicializando() {
        editText_telefono=vista.findViewById(R.id.editText_telefono);
        editText_correo=vista.findViewById(R.id.editText_correo);
        editText_nombre=vista.findViewById(R.id.editText_nombre);
        button_habeasData=vista.findViewById(R.id.button_habeasData);
        button_invitar=vista.findViewById(R.id.button_invitar);
        checkBox_habeasData = vista.findViewById(R.id.checkBox_habeasData);
    }
}