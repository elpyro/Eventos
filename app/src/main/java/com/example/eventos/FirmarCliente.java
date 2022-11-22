package com.example.eventos;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView imageView_bottonWhatapps;
    private String aperturaInvitacion="No enviada";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        vista= inflater.inflate(R.layout.fragment_firmar_cliente, container, false);
        inicializando();
        recibirArgumentos();
        button_firmar.setOnClickListener(view -> firmarInvitado());

        edittext_clave_validus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                textView_password.setText(edittext_clave_validus.getText().toString().trim());
            }
        });


        editText_usuarioValidus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                textView_Usuario_validus.setText(editText_usuarioValidus.getText().toString().trim());
            }
        });

        imageView_bottonWhatapps.setOnClickListener(view -> enviarMensajeWhataaps());


        return vista;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(!aperturaInvitacion.equals("No enviada")){
            compartirInvitacionWhatsapp();
            aperturaInvitacion="No enviada";
            Navigation.findNavController(vista).navigateUp();

        }
    }

    public void enviarMensajeWhataaps() {


        String smsNumber = "57"+ textView_telefono_referido.getText().toString().trim();
        smsNumber= smsNumber.replace(" ", "");
        smsNumber= smsNumber.replace("+57", "");


        try { Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber)+"@s.whatsapp.net");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola, ");
            startActivity(sendIntent);
        } catch(Exception e)  {
            Toast.makeText(getContext(), "Error enviado Whatsapp: "  + e, Toast.LENGTH_SHORT).show();
        }


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




    }

    private void compartirTarjeta() {

            crearImagenTarjeta();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            prepararInvitacion();
        }

    private void prepararInvitacion() {
        String smsNumber = "57" + editText_telefono.getText().toString().trim();
        smsNumber = smsNumber.replace(" ", "");
        smsNumber = smsNumber.replace("+57", "");
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uri = "whatsapp://send?phone=" + smsNumber + "&text=" + "Hola sr@ " + editText_nombre.getText().toString().trim() + ", mucho gusto mi nombre es " + PantallaPrincipal.nombreUsuario.trim() + ", Bienvenido a nuetra comunidad";
            intent.setData(Uri.parse(uri));
            aperturaInvitacion = "Enviada";
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void crearImagenTarjeta() {
        try {


            String mPath =Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES)+"/bienvenida.jpg";

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
            Toast.makeText(context, "Error al crear la invitacion: "+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void compartirInvitacionWhatsapp() {


        String smsNumber = "57"+ editText_telefono.getText().toString().trim();
        smsNumber= smsNumber.replace(" ", "");

        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/application/pdf");
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES)+ "/bienvenida.jpg");
            Uri uri = FileProvider.getUriForFile(requireContext(), "com.eventos.fileprovider", file);

            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
            aperturaInvitacion="Enviada";
            startActivity(sendIntent);
        } catch(Exception e)
        { Toast.makeText(getContext(), "Error enviado Whatsapp:" + e, Toast.LENGTH_SHORT).show(); }



//            try { Intent sendIntent = new Intent("android.intent.action.MAIN");
//                sendIntent.setAction(Intent.ACTION_SEND); sendIntent.setType("image/jpeg");
//
//                File file = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES)+"/bienvenida.jpg");
//                Uri uri = FileProvider.getUriForFile(requireContext(), "com.eventos.fileprovider", file);
//
//                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                sendIntent.setPackage("com.whatsapp");
//                sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
//                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber)+"@s.whatsapp.net");
//
//                startActivity(sendIntent); } catch(Exception e) {
//                Log.e("2","Erro compartiendo"+ e);
//                Toast.makeText(getContext(), "Error compartiendo: " + e, Toast.LENGTH_LONG).show(); }

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
        textView_invitado_por=vista.findViewById(R.id.textView_nombre_referido);
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
        imageView_bottonWhatapps=vista.findViewById(R.id.imageView_whatapps);
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