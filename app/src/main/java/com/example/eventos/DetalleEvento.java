package com.example.eventos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
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

import java.util.ArrayList;
import java.util.Comparator;

import adaptadorRecyclerView.AdaptadorAsistencia;


public class DetalleEvento extends Fragment {

    private ImageView imageView_imagen_evento;
    private TextView textView_nombre_evento,textView_descripcion,textView_fecha, textView_boletos,textView_disponibles,textView_costo,textView_ciudad,textView_direccion,textView_cuenta;
    private Context context;
    private View vista;
    String id_evento;
    private Button bottom_asistencia;
    private Button button_crearInvitacion;
    private Button button_firma;
    private Modelo_evento evento;

    private SearchView search_invitados;
    private RecyclerView recview;
    private Query referencia;
    private ArrayList<Modelo_invitacion> listaInvitados;
    private   ValueEventListener oyente;
    AdaptadorAsistencia adaptador=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_detalle_evento, container, false);

        incicializando();
        recibirArgumentos();

        button_crearInvitacion.setOnClickListener(view -> abrirCrearInvitacion());
        bottom_asistencia.setOnClickListener(view -> abrirConfirmarAsistencia());
        button_firma.setOnClickListener(view -> abrirConfirmarFirma());

        return vista;
    }

    private void abrirConfirmarFirma() {
        Bundle bundle = new Bundle();
        bundle.putString("evento",evento.getId());
        Navigation.findNavController(vista).navigate(R.id.confirmarFirma,bundle);
    }

    private void recibirArgumentos() {
        Bundle bundle = getArguments();
        if (getArguments() != null) {
            evento =(Modelo_evento) bundle.getSerializable("evento");
            cargarEvento(evento.getId());
            cargarAsistencia(evento.getId());
        }
    }

    private void abrirConfirmarAsistencia() {
        Bundle bundle = new Bundle();
        bundle.putString("evento",evento.getId());
        Navigation.findNavController(vista).navigate(R.id.confirmarAsistencia,bundle);
    }

    private void cargarAsistencia(String id_evento) {
        referencia= FirebaseDatabase.getInstance().getReference().child("Invitaciones").orderByChild("id_evento").equalTo(id_evento);

        if(referencia!=null){
            oyente= referencia.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        int asistencia=0;
                        int asistenciaTotal=0;
                        listaInvitados=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            if (ds.getValue(Modelo_invitacion.class).getId_usuario_referido().equals(PantallaPrincipal.idUsuario)){
                                listaInvitados.add(ds.getValue(Modelo_invitacion.class));
                                if (ds.getValue(Modelo_invitacion.class).getEstado().equals("Asistido"))asistencia=asistencia+1;
                                asistenciaTotal=asistenciaTotal+1;

                            }

                        }
                        textView_cuenta.setText(asistencia+" / "+ asistenciaTotal);
                        listaInvitados.sort(Comparator.comparing(Modelo_invitacion::getEstado));
                        adaptador= new AdaptadorAsistencia(listaInvitados);
                        recview.setAdapter(adaptador);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (search_invitados!=null){
            search_invitados.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filtro(newText);
                    return true;
                }
            });
        }
    }

    private void filtro(String valor) {
        ArrayList<Modelo_invitacion> filtro =new ArrayList<>();
        for(Modelo_invitacion objeto:listaInvitados){
            if(objeto.getNombre().toLowerCase().contains(valor.toLowerCase())){
                filtro.add(objeto);
            }
        }
        adaptador = new AdaptadorAsistencia(filtro);
        recview.setAdapter(adaptador);


    }

    private void abrirCrearInvitacion() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("evento",evento);
        Navigation.findNavController(vista).navigate(R.id.crearInvitacion,bundle);
    }

    private void cargarEvento(String id) {
        id_evento = id;
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Query dataQuery = myRefe.child("Eventos").orderByChild("id").equalTo(id_evento).limitToFirst(1);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        Modelo_evento evento = userSnapshot.getValue(Modelo_evento.class);
                        assert evento != null;

                        textView_nombre_evento.setText(evento.getNombre());
                        textView_descripcion.setText(evento.getDescripcion());
                        textView_ciudad.setText(evento.getCiudad());
                        textView_direccion.setText(evento.getDireccion());
                        String fecha=evento.getFecha();
                        textView_fecha.setText(fecha);
                        textView_costo.setText(evento.getCosto());
                        textView_boletos.setText(evento.getBoletos());
                        int boletosDisponibles= Integer.parseInt(evento.getBoletos())-Integer.parseInt(evento.getBoletosVendidos());
                        textView_disponibles.setText(boletosDisponibles+"");

                        try {
                            Picasso.with(context).load(evento.getUrlImagen()).into(imageView_imagen_evento);
                        }catch (Exception ignored){
                        }


                    }
                } else {
                    Toast.makeText(context,getString(R.string.EventoInexistente) , Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, getString(R.string.problemasConexion), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incicializando() {

        imageView_imagen_evento=vista.findViewById(R.id.imageView_imagen_evento);
        textView_nombre_evento=vista.findViewById(R.id.textView_nombre_evento);
        textView_descripcion=vista.findViewById(R.id.textView_descripcion);
        textView_fecha=vista.findViewById(R.id.textView_fecha);
        textView_costo=vista.findViewById(R.id.textView_costo);
        textView_ciudad=vista.findViewById(R.id.textView_ciudad);
        textView_direccion=vista.findViewById(R.id.textView_direccion);
        textView_boletos=vista.findViewById(R.id.textView_boletos);
        textView_disponibles=vista.findViewById(R.id.textView_disponibles);
        button_firma = vista.findViewById(R.id.button_firmar);
        button_crearInvitacion=vista.findViewById(R.id.button_crearInvitacion);
        bottom_asistencia=vista.findViewById(R.id.bottom_asistencia);
        textView_cuenta=vista.findViewById(R.id.textView_cuenta);

        recview= vista.findViewById(R.id.recyclerView_asistencia);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        search_invitados =vista.findViewById(R.id.search_invitados);

    }
}