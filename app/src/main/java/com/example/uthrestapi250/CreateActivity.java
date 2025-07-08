package com.example.uthrestapi250;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.uthrestapi250.Config.Personas;
import com.example.uthrestapi250.Config.RestApiMethods;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE = 101;
    static final int ACCESS_CAMERA = 201;
    ImageView imageView;
    Button btnfoto, btncreate;
    String currentPhotoPath;
    EditText nombres, apellidos, fechanac, telefono, direccion;
    private RequestQueue requestQueue;
    Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);

        imageView = findViewById(R.id.imageView);
        btnfoto = findViewById(R.id.btntakefoto);
        btncreate = findViewById(R.id.btncreate);

        nombres = findViewById(R.id.nombres);
        apellidos = findViewById(R.id.apellidos);
        direccion = findViewById(R.id.direccion);
        fechanac = findViewById(R.id.fecha);
        telefono = findViewById(R.id.telefono);

        // Calendario
        fechanac.setOnClickListener(view -> {
            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        fechanac.setText(fechaSeleccionada);
                    },
                    año, mes, dia
            );
            datePickerDialog.show();
        });

        btnfoto.setOnClickListener(v -> PermisosCamara());

        btncreate.setOnClickListener(v -> SendData());

        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(CreateActivity.this)
                    .setTitle("Confirmación")
                    .setMessage("¿Deseas regresar al menú principal?")
                    .setPositiveButton("Sí", (dialog, which) -> finish())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    }

    private void SendData() {
        // Obtener valores y validar
        String nombreTxt = nombres.getText().toString().trim();
        String apellidoTxt = apellidos.getText().toString().trim();
        String direccionTxt = direccion.getText().toString().trim();
        String telefonoTxt = telefono.getText().toString().trim();
        String fechaTxt = fechanac.getText().toString().trim();

        if (nombreTxt.isEmpty() || apellidoTxt.isEmpty() || direccionTxt.isEmpty() ||
                telefonoTxt.isEmpty() || fechaTxt.isEmpty() || currentPhotoPath == null) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
            return;
        }

        // Validar teléfono
        if (!telefonoTxt.matches("\\d{8,}")) {
            Toast.makeText(this, "El teléfono debe tener al menos 8 dígitos", Toast.LENGTH_LONG).show();
            return;
        }

        // Enviar datos
        requestQueue = Volley.newRequestQueue(this);
        Personas personas = new Personas();

        personas.setNombres(nombreTxt);
        personas.setApellidos(apellidoTxt);
        personas.setDireccion(direccionTxt);
        personas.setFechanac(fechaTxt);
        personas.setTelefono(telefonoTxt);
        personas.setFoto(ConvertImageBase64(currentPhotoPath));

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("nombres", personas.getNombres());
            jsonObject.put("apellidos", personas.getApellidos());
            jsonObject.put("direccion", personas.getDireccion());
            jsonObject.put("telefono", personas.getTelefono());
            jsonObject.put("fechanac", personas.getFechanac());
            jsonObject.put("foto", personas.getFoto());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, RestApiMethods.EndpointCreatePerson,
                    jsonObject,
                    response -> {
                        try {
                            String mensaje = response.getString("message");
                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String body = new String(error.networkResponse.data);
                            Toast.makeText(getApplicationContext(), "Error: " + body, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error de red: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

            requestQueue.add(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String ConvertImageBase64(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageArray, Base64.DEFAULT);
    }

    private void PermisosCamara() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, ACCESS_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getApplicationContext(), "Se necesita permiso de la cámara", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.uthrestapi250.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            try {
                File Foto = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(Foto));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
