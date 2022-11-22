package com.example.eventos.ui.inicio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventos.PantallaPrincipal;
import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

import adaptadorRecyclerView.AdaptadorProximosEventos;

public class Inicio extends Fragment {
    View vista;
    Context context;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout linearlayout_todosLosUsuarios;
    private RecyclerView recview;
    private Query referencia;
    private ImageView imagenView_banner;
    private   ArrayList<Modelo_evento> lista;
    ValueEventListener oyente;
    private String  urlBanner="";

    // matrices de permisos
    private  static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private String[] cameraPermissions; // cámara y almacenamiento


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        vista = inflater.inflate(R.layout.fragment_home, container, false);
        inicializando();
        cargarBanner();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // solo almacenamiento

        if (!checkCameraPermission()){
            requestCameraPermission();
        }


        imagenView_banner.setOnClickListener(view -> mostrarImagenGrande());

        return vista;
    }

    private boolean checkCameraPermission(){
        // verifica si el permiso de la cámara está habilitado o no
        boolean result = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        // solicita el permiso de la cámara
        ActivityCompat.requestPermissions(requireActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // resultado del permiso permitido / denegado
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(!cameraAccepted && !storageAccepted){

                        Toast.makeText(context, getString(R.string.Se_requieren_permisos), Toast.LENGTH_LONG).show();
                    }
                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){

                    // si se permite devolver verdadero de lo contrario falso
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!storageAccepted){
                          Toast.makeText(context, getString(R.string.Se_requieren_permiso_almacenamiento), Toast.LENGTH_LONG).show();
                    }
                }

            }
            break;
        }
    }

    private void mostrarImagenGrande() {
        Bundle bundle = new Bundle();
        bundle.putString("url_imagen",urlBanner);
        Navigation.findNavController(vista).navigate(R.id.mostrarImagenAmpliada,bundle);
    }


    private void cargarBanner() {

            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Query dataQuery = myRefe.child("Banner").orderByChild("Url");
            dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for(DataSnapshot ignored1 :dataSnapshot.getChildren()) {
                            try {
                                urlBanner=dataSnapshot.child("Url").getValue(String.class);
                                Picasso.with(context).load(urlBanner).into(imagenView_banner);

                            }catch (Exception ignored){
                            }

                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }



    private void inicializando() {
        linearlayout_todosLosUsuarios=vista.findViewById(R.id.linearlayout_todosLosUsuarios);
        imagenView_banner=vista.findViewById(R.id.imagenView_banner);
        recview=vista.findViewById(R.id.recyclerView_proximosEventos);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(context));
        referencia= FirebaseDatabase.getInstance().getReference().child("Eventos").orderByChild("estado").equalTo("Proximamente");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (PantallaPrincipal.perfilUsaurio!=null ) if ( !PantallaPrincipal.perfilUsaurio.equals("Usuario no registrado"))linearlayout_todosLosUsuarios.setVisibility(View.VISIBLE);
        llenandoRecyclerView();
//
    }



    private void llenandoRecyclerView() {
        try {

        if(referencia!=null){
            oyente= referencia.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        lista=new ArrayList<>();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            lista.add(ds.getValue(Modelo_evento.class));
                        }
                        try {
                            lista.sort(Comparator.comparing(Modelo_evento::getFechaHora));
                        }catch (Exception e){
                            Toast.makeText(context , "Un se puede ordenar por fecha, verificar fecha evento", Toast.LENGTH_SHORT).show();
                        }

                        AdaptadorProximosEventos adapador= new AdaptadorProximosEventos(lista);
                        recview.setAdapter(adapador);

                        //Oyente de la seleccion
                        adapador.setOnClickListener(view -> {
                            Modelo_evento evento = lista.get(recview.getChildAdapterPosition(view));
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("evento", evento);
                            Navigation.findNavController(vista).navigate(R.id.detalleEvento,bundle);

                        });


                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
                }
            });
        }

        }catch (Exception e){
            Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            referencia.removeEventListener(oyente);
        }catch (Exception ignored){}
        vista=null;
    }
}
