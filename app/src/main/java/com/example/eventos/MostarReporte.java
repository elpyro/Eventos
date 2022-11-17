package com.example.eventos;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.List;

final public class MostarReporte extends AppCompatActivity implements PDFUtility.OnDocumentClose
{
    private static final String TAG = MostarReporte.class.getSimpleName();

    private Context context;
    private PDFView pdfView;
    private Button button_compartir;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_reporte);

        button_compartir=findViewById(R.id.button_compartirPdf);
        context=this;
//        Modelo_evento evento=(Modelo_evento) getIntent().getExtras().getSerializable("datosEvento");

        pdfView=(PDFView)findViewById(R.id.vista_pdf);

        crearPdf();


        button_compartir.setOnClickListener(view -> compartirPDF());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    private void crearPdf() {
        String path = Environment.getExternalStorageDirectory().toString() + "/reporte.pdf";
        try
        {
            PDFUtility.createPdf(this, MostarReporte.this,path,true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this,"Error Creating Pdf "+e,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPDFDocumentClose(File file)
    {
        MostrarPdf();
    }

    private void MostrarPdf() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/reporte.pdf");
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();


        Reportes.textView_informacion.setText("Reportes");
        Reportes.progress_bar_creadonPDF.setVisibility(View.GONE);

    }


    private void compartirPDF(){

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/reporte.pdf");

        Uri uri = FileProvider.getUriForFile(context, "com.eventos.fileprovider", file);

        Intent intent = ShareCompat.IntentBuilder.from((Activity) context)
                .setType("application/pdf")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

    }



    private List<String[]> getSampleData()
    {
        return null;
    }
}