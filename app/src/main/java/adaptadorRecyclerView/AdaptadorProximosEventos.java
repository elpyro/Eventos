package adaptadorRecyclerView;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_evento;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class AdaptadorProximosEventos extends RecyclerView.Adapter<AdaptadorProximosEventos.MyViewHolder> {
    Context context;
    ArrayList<Modelo_evento> list;

    public AdaptadorProximosEventos(ArrayList<Modelo_evento> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proximos_eventos, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_invitados.setText(list.get(position).getBoletosVendidos());
        holder.textView_boletos.setText(list.get(position).getBoletos());
        holder.textView_ciudad.setText(list.get(position).getCiudad());
        try {
            Picasso.with(context).load(list.get(position).getUrlImagen()).into(holder.imageView_evento);
        }catch (Exception ignored){}


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre, textView_boletos,textView_invitados,textView_ciudad,textView_cuenta_regresiva;
        ImageView imageView_evento;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre = itemView.findViewById(R.id.textView_nombre_evento);
            textView_boletos=itemView.findViewById(R.id.textView_boletos);
            textView_invitados=itemView.findViewById(R.id.textView_invitados);
            textView_ciudad= itemView.findViewById(R.id.textView_ciudad);
            imageView_evento=itemView.findViewById(R.id.imageView_imagen_evento);
            textView_cuenta_regresiva=itemView.findViewById(R.id.textView_cuenta_regresiva);



//            cardView_usuario.setOnClickListener(view -> {
//
//                Bundle bundle = new Bundle();
//                bundle.putString("id_evento", textView_id_invisible.getText().toString().trim());
//                Navigation.findNavController(view).navigate(R.id.evento,bundle);
//
//            });
        }
    }
}