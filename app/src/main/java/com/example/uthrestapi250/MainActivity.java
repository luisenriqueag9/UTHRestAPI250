package com.example.uthrestapi250;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.uthrestapi250.Config.AdaptadorPersonas;
import com.example.uthrestapi250.Config.Personas;
import com.example.uthrestapi250.Config.RestApiMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Personas> listaPersonas = new ArrayList<>();
    AdaptadorPersonas adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerPersonas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        obtenerPersonas();
    }

    private void obtenerPersonas() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                RestApiMethods.EndpointGetPersons,
                null,
                response -> {
                    try {
                        listaPersonas.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Personas p = new Personas();
                            p.setId(obj.getString("id"));
                            p.setNombres(obj.getString("nombres"));
                            p.setApellidos(obj.getString("apellidos"));
                            p.setDireccion(obj.getString("direccion"));
                            p.setTelefono(obj.getString("telefono"));
                            p.setFechanac(obj.getString("fechanac"));
                            p.setFoto(obj.getString("foto"));

                            listaPersonas.add(p);
                        }

                        adaptador = new AdaptadorPersonas(listaPersonas);
                        recyclerView.setAdapter(adaptador);

                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                        Log.e("VOLLEY", "Error JSON: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                    Log.e("VOLLEY", "Error: " + error.toString());
                });

        Volley.newRequestQueue(this).add(request);
    }
}
