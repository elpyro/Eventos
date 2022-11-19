package com.example.eventos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;

public class FirmarCliente extends Fragment {

    private Context context;
    private View vista;
    private Modelo_invitacion invitacion;
    private Button button_firmar;
    private CardView cardView_tarjeta_registro;
    private TextView textView_titulo,textView_validus_referido,textView_apline,textView_telefono_referido,textView_invitado_por,edittext_clave_validus,editText_usuarioValidus,editText_correo,editText_telefono,editText_nombre,textView_password,textView_nombre_firmado,textView_Usuario_validus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_firmar_cliente, container, false);
        inicializando();
        recibirArgumentos();
        button_firmar.setOnClickListener(view -> firmarInvitado());
        return vista;
    }

    private void firmarInvitado() {



        if (!validarDatos()) return;
        textView_nombre_firmado.setText(editText_nombre.getText().toString());
        textView_password.setText(edittext_clave_validus.getText().toString());
        textView_Usuario_validus.setText(textView_Usuario_validus.getText().toString());
        textView_password.setText(invitacion.getNuevo_codigo_validus());
        textView_Usuario_validus.setText(invitacion.getNuevo_usuario_validus());
        textView_password.setText(invitacion.getNuevo_codigo_validus());
        textView_Usuario_validus.setText(invitacion.getNuevo_usuario_validus());
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        invitacion.setNombre(editText_nombre.getText().toString().trim());
        invitacion.setTelefono(editText_telefono.getText().toString().trim());
        invitacion.setCorreo(editText_correo.getText().toString().trim());
        invitacion.setNuevo_usuario_validus(editText_usuarioValidus.getText().toString().trim());
        invitacion.setNuevo_codigo_validus(edittext_clave_validus.getText().toString().trim());
        invitacion.setEstado("Firmado");
        myRefe.child("Invitaciones").child(invitacion.getId()).setValue(invitacion);

        Toast.makeText(context, "Usuario firmado", Toast.LENGTH_SHORT).show();

        compartirTarjeta();
        Navigation.findNavController(vista).navigateUp();


    }

    private void compartirTarjeta() {

            crearImagenTarjeta();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            compartirInvitacionWhatsapp();
        }


    private void crearImagenTarjeta() {
        try {


            String mPath = getActivity().getDatabasePath("bienvenida.jpg").toString();

            // create bitmap screen capture
            View v1 = requireActivity().getWindow().getDecorView().getRootView().findViewById(R.id.cardView_tarjeta_registro);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            cardView_tarjeta_registro.setVisibility(View.GONE);
        } catch (Throwable e) {
            Toast.makeText(context, "Error al crear la invitacion"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void compartirInvitacionWhatsapp() {

        String smsNumber = "57"+ editText_telefono.getText().toString().trim();
        smsNumber= smsNumber.replace(" ", "");

            try { Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND); sendIntent.setType("text/application/pdf");
                File file = new File( getActivity().getDatabasePath("reporte.pdf").toString());
                File filejh = new File( getActivity().getDatabasePath("bienvenida.jpg").toString());
                Uri uri = FileProvider.getUriForFile(requireContext(), "com.eventos.fileprovider", file);

                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
                //phone number without "+" prefix sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent); } catch(Exception e) {
                Log.e("2","eroro compartiendo"+ e);
                Toast.makeText(getContext(), "Error compartiendo: " + e, Toast.LENGTH_LONG).show(); }

    }
//
//    private boolean whatsappInstalledOrNot(){
//        PackageManager pm = requireContext().getPackageManager();
//        boolean app_installed;
//        try {
//            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES); app_installed = true;
//        } catch (PackageManager.NameNotFoundException e)
//        { app_installed = false; } return app_installed;
//    }

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

        if (editText_correo.getText().toString().length()<3){
            editText_correo.setError("Requerido");
            datosAceptados=false;
        }

        if (editText_usuarioValidus.getText().toString().length()<3){
            editText_usuarioValidus.setError("Requerido");
            datosAceptados=false;
        }
        if (edittext_clave_validus.getText().toString().length()<3){
            edittext_clave_validus.setError("Requerido");
            datosAceptados=false;
        }
        return datosAceptados;
    }

    private void inicializando() {
        button_firmar=vista.findViewById(R.id.button_firmar);
        textView_validus_referido =vista.findViewById(R.id.textView_validus);
        textView_apline=vista.findViewById(R.id.textView_apline);
        textView_telefono_referido=vista.findViewById(R.id.textView_telefono_referido);
        textView_invitado_por=vista.findViewById(R.id.textView_invitado_por);
        edittext_clave_validus=vista.findViewById(R.id.edittext_clave_validus);
        editText_usuarioValidus=vista.findViewById(R.id.editText_usuarioValidus);
        editText_correo=vista.findViewById(R.id.editText_correo);
        editText_telefono=vista.findViewById(R.id.editText_telefono);
        editText_nombre=vista.findViewById(R.id.editText_nombre);
        textView_titulo=vista.findViewById(R.id.textView_titulo);
        textView_password=vista.findViewById(R.id.textView_password);
        textView_nombre_firmado=vista.findViewById(R.id.textView_nombre_firmado);
        textView_Usuario_validus=vista.findViewById(R.id.textView_Usuario_validus);
        cardView_tarjeta_registro=vista.findViewById(R.id.cardView_tarjeta_registro);
        cardView_tarjeta_registro.setBackgroundResource(R.drawable.fondo_firma);
    }

    @SuppressLint("SetTextI18n")
    private void recibirArgumentos() {
        Bundle bundle = getArguments();
        if (getArguments() != null) {
            invitacion =(Modelo_invitacion) bundle.getSerializable("invitacion");

            editText_nombre.setText(invitacion.getNombre());
            editText_telefono.setText(invitacion.getTelefono());
            editText_correo.setText(invitacion.getCorreo());
            editText_usuarioValidus.setText(invitacion.getNuevo_usuario_validus());
            edittext_clave_validus.setText(invitacion.getNuevo_codigo_validus());
            textView_invitado_por.setText(invitacion.getNombre_referido());
            textView_telefono_referido.setText(invitacion.getTelefono_referido());
            textView_apline.setText(invitacion.getApline_referido());
            textView_validus_referido.setText(invitacion.getUsuario_validus_referido());

            textView_nombre_firmado.setText(invitacion.getNombre());
            textView_password.setText(invitacion.getNuevo_codigo_validus());
            textView_Usuario_validus.setText(invitacion.getNuevo_usuario_validus());

            if(invitacion.getEstado().equals("Firmado")){
                textView_titulo.setText("Invitado firmado");
                button_firmar.setText("Compartir");
            }

        }
    }
}