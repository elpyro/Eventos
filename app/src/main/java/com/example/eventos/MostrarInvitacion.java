package com.example.eventos;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventos.modelos.Modelo_evento;
import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MostrarInvitacion extends Fragment {
    private Context context;
    private View vista;
    private TextView textView_estado,textView_correo_invitado,textView_telefono_invitado,textView_nombre_invitado,
            textView_direccion,textView_ciudad,textView_costo,textView_fecha,textView_descripcion,textView_nombre_evento;
    private Button button_compartir;
    private ImageView imageView_qr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_mostrar_invitacion, container, false);
        inicializar();
        recibirArgumentos();
        return vista;
    }

    private void inicializar() {
        textView_estado=vista.findViewById(R.id.textView_estado);
        textView_correo_invitado=vista.findViewById(R.id.textView_correo_invitado);
        textView_telefono_invitado=vista.findViewById(R.id.textView_telefono_invitado);
        textView_nombre_invitado=vista.findViewById(R.id.textView_nombre_invitado);
        textView_direccion=vista.findViewById(R.id.textView_direccion);
        textView_costo=vista.findViewById(R.id.textView_costo);
        textView_ciudad=vista.findViewById(R.id.textView_ciudad);
        textView_fecha=vista.findViewById(R.id.textView_fecha);
        textView_descripcion=vista.findViewById(R.id.textView_descripcion);
        button_compartir=vista.findViewById(R.id.button_compartir);
        imageView_qr=vista.findViewById(R.id.imageView_qr);
        textView_nombre_evento=vista.findViewById(R.id.textView_nombre_evento);

    }

    private void recibirArgumentos() {
        if (getArguments() != null) {

            String id_invitacion = getArguments().getString("id_invitacion");


            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Query dataQuery = myRefe.child("Invitaciones").orderByChild("id").equalTo(id_invitacion).limitToFirst(1);
            dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            Modelo_invitacion invitacion = userSnapshot.getValue(Modelo_invitacion.class);
                            assert invitacion != null;

                            textView_descripcion.setText(invitacion.getNombre()+" "+getString(R.string.tienesInvitacion));
                            textView_nombre_evento.setText(invitacion.getNombre_evento());
                            textView_fecha.setText(invitacion.getFecha());
                            textView_costo.setText(invitacion.getCosto());
                            textView_ciudad.setText(invitacion.getCiudad());
                            textView_direccion.setText(invitacion.getDireccion());
                            textView_nombre_invitado.setText(invitacion.getNombre());
                            textView_telefono_invitado.setText(invitacion.getTelefono());
                            textView_correo_invitado.setText(invitacion.getCorreo());
                            textView_estado.setText(invitacion.getEstado());




                        }
                    } else {
                        Toast.makeText(context,getString(R.string.InvitacionNoEncontrada) , Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, getString(R.string.problemasConexion), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}