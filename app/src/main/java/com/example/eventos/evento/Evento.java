package com.example.eventos.evento;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eventos.PantallaPrincipal;
import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_evento;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class Evento extends Fragment {

        private String id_exitente="";
        private View vista;
        private Context context;
        private EditText editText_nombre, editText_descripcion, editText_ciudad,editText_direccion,editText_boletos_vendidos,editText_boletos,
                editText_costo;
        private Button button_imagen, button_guardar;
        private TextView textView_titulo, textView_edicion;
        private ImageView imageView_evento;
        private TextView textView_fecha, textView_hora;
        private  static final int CAMERA_REQUEST_CODE = 100;
        private static final int STORAGE_REQUEST_CODE = 101;
        private String[] cameraPermissions; // cámara y almacenamiento
    private static final int SELECT_FILE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista= inflater.inflate(R.layout.fragment_evento, container, false);

        incializando();
        recibirArgumentos();

        button_guardar.setOnClickListener(view -> guardar());
        button_imagen.setOnClickListener(view -> seleccionarImagen());
        textView_fecha.setOnClickListener(view -> mostrarSelectorFecha());
        textView_hora.setOnClickListener(view -> mostrarSelectorHora());

        return vista;
    }

    private void incializando() {
        context=getContext();
        editText_nombre=vista.findViewById(R.id.editText_nombre);
        editText_ciudad=vista.findViewById(R.id.editText_ciudad);
        editText_descripcion=vista.findViewById(R.id.editText_descripcion);
        editText_direccion=vista.findViewById(R.id.editText_direccion);
        editText_boletos_vendidos=vista.findViewById(R.id.editText_boletos_vendidos);
        editText_boletos=vista.findViewById(R.id.editText_boletos);
        editText_costo=vista.findViewById(R.id.editText_costo);
        textView_fecha=vista.findViewById(R.id.textView_fecha);
        textView_hora=vista.findViewById(R.id.textView_hora);
        button_imagen=vista.findViewById(R.id.button_imagen);
        button_guardar=vista.findViewById(R.id.button_guardar);
        textView_titulo=vista.findViewById(R.id.textView_titulo);
        textView_edicion=vista.findViewById(R.id.textView_edicion);
        imageView_evento=vista.findViewById(R.id.imageView_evento);

    }

    private void seleccionarImagen() {
                        // muestra el cuadro de diálogo de selección de imagen

                //ocultar teclado
        InputMethodManager input = (InputMethodManager) (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(vista.getWindowToken(), 0);

        //permisos de almacenamiento
        cameraPermissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // solo almacenamiento

        if (!checkCameraPermission()){
            requestCameraPermission();
        }else{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, "Seleccione una imagen"),
                    SELECT_FILE);
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


    private void mostrarSelectorHora() {
        final Calendar getDate = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
            getDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            getDate.set(Calendar.MINUTE, minute);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm a");
            String formatedDate = timeformat.format(getDate.getTime());
            textView_hora.setText(formatedDate);
        }, getDate.get(Calendar.HOUR_OF_DAY), getDate.get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }

    private void mostrarSelectorFecha() {
        final Calendar c=Calendar.getInstance();
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH);
        int ano=c.get(Calendar.YEAR);

        DatePickerDialog datepickerDialogo = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            month=month+1;
            String fecha_desde;

            if(dayOfMonth<10){
                fecha_desde= "0"+dayOfMonth+"-"+month+"-"+ year;
            }else{
                fecha_desde=dayOfMonth+"-"+month+"-"+ year;
            }
            textView_fecha.setText(fecha_desde);
        },ano,mes,dia);
        datepickerDialogo.show();
    }

    private void recibirArgumentos() {
        //si hay argumentos el evento va a ser editado
        if (getArguments() != null) {

            String id_evento = getArguments().getString("id_evento");
            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Query dataQuery = myRefe.child("Eventos").orderByChild("id").equalTo(id_evento).limitToFirst(1);
            dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            Modelo_evento evento = userSnapshot.getValue(Modelo_evento.class);
                            assert evento != null;
                            id_exitente=evento.getId();
                            editText_nombre.setText(evento.getNombre());
                            editText_descripcion.setText(evento.getDescripcion());
                            editText_ciudad.setText(evento.getCiudad());
                            editText_direccion.setText(evento.getDireccion());
                            String fecha=evento.getFecha();
                            textView_fecha.setText(fecha.substring(fecha.length() - 10));
                            textView_hora.setText(evento.getHora());
                            editText_costo.setText(evento.getCosto());
                            editText_boletos.setText(evento.getBoletos());
                            editText_boletos_vendidos.setText(evento.getBoletosVendidos());
                            textView_edicion.setText(evento.getEdicion());
                            String estado=evento.getEstado().trim();
                            textView_titulo.setText(estado);
                            //TODO ESTE IF NO RECONOCE R.STRIN.TERMINADO
                            if(estado.equals("Terminado")){
                                button_guardar.setEnabled(false);
                            }
                            try {
                                Picasso.with(context).load(evento.getUrlImagen()).into(imageView_evento);
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
    }

    private boolean validarDatos(){
        boolean datosAceptados=true;
        if (editText_nombre.getText().toString().length()<3){
            editText_nombre.setError("Requerido");
            datosAceptados=false;
        }
        if (editText_ciudad.getText().toString().length()<3){
            editText_ciudad.setError("Requerido");
            datosAceptados=false;
        }
        if (editText_direccion.getText().toString().length()<3){
            editText_direccion.setError("Requerido");
            datosAceptados=false;
        }

        if (editText_costo.getText().toString().length()==0){
            editText_costo.setError("Requerido");
            datosAceptados=false;
        }
        if (editText_boletos.getText().toString().length()==0){
            editText_boletos.setError("Requerido");
            datosAceptados=false;
        }
        if (textView_hora.getText().toString().trim().equals("Hora")){
            datosAceptados=false;
            Toast.makeText(context, "Por favor establecer hora del evento", Toast.LENGTH_LONG).show();
        }
        if (textView_fecha.getText().toString().trim().equals("Fecha")) {
            datosAceptados=false;
            Toast.makeText(context, "Por favor establecer fecha del evento", Toast.LENGTH_LONG).show();
        }
        if (!verificarFecha()) datosAceptados=false;
        return  datosAceptados;
    }

    private boolean verificarFecha() {

        boolean fechaCorrecta=true;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        @SuppressLint("SimpleDateFormat") String fechaActual= new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        Date fechaSeleccionada;
        try {
            fechaSeleccionada = dateFormat.parse(textView_fecha.getText().toString());
            Date fechaActualf=dateFormat.parse(fechaActual);
            assert fechaSeleccionada != null;
            if (fechaSeleccionada.compareTo(fechaActualf)<0 ){
                fechaCorrecta=false;
                Toast.makeText(context, "Fecha incorrecta", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fechaCorrecta;
    }

    private void guardar() {

        if (!validarDatos())return;

        new Thread(() -> {

            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Modelo_evento evento = new Modelo_evento();

            //si se recibieron argumentos el id es el mismo. EDITAR
            if (!id_exitente.equals("")){
                evento.setId(id_exitente);
            }else{
                evento.setId(UUID.randomUUID().toString());
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference to "mountains.jpg"
            StorageReference mountainsRef = storageRef.child("imagen_eventos/" + evento.getId());


            // [START upload_memory]
            // Get the data from an ImageView as bytes

            imageView_evento.setDrawingCacheEnabled(true);
            imageView_evento.buildDrawingCache();
            byte[] data;
            try {
                Bitmap bitmap = ((BitmapDrawable) imageView_evento.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                data = baos.toByteArray();


                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(exception -> Toast.makeText(context, "Problemas de conexion", Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {

                    //guardando la url para la imagen
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;

                    Uri descargaUri = uriTask.getResult();

                    @SuppressLint("SimpleDateFormat") String fecha= new SimpleDateFormat("hh:mm aa dd-MM-yy").format(new Date());

                    evento.setNombre(editText_nombre.getText().toString().trim());
                    evento.setDescripcion(editText_descripcion.getText().toString().trim());
                    evento.setCiudad(editText_ciudad.getText().toString().trim());
                    evento.setDireccion(editText_direccion.getText().toString().trim());
                    evento.setFecha(textView_hora.getText().toString().trim() +" " +textView_fecha.getText().toString().trim());
                    evento.setHora(textView_hora.getText().toString().trim());
                    evento.setCosto(editText_costo.getText().toString().trim());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm aa dd-MM-yyyy");
                    Date fecha_evento = null;
                    try {
                         fecha_evento= formatoFecha.parse((textView_hora.getText().toString().trim() +" " +textView_fecha.getText().toString().trim()));
                    } catch (ParseException exception) {
                        exception.printStackTrace();
                    }
                    evento.setFechaHora(fecha_evento);
                    evento.setBoletos(editText_boletos.getText().toString().trim());
                    if (editText_boletos.getText().equals("")){
                        evento.setBoletosVendidos("0");
                    }else{
                        evento.setBoletosVendidos(editText_boletos_vendidos.getText().toString().trim());
                    }
                    //TODO colocar r.string
                    evento.setEstado("Proximamente");
                    evento.setEdicion("Editado: " + PantallaPrincipal.nombreUsuario +" "+ fecha );
                    evento.setUrlImagen(descargaUri.toString());
                    myRefe.child("Eventos").child(evento.getId()).setValue(evento);
                    Toast.makeText(context, "Evento Guardado", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
            try {
                requireActivity().runOnUiThread(() -> {
                    try {
                        Toast.makeText(context, "Problemas de conexion", Toast.LENGTH_SHORT).show();
                    }catch (Exception ignored){ }

                });
            }catch (Exception er){
                Toast.makeText(context, "Problemas de conexion", Toast.LENGTH_SHORT).show();
            }
            }


        }).start();
        requireActivity().onBackPressed();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImage;

        if (requestCode == SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                selectedImage = imageReturnedIntent.getData();
                String selectedPath = selectedImage.getPath();

                if (selectedPath != null) {
                    InputStream imageStream = null;
                    try {
                        imageStream = requireActivity().getContentResolver().openInputStream(
                                selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                    // Ponemos nuestro bitmap en un ImageView que tengamos en la vista

                    imageView_evento.setImageBitmap(bmp);

                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        InputMethodManager input = (InputMethodManager) (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(vista.getWindowToken(), 0);
        vista=null;
    }
}