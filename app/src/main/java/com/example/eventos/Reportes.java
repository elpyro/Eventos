package com.example.eventos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eventos.modelos.Modelo_evento;
import com.example.eventos.modelos.Modelo_invitacion;
import com.example.eventos.modelos.Modelo_spinner_recuperarId;
import com.example.eventos.modelos.Modelo_usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class Reportes extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progress_bar_creadonPDF;
    public static Modelo_evento evento;
    public static ArrayList<Modelo_invitacion> invitaciones=new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static TextView textView_informacion;
    private Context context;
    private View vista;
    private Spinner spinner_estado, spinner_evento, spinner_referido;
    private Button button_crearReporte;
    ArrayList<Modelo_spinner_recuperarId> nombreEvento;
    ArrayList<Modelo_spinner_recuperarId> nombreReferido;
    private String idEventoSeleccionado, id_referidoSeleccionado, estadoSeleccionado;

    private  static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private String[] cameraPermissions; // cámara y almacenamiento

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_reportes, container, false);

        inicializando();

        button_crearReporte.setOnClickListener(view -> permisos());
        spinner_evento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idEventoSeleccionado= nombreEvento.get(i).getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner_referido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_referidoSeleccionado= nombreReferido.get(i).getId();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                estadoSeleccionado =adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        return vista;
    }


    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("LOG_TAG", "Directory not created");
        }
        return file;
    }

    private void permisos(){
    cameraPermissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // solo almacenamiento

        if (!checkCameraPermission()){
        requestCameraPermission();
      }else{
            crearReporte();
    }


}


    private boolean checkCameraPermission(){
        // verifica si el permiso de la cámara está habilitado o no
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
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






    @Override
    public void onResume() {
        super.onResume();

        llenandoSpinner();
        button_crearReporte.setEnabled(true);
    }

    @SuppressLint("SetTextI18n")
    private void crearReporte() {
        textView_informacion.setText("Creando PDF...");
        progress_bar_creadonPDF.setVisibility(View.VISIBLE);
        button_crearReporte.setEnabled(false);
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Query dataQuery = myRefe.child("Eventos").orderByChild("id").equalTo(idEventoSeleccionado).limitToFirst(1);
        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                       evento = userSnapshot.getValue(Modelo_evento.class);

                        buscarInvitacionesDelEvento();


                    }
                } else {
                    Toast.makeText(context,getString(R.string.EventoInexistente) , Toast.LENGTH_SHORT).show();
                    limpiar();
                }
            }



            private void buscarInvitacionesDelEvento() {
                DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
                Query dataQuery = myRefe.child("Invitaciones").orderByChild("id_evento").equalTo(idEventoSeleccionado);
                dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            invitaciones.clear();
                            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                boolean registrarInvitacion=false;
                                if (id_referidoSeleccionado.equals("0")) {
                                    if(estadoSeleccionado.equals("Todos"))registrarInvitacion=true;
                                    if(estadoSeleccionado.equals(Objects.requireNonNull(userSnapshot.getValue(Modelo_invitacion.class)).getEstado()))registrarInvitacion=true;
                                }
                                if (id_referidoSeleccionado.equals( Objects.requireNonNull(userSnapshot.getValue(Modelo_invitacion.class)).getId_usuario_referido())) {
                                    if(estadoSeleccionado.equals("Todos"))registrarInvitacion=true;
                                    if(estadoSeleccionado.equals(Objects.requireNonNull(userSnapshot.getValue(Modelo_invitacion.class)).getEstado()))registrarInvitacion=true;
                                }

                                if(registrarInvitacion)    invitaciones.add( userSnapshot.getValue(Modelo_invitacion.class));


                            }
                            if (invitaciones.size()==0) {
                                Toast.makeText(context, "Este evento no tiene invitaciones", Toast.LENGTH_LONG).show();
                                limpiar();
                            }else{
                                Intent intent = new Intent(context, MostarReporte.class);
                                intent.putExtra("datosEvento", evento);
                                startActivity(intent);
                            }

                        } else {
                            Toast.makeText(context, "Este evento no tiene invitaciones", Toast.LENGTH_LONG).show();
                            limpiar();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, getString(R.string.problemasConexion), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, getString(R.string.problemasConexion), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("SetTextI18n")
    public void limpiar() {
        progress_bar_creadonPDF.setVisibility(View.GONE);
        textView_informacion.setText("Reportes");
        button_crearReporte.setEnabled(true);
    }
    private void llenandoSpinner() {
        llenandoSpinnerEvento();
        llenandoSpinnerReferido();
        llenandoSpinnerEstado();
    }

    private void llenandoSpinnerEstado() {
        String [] estados= {"Todos","Invitado","Asistido","Firmado"};

        ArrayAdapter<String> adapter= new ArrayAdapter<>(context, R.layout.spinner_item, estados);
        spinner_estado.setAdapter(adapter);
    }

    private void llenandoSpinnerReferido() {
        Query referenciaReferidos = FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("nombre");

        referenciaReferidos.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                   nombreReferido = new ArrayList<>();
                    nombreReferido.add(new Modelo_spinner_recuperarId("0", "Todos"));
                    for(DataSnapshot ds:snapshot.getChildren()){
                        nombreReferido.add(new Modelo_spinner_recuperarId(Objects.requireNonNull(ds.getValue(Modelo_usuario.class)).getId(), Objects.requireNonNull(ds.getValue(Modelo_usuario.class)).getNombre()));
                    }
                    ArrayAdapter<Modelo_spinner_recuperarId> adaptadorReferido= new ArrayAdapter<>(context, R.layout.spinner_item, nombreReferido);
                    spinner_referido.setAdapter(adaptadorReferido);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void llenandoSpinnerEvento() {

        Query referenciaEventos = FirebaseDatabase.getInstance().getReference().child("Eventos").orderByChild("fechaHora");

        referenciaEventos.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                     nombreEvento = new ArrayList<>();

                    for(DataSnapshot ds:snapshot.getChildren()){
                        nombreEvento.add(new Modelo_spinner_recuperarId(Objects.requireNonNull(ds.getValue(Modelo_usuario.class)).getId(), Objects.requireNonNull(ds.getValue(Modelo_usuario.class)).getNombre()));
                    }
                    ArrayAdapter<Modelo_spinner_recuperarId> adapterEvento= new ArrayAdapter<>(context, R.layout.spinner_item, nombreEvento);
                    spinner_evento.setAdapter(adapterEvento);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error en conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializando() {
        textView_informacion=vista.findViewById(R.id.textView_informacion);
        spinner_estado=vista.findViewById(R.id.spinner_estado);
        spinner_evento=vista.findViewById(R.id.spinner_evento);
        spinner_referido=vista.findViewById(R.id.spinner_referido);
        button_crearReporte=vista.findViewById(R.id.button_crearReporte);
        progress_bar_creadonPDF=vista.findViewById(R.id.progress_bar_creadonPDF);
    }
}