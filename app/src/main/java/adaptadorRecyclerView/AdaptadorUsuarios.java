package adaptadorRecyclerView;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventos.R;
import com.example.eventos.modelos.Modelo_usuario;

import java.util.ArrayList;

//https://www.youtube.com/watch?v=M8sKwoVjqU0&ab_channel=Foxandroid firebase con recyclerview
public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.MyViewHolder> {
    Context context;
    ArrayList<Modelo_usuario> list;

    public AdaptadorUsuarios(ArrayList<Modelo_usuario> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView_nombre.setText(list.get(position).getNombre());
        holder.textView_id_invisible.setText(list.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_nombre, textView_id_invisible;

        CardView cardView_usuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_nombre = (TextView) itemView.findViewById(R.id.textView_nombre);
            textView_id_invisible=(TextView)itemView.findViewById(R.id.textView_id);
            cardView_usuario= (CardView) itemView.findViewById(R.id.carView_usuario);

            cardView_usuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString("id_usuario", textView_id_invisible.getText().toString().trim());
                    Navigation.findNavController(view).navigate(R.id.usuarios,bundle);

                }
            });
        }
    }
}