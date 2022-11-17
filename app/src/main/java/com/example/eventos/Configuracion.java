package com.example.eventos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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


public class Configuracion extends Fragment {


    private View vista;
    private Context context;
    private EditText edittext_habeasData;
    private ImageView imagenView_banner;
    private Button button_cambiar_imagen,button_cambiar_habeasData;
    private  static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private String[] cameraPermissions; // cámara y almacenamiento
    private static final int SELECT_FILE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_configuracion, container, false);
        inicializando();
        cargarHabeasData();
        cargarImgagenBanner();
        button_cambiar_habeasData.setOnClickListener(view -> guardarHabeasData());
        button_cambiar_imagen.setOnClickListener(view -> seleccionarImagen());

        return vista;
    }




        private boolean isNetworkConnected(Context context) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info == null || !info.isConnected() || !info.isAvailable()) {
            Toast.makeText(context, "Necesita conexión a internet para subir imagenes", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    private void seleccionarImagen() {
        // muestra el cuadro de diálogo de selección de imagen
        if (isNetworkConnected(context)==false)return;
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

                    imagenView_banner.setImageBitmap(bmp);

                    cambiarImagen();

                }
            }
        }
    }


    private void cambiarImagen() {
        if (isNetworkConnected(context)==false)return;
        button_cambiar_imagen.setEnabled(false);

        new Thread(() -> {



            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();


            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference to "mountains.jpg"
            StorageReference mountainsRef = storageRef.child("imagen_banner/" + "banner_inicio");


            // [START upload_memory]
            // Get the data from an ImageView as bytes

            imagenView_banner.setDrawingCacheEnabled(true);
            imagenView_banner.buildDrawingCache();
            byte[] data;
            try {
                Bitmap bitmap = ((BitmapDrawable) imagenView_banner.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                data = baos.toByteArray();


                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(exception -> Toast.makeText(context, "Problemas de conexion", Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {

                    //guardando la url para la imagen
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;

                    Uri descargaUri = uriTask.getResult();


                    myRefe.child("Banner").child("Url").setValue(descargaUri.toString());

                    try{

                    requireActivity().runOnUiThread(() -> {
                        button_cambiar_imagen.setEnabled(true);
                        Toast.makeText(context, "Banner cambiado", Toast.LENGTH_SHORT).show();
                    });
                    }catch (Exception e){}
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
    }

    private void guardarHabeasData() {
        if (edittext_habeasData.getText().toString().trim().length()<0) {
            Toast.makeText(context, "Nada que guardar", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("HabeasData").child("descripcion").setValue(edittext_habeasData.getText().toString().trim());
        Toast.makeText(context, "Guardado", Toast.LENGTH_LONG).show();
    }

    private void inicializando() {
        button_cambiar_imagen=vista.findViewById(R.id.button_cambiar_imagen);
        button_cambiar_habeasData=vista.findViewById(R.id.button_cambiar_habeasData);
        edittext_habeasData=vista.findViewById(R.id.editTextTextMultiLine_habeasData);
        imagenView_banner=vista.findViewById(R.id.imageView_banner);
    }

    private void cargarImgagenBanner() {
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Query dataQuery = myRefe.child("Banner").orderByChild("Url");
        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()) {
                        try {
                            Picasso.with(context).load(dataSnapshot.child("Url").getValue(String.class)).into(imagenView_banner);

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

    private void cargarHabeasData() {
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Query dataQuery = myRefe.child("HabeasData").orderByChild("descripcion");
        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    edittext_habeasData.setText(dataSnapshot.child("descripcion").getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}