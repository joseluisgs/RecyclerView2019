package com.example.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;




/*
Adaptador de la lista
 */

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

    // Array list que le pasamos
    private ArrayList<MyListData> listdata;
    //private Context mContext;

    // RecyclerView recyclerView; ---
    // Cponstructor
    public MyListAdapter(ArrayList<MyListData> listdata) {

        this.listdata = listdata;
        //this.mContext = context;
    }


    @Override
    // Le pasamos el ViewHolder y el layout que va a usar
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override

    // Publicamos el evento en la posición del holder y lo programamos
    // Ver el ejemplo de la lista
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata.get(position);
        holder.textView.setText(listdata.get(position).getDescription());
        holder.imageView.setImageResource(listdata.get(position).getImgId());

        // Cargamos los eventos de los componentes que quedamos
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }


    // Para insertar sobre la marcha
    public void addItem(MyListData item) {
        listdata.add(item);
        notifyItemInserted(listdata.size());

    }

    // Para borrar
    public void removeItem(int position) {
        listdata.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listdata.size());


    }

    public void restoreItem(MyListData item, int position) {
        //listdata.set(position, item);
        listdata.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, listdata.size());
    }

    public ArrayList<MyListData> getList(){
        return this.listdata;
    }

    // Aqui está el holder y lo que va a manejar, es decir la vista para interactuar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // componentes que vamos a manejar
        public ImageView imageView;
        public TextView textView;

        // Layout de la fila
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.txtNombre);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}