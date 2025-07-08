package com.example.uthrestapi250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnRegistrar, btnVerLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVerLista = findViewById(R.id.btnVerLista);

        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        btnVerLista.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaPersonasActivity.class);
            startActivity(intent);
        });
    }
}
