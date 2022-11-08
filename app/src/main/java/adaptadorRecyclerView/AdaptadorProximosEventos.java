package adaptadorRecyclerView;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_evento;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class AdaptadorProximosEventos extends RecyclerView.Adapter<AdaptadorProximosEventos.MyViewHolder> implements View.OnClickListener {

    private View.OnClickListener listener;
    Context context;
    View vista;
    public ArrayList<Modelo_evento> list;
    @SuppressLint("SimpleDateFormat") String  fechaActualSinFormato= new SimpleDateFormat("HH:mm aa dd-MM-yyyy").format(new Date());
    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm aa dd-MM-yyyy");

    public AdaptadorProximosEventos(ArrayList<Modelo_evento> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proximos_eventos, parent, false);
        vista.setOnClickListener(this);
        return new MyViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_invitados.setText(list.get(position).getBoletosVendidos());
        holder.textView_boletos.setText(list.get(position).getBoletos());
        holder.textView_ciudad.setText(list.get(position).getCiudad());
        crearCuentaRegresiva(position, holder);


        try {
            Picasso.with(context).load(list.get(position).getUrlImagen()).into(holder.imageView_evento);
        }catch (Exception ignored){}

//        holder.linerarLayout_tarjeta.setOnLongClickListener(view -> seleccion(position));

    }
//
//    private boolean seleccion(int position) {
//        Modelo_evento eventoSeleccionado= list.get(position);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("id_evento", (Serializable) eventoSeleccionado);
//        Navigation.findNavController().navigate(R.id.evento,bundle);
//        return false;
//    }

    private String  crearCuentaRegresiva(int position, MyViewHolder holder) {
        String cuenta="";
        try {
            Date fechaActual= formatoFecha.parse(fechaActualSinFormato);
            Date fecha_evento= formatoFecha.parse(list.get(position).getFecha());
            long diff = fecha_evento.getTime() - fechaActual.getTime();

            long segundos = diff / 1000;
            long minutos = segundos / 60;
            long horas = minutos / 60;
            long dias = horas / 24;

            if (dias>0) {
//                cuenta= dias +" Dias";
                holder.textView_cuenta_regresiva.setText(dias+" Dias");
                long diasTrascurridos=dias*24;
                holder.textView_cuenta_regresiva_pequena.setText( (horas-diasTrascurridos)+" Horas");
            }
            if (dias<1) {
                long horasTrascurridas=horas*60;
                holder.textView_cuenta_regresiva.setText(horas+" Horas");
                holder.textView_cuenta_regresiva_pequena.setText((minutos-horasTrascurridas)+" Min");
            }

            if (minutos<0) {
                holder.textView_cuenta_regresiva.setTextColor(Color.parseColor("#FF0000"));
                holder.textView_cuenta_regresiva_pequena.setTextColor(Color.parseColor("#FF0000"));
            }
            if (horas<-12) cambiarEstadoEvento(position);

        } catch (ParseException exception) {        }
        return cuenta;
    }

    private void cambiarEstadoEvento( int position) {
        DatabaseReference myRefe = FirebaseDatabase.getInstance().getReference();
        Modelo_evento evento =  list.get(position);
        evento.setEstado("Terminado");
        myRefe.child("Eventos").child(evento.getId()).setValue(evento);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre, textView_boletos,textView_invitados,textView_ciudad,textView_cuenta_regresiva, textView_cuenta_regresiva_pequena;
        ImageView imageView_evento;
        LinearLayout linerarLayout_tarjeta;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre = itemView.findViewById(R.id.textView_nombre_evento);
            textView_boletos=itemView.findViewById(R.id.textView_boletos);
            textView_invitados=itemView.findViewById(R.id.textView_invitados);
            textView_ciudad= itemView.findViewById(R.id.textView_ciudad);
            imageView_evento=itemView.findViewById(R.id.imageView_imagen_evento);
            textView_cuenta_regresiva=itemView.findViewById(R.id.textView_cuenta_regresiva);
            textView_cuenta_regresiva_pequena=itemView.findViewById(R.id.textView_cuenta_regresiva_pequena);
            linerarLayout_tarjeta=itemView.findViewById(R.id.linerarLayout_tarjeta);



        }
    }
}