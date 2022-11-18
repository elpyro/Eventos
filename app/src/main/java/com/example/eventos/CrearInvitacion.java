package com.example.eventos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eventos.modelos.Modelo_evento;
import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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


    @SuppressLint("SuspiciousIndentation")
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
            invitacion.setApline_referido(PantallaPrincipal.apline_usuario);
            invitacion.setTelefono_referido(PantallaPrincipal.usuario_telefono);
            invitacion.setUsuario_validus_referido(PantallaPrincipal.usuario_validus_referido);
            invitacion.setId_evento(evento.getId());
            invitacion.setNombre_evento(evento.getNombre());
            invitacion.setFecha(evento.getFecha());
            invitacion.setCiudad(evento.getCiudad());
            invitacion.setCosto(evento.getCosto());
            invitacion.setDireccion(evento.getDireccion());


            myRefe.child("Invitaciones").child(invitacion.getId()).setValue(invitacion);

            limpiar();

            Bundle bundle = new Bundle();
            bundle.putString("id_invitacion",id_invitacion);
            Navigation.findNavController(vista).navigate(R.id.mostrarInvitacion,bundle);

            if(isNetworkConnected(context))restarBoletoEvento(evento.getId());



    }

    private void limpiar() {
        editText_nombre.setText("");
        editText_telefono.setText("");
        editText_correo.setText("");
        //ocultar teclado
        InputMethodManager input = (InputMethodManager) (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(vista.getWindowToken(), 0);
    }

    private boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.isAvailable();
    }

    private void restarBoletoEvento(String id_evento) {
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Query dataQuery = myRefe.child("Eventos").orderByChild("id").equalTo(id_evento).limitToFirst(1);
        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Modelo_evento evento = userSnapshot.getValue(Modelo_evento.class);
                        assert evento != null;

                        int boletos_vendidos = Integer.parseInt(evento.getBoletosVendidos());
                        boletos_vendidos = boletos_vendidos + 1;
                        evento.setBoletosVendidos(boletos_vendidos + "");

                        DatabaseReference GuardarReferencia = FirebaseDatabase.getInstance().getReference();
                        GuardarReferencia.child("Eventos").child(evento.getId()).setValue(evento);
                    }
                } else {
                    Toast.makeText(context, getString(R.string.EventoInexistente), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, getString(R.string.problemasConexion), Toast.LENGTH_SHORT).show();
            }
        });

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