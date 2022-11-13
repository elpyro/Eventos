package adaptadorRecyclerView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_invitacion;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class AdaptadorConfimarFirma extends RecyclerView.Adapter<AdaptadorConfimarFirma.MyViewHolder> {
    Context context;
    ArrayList<Modelo_invitacion> list;

    public AdaptadorConfimarFirma(ArrayList<Modelo_invitacion> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asistencia, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_id_invisible.setText(list.get(position).getId());


        holder.imageView_cheked.setVisibility(View.GONE);
        holder.imageView_cheked_firmado.setVisibility(View.GONE);
        if (list.get(position).getEstado().equals("Firmado"))holder.imageView_cheked_firmado.setVisibility(View.VISIBLE);

        holder.linerarLayout_tarjeta.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("invitacion",list.get(position));
            Navigation.findNavController(view).navigate(R.id.firmarCliente,bundle);
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre, textView_id_invisible;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        ImageView imageView_cheked, imageView_cheked_firmado;
        LinearLayout linerarLayout_tarjeta;
       

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre = itemView.findViewById(R.id.textView_nombre);
            textView_id_invisible=itemView.findViewById(R.id.textView_invisible);
            imageView_cheked = itemView.findViewById(R.id.imageView_cheked);
            imageView_cheked_firmado=itemView.findViewById(R.id.imageView_cheked_firmado);
            linerarLayout_tarjeta=itemView.findViewById(R.id.linerarLayout_tarjeta);


        }
    }
}