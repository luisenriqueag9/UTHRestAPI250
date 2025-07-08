package com.example.uthrestapi250;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.uthrestapi250.Config.AdaptadorPersonas;
import com.example.uthrestapi250.Config.Personas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListaPersonasActivity extends AppCompatActivity {

    RecyclerView listaPersonas;
    ArrayList<Personas> lista = new ArrayList<>();
    AdaptadorPersonas adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_personas);

        listaPersonas = findViewById(R.id.listaPersonas);
        listaPersonas.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorPersonas(lista);
        listaPersonas.setAdapter(adaptador);

        Button btnAtras = findViewById(R.id.btnAtras);

        btnAtras.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(ListaPersonasActivity.this)
                    .setTitle("Confirmación")
                    .setMessage("¿Deseas regresar al menú principal?")
                    .setPositiveButton("Sí", (dialog, which) -> finish())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });



        obtenerPersonas();
    }

    private void obtenerPersonas() {
        String url = "http://10.0.2.2/CRUD-PHP/GetPersons.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        lista.clear(); // Limpia para evitar duplicados
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Personas p = new Personas();

                            p.setId(obj.getInt("id"));
                            p.setNombres(obj.getString("nombres"));
                            p.setApellidos(obj.getString("apellidos"));
                            p.setDireccion(obj.getString("direccion"));
                            p.setTelefono(obj.getString("telefono"));
                            p.setFoto(obj.getString("foto"));

                            lista.add(p);
                        }

                        adaptador.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al leer datos", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
