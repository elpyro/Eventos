package adaptadorRecyclerView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_invitacion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class AdaptadorConfirmarAsistencia extends RecyclerView.Adapter<AdaptadorConfirmarAsistencia.MyViewHolder> {
    Context context;
    public ArrayList<Modelo_invitacion> list;

    public AdaptadorConfirmarAsistencia(ArrayList<Modelo_invitacion> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirmar_asistencia, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_id_invisible.setText(list.get(position).getId());


        holder.switch_confirmar.setChecked(false);
        if (list.get(position).getEstado().equals("Asistido")) holder.switch_confirmar.setChecked(true);

        holder.switch_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Modelo_invitacion cambioEstadoInvitacion= list.get(holder.getAdapterPosition());
                if (holder.switch_confirmar.isChecked()){
                    cambioEstadoInvitacion.setEstado("Asistido");
                }else{
                    cambioEstadoInvitacion.setEstado("Pendiente");
                }

                DatabaseReference CambiarEstado = FirebaseDatabase.getInstance().getReference();
                CambiarEstado.child("Invitaciones").child(list.get(holder.getAdapterPosition()).getId()).setValue(cambioEstadoInvitacion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_nombre, textView_id_invisible;
        private Switch switch_confirmar;

        LinearLayout linerarLayout_tarjeta;
       

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre = itemView.findViewById(R.id.textView_nombre);
            textView_id_invisible=itemView.findViewById(R.id.textView_invisible);
            switch_confirmar = itemView.findViewById(R.id.switch_confirmar);
            linerarLayout_tarjeta=itemView.findViewById(R.id.linerarLayout_tarjeta);

            linerarLayout_tarjeta.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("id_invitacion", textView_id_invisible.getText().toString().trim());
                Navigation.findNavController(view).navigate(R.id.mostrarInvitacion,bundle);

            });

        }
    }
}