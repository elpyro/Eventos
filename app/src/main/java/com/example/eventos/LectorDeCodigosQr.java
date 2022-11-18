package com.example.eventos;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class LectorDeCodigosQr extends AppCompatActivity {
    String codigoEscaner;
    private String desdeFramgento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_de_codigos_qr);
        RecibirArgumentos();
    }


    private void RecibirArgumentos() {

        desdeFramgento = getIntent().getExtras().getString("desdeFragmento");
    }

    public void Lectorbarras() {
        //SCANNER
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        if (desdeFramgento.equals("ConfirmarAsistencia")){
            integrator.setPrompt("Confirmar Asistencia");
        }else{
            integrator.setPrompt("Firmar Invitado");
        }

        integrator.setCaptureActivity(ActivityOrientacionScanner.class);
        integrator.initiateScan();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (desdeFramgento.equals("ConfirmarAsistencia")) {
            Lectorbarras();
        } else if (desdeFramgento.equals("ConfirmarFirma")) {
            if (ConfirmarFirma.codigoEncontrado != null) {
                codigoEscaner = null;
                finish();
            } else {
                Lectorbarras();
            }
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //LECTOR DE BARRAS ONACTIVITY
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode,data);

        if (result !=null){
            if(result.getContents()==null) {
                finish();
            }else{

                codigoEscaner=result.getContents();
                codigoEscaner=codigoEscaner.substring(codigoEscaner.length()-7);
                if(desdeFramgento.equals("ConfirmarFirma")) {
                    buscarEnListaAsitidos();
                }else if(desdeFramgento.equals("ConfirmarAsistencia")){
                    buscarEnListaInvitados();
                }

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void buscarEnListaInvitados() {
        boolean encontrado=false;
        Modelo_invitacion invitacion = null;
        for (int x = 0; x < ConfirmarAsistencia.listaInvitados.size(); x++) {
             invitacion = ConfirmarAsistencia.listaInvitados.get(x);
            if (invitacion.getId().substring(0,7).equals(codigoEscaner)) {
                ConfirmarAsistencia.codigoEncontrado = invitacion;
                encontrado=true;
                break;
            }
        }
        if (!encontrado) {
            Toast.makeText(this, "Codigo invalido", Toast.LENGTH_SHORT).show();
            SonidoBeep();
        }else{
             cambiarEstado(invitacion);
        }

    }

    private void SonidoBeep() {
        ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80);
        tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);//sonido
    }

    private void cambiarEstado(  Modelo_invitacion invitacion) {
        if ( invitacion.getEstado().equals("Invitado")){
            invitacion.setEstado("Asistido");
            Toast.makeText(this, "Asitencia confirmada", Toast.LENGTH_SHORT).show();
        }else  {
            Toast.makeText(this, "El invitado ya fue marcado Asistido", Toast.LENGTH_SHORT).show();
            SonidoBeep();
        }

        DatabaseReference CambiarEstado = FirebaseDatabase.getInstance().getReference();
        CambiarEstado.child("Invitaciones").child(invitacion.getId()).setValue(invitacion);
    }

    private void buscarEnListaAsitidos() {
        boolean encontrado=false;
        for (int x = 0; x < ConfirmarFirma.listaAsistidos.size(); x++) {
            Modelo_invitacion invitacion = ConfirmarFirma.listaAsistidos.get(x);
            if (invitacion.getId().substring(0,7).equals(codigoEscaner)) {
                ConfirmarFirma.codigoEncontrado = invitacion;
                encontrado=true;
                break;
            }
        }
        if (!encontrado){
            Toast.makeText(this, "Codigo invalido", Toast.LENGTH_SHORT).show();
            SonidoBeep();
        }

    }

}