package com.example.eventos.pantallas;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;
import java.util.regex.Pattern;


public class Usuarios extends Fragment {
    String id_exitente="", id_usuario;
    View vista;
    Context context;
    Spinner spinner_tipo_usuario;
    Button boton_guardar;
    EditText editText_nombre, editText_telefono,editText_correo,editText_direccion, editText_apline, editText_usuarioValidus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        vista = inflater.inflate(R.layout.fragment_usuarios, container, false);
        inicializando();

        recibirArgumentos();

        String [] opciones= {"Promotor","Auditor","Administrador"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String >(context,R.layout.spinner_item, opciones);
        spinner_tipo_usuario.setAdapter(adapter);


        boton_guardar.setOnClickListener((View v) -> {
            guardarUsuario();
        });

    return vista;
    }

    private void recibirArgumentos() {
        //recibir argumentos
        if (getArguments() != null) {
            id_usuario =getArguments().getString("id_usuario");
            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Query dataQuery = myRefe.child("Usuarios").orderByChild("id").equalTo(id_usuario).limitToFirst(1);
            dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            Modelo_usuario usuario = userSnapshot.getValue(Modelo_usuario.class);
                            id_exitente=usuario.getId();
                            editText_nombre.setText(usuario.getNombre());
                            editText_telefono.setText(usuario.getTelefono());
                            editText_correo.setText(usuario.getCorreoGmail());
                            editText_direccion.setText(usuario.getDireccion());
                            editText_apline.setText(usuario.getApline());
                            editText_usuarioValidus.setText(usuario.getValidusUsuario());
                            if (usuario.getPerfil().equals("Administrador")){
                                spinner_tipo_usuario.setSelection(2);
                            } else if (usuario.getPerfil().equals("Auditor")){
                                spinner_tipo_usuario.setSelection(1);
                            } else if (usuario.getPerfil().equals("Promotor")){
                                spinner_tipo_usuario.setSelection(0);
                            }

                        }
                    } else {

                        Toast.makeText(context,getString(R.string.UsuarioInexistente) , Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, getString(R.string.problemasConexion), Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    private void inicializando() {
        spinner_tipo_usuario=vista.findViewById(R.id.spinnerTipo);
        boton_guardar=vista.findViewById(R.id.button_guardar);
        editText_nombre=vista.findViewById(R.id.editText_nombre);
        editText_telefono=vista.findViewById(R.id.editText_telefono);
        editText_correo=vista.findViewById(R.id.editText_correoGmail);
        editText_direccion=vista.findViewById(R.id.editText_direccion);
        editText_apline=vista.findViewById(R.id.editText_apline);
        editText_usuarioValidus=vista.findViewById(R.id.editText_usuarioValidus);
    }

    private void guardarUsuario() {

        if (validacionDeCampos()==false) return;

        new Thread(new Runnable() {

            @Override
            public void run() {
                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                Modelo_usuario usuario = new Modelo_usuario();

                //si se recibieron argumentos el id es el mismo. EDITAR
                if (!id_exitente.equals("")){
                    usuario.setId(id_exitente);
                }else{
                    usuario.setId(UUID.randomUUID().toString());
                }

                usuario.setPerfil(spinner_tipo_usuario.getSelectedItem().toString().trim());
                usuario.setNombre(editText_nombre.getText().toString().trim());
                usuario.setTelefono(editText_telefono.getText().toString().trim());
                usuario.setCorreoGmail(editText_correo.getText().toString().trim());
                usuario.setDireccion(editText_direccion.getText().toString().trim());
                usuario.setApline(editText_apline.getText().toString().trim());
                usuario.setValidusUsuario(editText_usuarioValidus.getText().toString().trim());
                myRefe.child("Usuarios").child(usuario.getId()).setValue(usuario);
            }
        }).start();
        Toast.makeText(context, "Guardado", Toast.LENGTH_LONG).show();
        getActivity().onBackPressed();
    }


    private boolean validacionDeCampos() {
        boolean datosAceptados=true;
        if (editText_nombre.getText().toString().length()<3){
            editText_nombre.setError("Requerido");
            datosAceptados=false;
        }
        if (editText_telefono.getText().toString().length()<3){
            editText_telefono.setError("Requerido");
            datosAceptados=false;
        }
        if (!validarEmail(editText_correo.getText().toString().trim())){
            editText_correo.setError("Email no válido");
            datosAceptados=false;
        }

        return datosAceptados;
    }

//    private boolean correoRepetido() {
//        final boolean[] coincidencia = {false};
//
//            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
//            Query dataQuery = myRefe.child("Usuarios").orderByChild("correoGmail").equalTo(editText_correo.getText().toString().trim().toLowerCase()).limitToFirst(1);
//            dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        coincidencia[0] =true;
//                        return;
//                    }else {
//                        return;
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(context, getString(R.string.problemasConexion), Toast.LENGTH_SHORT).show();
//                }
//            });
//
//
//    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        vista=null;
    }

}