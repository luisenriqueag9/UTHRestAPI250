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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.uthrestapi250.CreateActivity;
import com.example.uthrestapi250.R;
import org.json.JSONException;
import org.json.JSONObject;


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

        if (p.getFoto() != null && !p.getFoto().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(p.getFoto()).into(holder.imgPersona);
        } else {
            holder.imgPersona.setImageResource(R.drawable.ic_person);
        }

        // Clic largo para mostrar opciones
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
                .setMessage("¿Qué deseas hacer con " + persona.getNombres() + "?")
                .setPositiveButton("Editar", (dialog, which) -> {
                    Intent intent = new Intent(context, CreateActivity.class);
                    intent.putExtra("persona", persona); // Persona debe implementar Serializable
                    context.startActivity(intent);
                })
                .setNegativeButton("Eliminar", (dialog, which) -> {
                    eliminarPersona(context, persona.getId());
                })
                .setNeutralButton("Cancelar", null)
                .show();
    }

    private void eliminarPersona(Context context, int idPersona) {
        String url = "http://10.0.2.2/CRUD-PHP/DeletePersons.php";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", idPersona);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        CustomJsonRequest request = new CustomJsonRequest(
                Request.Method.POST,
                url,
                jsonBody,
                null,
                response -> {
                    Toast.makeText(context, "Persona eliminada", Toast.LENGTH_SHORT).show();

                    // Eliminar de la lista y actualizar RecyclerView
                    int position = -1;
                    for (int i = 0; i < lista.size(); i++) {
                        if (lista.get(i).getId() == idPersona) {
                            position = i;
                            break;
                        }
                    }

                    if (position != -1) {
                        lista.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, lista.size());
                    }
                },
                error -> {
                    String msg = error.toString();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        msg = new String(error.networkResponse.data);
                    }
                    Toast.makeText(context, "Error al eliminar:\n" + msg, Toast.LENGTH_LONG).show();
                }
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
