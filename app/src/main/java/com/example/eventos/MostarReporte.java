package com.example.eventos;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.eventos.modelos.Modelo_evento;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

final public class MostarReporte extends AppCompatActivity implements PDFUtility.OnDocumentClose
{
    private static final String TAG = MostarReporte.class.getSimpleName();
    private AppCompatEditText rowCount;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_reporte);
        rowCount = findViewById(R.id.rowCount);
        context=this;
        Modelo_evento evento=(Modelo_evento) getIntent().getExtras().getSerializable("datosEvento");

        AppCompatButton button1 = findViewById(R.id.button1);



        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String path = Environment.getExternalStorageDirectory().toString() + "/reporte.pdf";
                try
                {
                    PDFUtility.createPdf(v.getContext(), MostarReporte.this,getSampleData(),path,true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(),"Error Creating Pdf "+e,Toast.LENGTH_SHORT).show();
                }
                compartirPDF();
            }

            private void compsartirPDF() {



                // Así va correctamente la dirección





//                ntent sendIntent = new Intent("android.intent.action.MAIN");
//                sendIntent.setAction(Intent.ACTION_SEND); sendIntent.setType("text/application/pdf");
//

//
//
//                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
//                //phone number without "+" prefix sendIntent.setPackage("com.whatsapp");
//                startActivity(sendIntent); } catch(Exception e)

            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    @Override
    public void onPDFDocumentClose(File file)
    {
        Toast.makeText(this,"Sample Pdf Created",Toast.LENGTH_SHORT).show();

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
        int count = 20;
        if(!TextUtils.isEmpty(rowCount.getText()))
        {
            count = Integer.parseInt(rowCount.getText().toString());
        }

        List<String[]> temp = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            temp.add(new String[] {"C1-R"+ (i+1),"C2-R"+ (i+1)});
        }
        return  temp;
    }
}