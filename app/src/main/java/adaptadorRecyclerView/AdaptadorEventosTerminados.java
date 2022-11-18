package adaptadorRecyclerView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_evento;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class AdaptadorEventosTerminados extends RecyclerView.Adapter<AdaptadorEventosTerminados.MyViewHolder> implements View.OnClickListener {
    private View.OnClickListener listener;
    Context context;
    View vista;
    ArrayList<Modelo_evento> list;

    public AdaptadorEventosTerminados(ArrayList<Modelo_evento> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_eventos_terminados, parent, false);
        vista.setOnClickListener(this);
        return new MyViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        String fecha=list.get(position).getFecha();
        holder.textView_fecha.setText(fecha.substring(fecha.length() - 10));

        holder.linearLayout_evento.setOnClickListener(view -> {
            Modelo_evento evento = list.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("evento", evento);
            Navigation.findNavController(view).navigate(R.id.detalleEvento,bundle);
        });
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
        TextView textView_nombre, textView_fecha;

        LinearLayout linearLayout_evento;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre = itemView.findViewById(R.id.textView_nombre);
            textView_fecha=itemView.findViewById(R.id.textView_fecha);
            linearLayout_evento= itemView.findViewById(R.id.linerarLayout_tarjeta);


        }
    }
}