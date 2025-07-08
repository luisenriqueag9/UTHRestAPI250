package com.example.uthrestapi250.Config;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uthrestapi250.R;

import java.util.List;

public class AdaptadorPersonas extends RecyclerView.Adapter<AdaptadorPersonas.ViewHolder> {

    private List<Personas> lista;//Guarda todas las personas a mostrar

    public AdaptadorPersonas(List<Personas> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_persona, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Personas p = lista.get(position);
        holder.txtNombre.setText(p.getNombres() + " " + p.getApellidos());
        holder.txtFecha.setText("Fecha: " + p.getFechanac());
        holder.txtTelefono.setText("Tel: " + p.getTelefono());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha, txtTelefono;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCompleto);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
        }
    }
}
