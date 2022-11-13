package com.example.eventos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.File;
import java.io.FileOutputStream;


public class MostrarInvitacion extends Fragment {
    private Context context;
    private View vista;
    private TextView textView_estado;
    private TextView textView_direccion;
    private TextView textView_ciudad;
    private TextView textView_costo;
    private TextView textView_fecha;
    private TextView textView_nombre_evento;
    private Switch switch_asistencia;
    @SuppressLint("StaticFieldLeak")
    public static TextView textView_correo_invitado,textView_telefono_invitado,textView_nombre_invitado, textView_nombreEnInvitacion, textView_invitado_por, textView_apline, textView_validus_referido, textView_telefono_referido;
    private Button button_compartir, button_editar;
    private ImageView imageView_qr;
    private CardView cardView_invitacion;
    public String id_invitacion;
    Modelo_invitacion invitacion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_mostrar_invitacion, container, false);

        inicializar();
        recibirArgumentos();
        crearCodigoQr();

        button_compartir.setOnClickListener(view -> prepararInvitacion());
        button_editar.setOnClickListener(view -> editarInvitado());
        switch_asistencia.setOnClickListener(view -> cambiarEstado());
        return vista;
    }

    private void cambiarEstado() {
                Modelo_invitacion cambioEstadoInvitacion= invitacion;
                if ( cambioEstadoInvitacion.getEstado().equals("Pendiente")){
                    cambioEstadoInvitacion.setEstado("Asistido");
                    switch_asistencia.setChecked(true);
                }else if (cambioEstadoInvitacion.getEstado().equals("Asistido")) {
                    cambioEstadoInvitacion.setEstado("Pendiente");
                    switch_asistencia.setChecked(false);
                }


                DatabaseReference CambiarEstado = FirebaseDatabase.getInstance().getReference();
                CambiarEstado.child("Invitaciones").child(cambioEstadoInvitacion.getId()).setValue(cambioEstadoInvitacion);
    }

    private void editarInvitado() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("invitacion",invitacion);
        Navigation.findNavController(vista).navigate(R.id.editarInvitado,bundle);
    }

    private void prepararInvitacion() {
        crearImagenInvitacion();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        compartirInvitacionWhatsapp();
    }

    private void compartirInvitacionWhatsapp() {

            String smsNumber = "57"+ textView_telefono_invitado.getText().toString().trim();
            smsNumber= smsNumber.replace(" ", "");
            boolean isWhatsappInstalled = whatsappInstalledOrNot();
            if (isWhatsappInstalled) {
                try { Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setAction(Intent.ACTION_SEND); sendIntent.setType("text/application/pdf");

                    File file = new File(Environment.getExternalStorageDirectory().toString() + "/invitacion.jpg");
                    Uri uri = FileProvider.getUriForFile(requireContext(), "com.eventos.fileprovider", file);

                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
                    //phone number without "+" prefix sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent); } catch(Exception e)
                { Toast.makeText(getContext(), "Error/n" + e, Toast.LENGTH_SHORT).show(); } } else
            { Uri uri = Uri.parse("market://details?id=com.whatsapp");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                Toast.makeText(getContext(), getString(R.string.WhatsApp_no_instalado), Toast.LENGTH_SHORT).show();
                this.startActivity(goToMarket); }

    }

    private boolean whatsappInstalledOrNot(){
        PackageManager pm = requireContext().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES); app_installed = true;
        } catch (PackageManager.NameNotFoundException e)
        { app_installed = false; } return app_installed;
    }


    private void crearImagenInvitacion() {
        try {
            cardView_invitacion.setBackgroundResource(R.drawable.fondo_tarjeta_invitacion);

            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "invitacion" + ".jpg";

            // create bitmap screen capture
            View v1 = requireActivity().getWindow().getDecorView().getRootView().findViewById(R.id.cardView_invitacion);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            Toast.makeText(context, "Error al crear la invitacion", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void crearCodigoQr() {
        try {
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap= barcodeEncoder.encodeBitmap(textView_nombre_evento.getText().toString(), BarcodeFormat.QR_CODE,1000, 1000);
            imageView_qr.setImageBitmap(bitmap);

        }catch (Exception ignored){}

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
        textView_telefono_referido=vista.findViewById(R.id.textView_telefono_referido);
        textView_apline=vista.findViewById(R.id.textView_apline);
        textView_validus_referido=vista.findViewById(R.id.textView_validus);
        button_compartir=vista.findViewById(R.id.button_compartir);
        button_editar=vista.findViewById(R.id.button_editar);
        imageView_qr=vista.findViewById(R.id.imageView_qr);
        textView_nombre_evento=vista.findViewById(R.id.textView_nombre_evento);
        cardView_invitacion=vista.findViewById(R.id.cardView_invitacion);
        textView_nombreEnInvitacion=vista.findViewById(R.id.textView_nombreEnInvitacion);
        textView_invitado_por=vista.findViewById(R.id.textView_invitado_por);
        switch_asistencia=vista.findViewById(R.id.switch_asistencia);
    }

    private void recibirArgumentos() {
        if (getArguments() != null) {

            id_invitacion = getArguments().getString("id_invitacion");

            DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
            Query dataQuery = myRefe.child("Invitaciones").orderByChild("id").equalTo(id_invitacion).limitToFirst(1);
            dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            invitacion = userSnapshot.getValue(Modelo_invitacion.class);
                            assert invitacion != null;

                            textView_nombreEnInvitacion.setText(invitacion.getNombre());
                            textView_nombre_evento.setText(invitacion.getNombre_evento());
                            textView_fecha.setText(invitacion.getFecha());
                            textView_costo.setText(invitacion.getCosto());
                            textView_ciudad.setText(invitacion.getCiudad());
                            textView_direccion.setText(invitacion.getDireccion());
                            textView_nombre_invitado.setText(invitacion.getNombre());
                            textView_telefono_invitado.setText(invitacion.getTelefono());
                            textView_correo_invitado.setText(invitacion.getCorreo());
                            textView_estado.setText(invitacion.getEstado());
                            textView_invitado_por.setText(invitacion.getNombre_referido());
                            textView_telefono_referido.setText(invitacion.getTelefono_referido());
                            textView_validus_referido.setText(invitacion.getUsuario_validus_referido());
                            textView_apline.setText(invitacion.getApline_referido());
                            if (PantallaPrincipal.perfilUsaurio.equals("Promotor")){
                                switch_asistencia.setVisibility(View.GONE);
                            }
                            if(invitacion.getEstado().equals("Pendiente")){
                                switch_asistencia.setChecked(false);
                            } else if (invitacion.getEstado().equals("Asistido")){
                                switch_asistencia.setChecked(true);
                            } else if (invitacion.getEstado().equals("Firmado")){
                                switch_asistencia.setChecked(true);
                                switch_asistencia.setEnabled(false);
                            }
                            
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