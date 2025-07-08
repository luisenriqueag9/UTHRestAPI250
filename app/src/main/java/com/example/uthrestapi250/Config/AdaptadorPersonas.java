package com.example.uthrestapi250.Config;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.uthrestapi250.CreateActivity;
import com.example.uthrestapi250.R;

import java.util.List;

public class AdaptadorPersonas extends RecyclerView.Adapter<AdaptadorPersonas.ViewHolder> {

    private List<Personas> lista;

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
        holder.txtFecha.setText("Dirección: " + p.getDireccion());
        holder.txtTelefono.setText("Tel: " + p.getTelefono());

        // Cargar foto si existe
        if (p.getFoto() != null && !p.getFoto().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(p.getFoto()).into(holder.imgPersona);
        } else {
            holder.imgPersona.setImageResource(R.drawable.ic_person);
        }

        // Al mantener presionado, mostrar opciones
        holder.itemView.setOnLongClickListener(v -> {
            showOptionsDialog(holder.itemView.getContext(), p);
            return true;
        });
    }



    @Override
    public int getItemCount() {
        return lista.size();
    }

    private void showOptionsDialog(Context context, Personas persona) {
        new AlertDialog.Builder(context)
                .setTitle("Opciones")
                .setMessage("¿Qué desea hacer con " + persona.getNombres() + "?")
                .setPositiveButton("Editar", (dialog, which) -> {
                    // Ir a pantalla de edición
                    Intent intent = new Intent(context, CreateActivity.class); // Reutilizamos CreateActivity
                    intent.putExtra("persona", persona); // Pasamos la persona (debes implementar Serializable)
                    context.startActivity(intent);
                })
                .setNegativeButton("Eliminar", (dialog, which) -> {
                    // Llamar API de eliminación (puede ser método en ListaPersonasActivity o aquí mismo)
                    eliminarPersona(context, persona.getId());
                })
                .setNeutralButton("Cancelar", null)
                .show();
    }

    private void eliminarPersona(Context context, int idPersona) {
        String url = "http://10.0.2.2/CRUD-PHP/DeletePersons.php?id=" + idPersona;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Toast.makeText(context, "Persona eliminada", Toast.LENGTH_SHORT).show();
                    // Recargar o notificar a la actividad para actualizar lista
                },
                error -> Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(context).add(request);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha, txtTelefono;
        ImageView imgPersona;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCompleto);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
            imgPersona = itemView.findViewById(R.id.imgPersona);
        }
    }
}
