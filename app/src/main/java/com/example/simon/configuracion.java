package com.example.simon;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class configuracion extends AppCompatActivity {
    private Button btn_aceptar;
    private EditText edt_numeroBtn;
    private int cantBtn = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(findViewById(R.id.Toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cantBtn = extras.getInt("cantidadBotones");
        }

        btn_aceptar = findViewById(R.id.btn_aceptar);
        edt_numeroBtn = findViewById(R.id.botones_numero);

        edt_numeroBtn.setText(String.valueOf(cantBtn));

        btn_aceptar.setOnClickListener(v -> {
            String cantidadBotones = edt_numeroBtn.getText().toString();
            cantBtn = Integer.parseInt(cantidadBotones);
            if(cantBtn > 16) cantBtn = 16;
            if(cantBtn < 2) cantBtn = 2;
            Intent button = new Intent(this, MainActivity.class);
            button.putExtra("cantidadBotones", cantBtn);
            startActivity(button);
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.main){
            Intent main = new Intent(this, MainActivity.class);
            main.putExtra("cantidadBotones", cantBtn);
            startActivity(main);
        }else if(item.getItemId()==R.id.otro){
            Toast toast = Toast.makeText(this /* MyActivity */, "Ya se encuentra en 'Configuración'.", Toast.LENGTH_SHORT);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }

}