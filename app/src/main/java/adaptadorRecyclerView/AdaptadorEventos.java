package adaptadorRecyclerView;


import android.content.Context;
import android.graphics.Color;
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
public class AdaptadorEventos extends RecyclerView.Adapter<AdaptadorEventos.MyViewHolder> {
    Context context;
    ArrayList<Modelo_evento> list;

    public AdaptadorEventos(ArrayList<Modelo_evento> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_id_invisible.setText(list.get(position).getId());
        if (list.get(position).getEstado().equals("Terminado"))holder.textView_nombre.setTextColor(Color.parseColor("#FFB039"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre, textView_id_invisible;

        LinearLayout linearLayout_evento;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre = itemView.findViewById(R.id.textView_nombre);
            textView_id_invisible=itemView.findViewById(R.id.textView_id);
            linearLayout_evento= itemView.findViewById(R.id.linerarLayout_tarjeta);

            linearLayout_evento.setOnClickListener(view -> {

                Bundle bundle = new Bundle();
                bundle.putString("id_evento", textView_id_invisible.getText().toString().trim());
                Navigation.findNavController(view).navigate(R.id.evento,bundle);

            });
        }
    }
}