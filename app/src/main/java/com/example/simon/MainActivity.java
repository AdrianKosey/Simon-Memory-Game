package com.example.simon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button[] misBotones;
    private int[] sonidos;
    private GridLayout contenedorBotones;
    private Button btn_start;
    private List<Integer> secuenciaSimon = new ArrayList<>();
    private List<Integer> secuenciaUsuario = new ArrayList<>();
    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());
    private int turnoUsuario = 0;
    private boolean jugando = false;
    private TextView txt_nivel, txt_turno;
    private int NUMERO_DE_BOTONES = 16;
    private TextView txt_level_max;
    private int maxLevel = 1;

    SharedPreferences archivo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(findViewById(R.id.Toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        archivo = this.getSharedPreferences("record", Context.MODE_PRIVATE);
        if(archivo.contains("record_level")){
            maxLevel = archivo.getInt("record_level", 1);
            Log.d("SharedPref", "Record cargado: " + maxLevel);
        }

        btn_start = findViewById(R.id.button_start);
        txt_nivel = findViewById(R.id.level_info);
        txt_turno = findViewById(R.id.turno_info);
        txt_level_max = findViewById(R.id.level_max);
        contenedorBotones = findViewById(R.id.grid_layout_simon);
        Bundle extras = getIntent().getExtras();
        if(extras != null) NUMERO_DE_BOTONES = extras.getInt("cantidadBotones");
        if(NUMERO_DE_BOTONES == 0) NUMERO_DE_BOTONES = 16;
        misBotones = new Button[NUMERO_DE_BOTONES];
        sonidos = new int[NUMERO_DE_BOTONES];
        final TypedArray colors = getResources().obtainTypedArray(R.array.button_colors);
        txt_level_max.setText("Record: " + maxLevel);
        // Calcular cantidad de columnas y filas del grid
        int columnas = (int) Math.ceil(Math.sqrt(NUMERO_DE_BOTONES));
        int filas = (int) Math.ceil((double) NUMERO_DE_BOTONES / columnas);
        contenedorBotones.setColumnCount(columnas);
        contenedorBotones.setRowCount(filas);

        // Ciclo para agregar cada boton
        for (int i = 0; i < NUMERO_DE_BOTONES; i++) {
            misBotones[i] = new Button(this);
            misBotones[i].setId(View.generateViewId());
            misBotones[i].setTag(String.valueOf(i));
            int buttonColor = colors.getColor(i, 0);
            misBotones[i].setBackgroundColor(buttonColor);
            sonidos[i] = getResources().getIdentifier("blipselect_" + (i + 1), "raw", getPackageName());
            // Posicion de cada boton
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = 0;
            params.width = 0;
            params.setMargins(8, 8, 8, 8);
            params.setGravity(Gravity.FILL);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // layout_columnWeight="1"
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            contenedorBotones.addView(misBotones[i], params);
        }
        colors.recycle();
        btn_start.setEnabled(true);
        btn_start.setOnClickListener(v -> startGame());
    }

    private void startGame() {
        btn_start.setEnabled(false);
        secuenciaSimon.clear();
        secuenciaUsuario.clear();
        turnoUsuario = 0;
        jugando = true;

        agregarNuevoBotonASecuencia();
        mostrarSecuencia();
    }

    private void reproducirSonido(int index) {
        if (index < 0 || index >= sonidos.length || sonidos[index] == 0) {
            Log.e("Simon", "Recurso de sonido no encontrado en el índice: " + index);
            return;
        }
        MediaPlayer mp = MediaPlayer.create(this, sonidos[index]);
        if (mp != null) {
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();
        } else {
            Log.e("Simon", "MediaPlayer no pudo crearse para el sonido: " + sonidos[index]);
        }
    }

    private void mostrarSecuencia() {
        btn_start.setEnabled(false);
        for(int i = 0; i < misBotones.length; i++) misBotones[i].setOnClickListener(null);
        txt_turno.setText("Mostrando Secuencia");
        handler.postDelayed(new Runnable() {
            int i = 0;
            @Override
            public void run() {
                if (i < secuenciaSimon.size()) {
                    int botonIndex = secuenciaSimon.get(i);
                    animarBoton(misBotones[botonIndex], botonIndex);
                    i++;
                    handler.postDelayed(this, 800); // 800ms entre botones
                } else {
                    // Habilitar al usuario para que ingrese la secuencia
                    txt_turno.setText("Tú Turno");
                    turnoUsuario = 0;
                    secuenciaUsuario.clear();
                    habilitarBotonesUsuario();
                }
            }
        }, 1000);
    }
    private void animarBoton(Button boton, int index) {
        int colorOriginal = ((ColorDrawable) boton.getBackground()).getColor();
        boton.setBackgroundColor(Color.WHITE); // resaltamos
        reproducirSonido(index);
        handler.postDelayed(() -> boton.setBackgroundColor(colorOriginal), 400);
    }
    private void habilitarBotonesUsuario() {
        for (int i = 0; i < misBotones.length; i++) {
            final int index = i;
            misBotones[i].setOnClickListener(v -> {
                if (!jugando) return;
                secuenciaUsuario.add(index);
                animarBoton(misBotones[index], index);
                verificarTurnoUsuario(index);
            });
        }
    }

    private void verificarTurnoUsuario(int botonPulsado) {
        if (botonPulsado != secuenciaSimon.get(turnoUsuario)) {
            // Usuario fallo
            Toast.makeText(this, "¡Perdiste!", Toast.LENGTH_SHORT).show();
            btn_start.setEnabled(true);
            txt_nivel.setText("Nivel: 1");
            txt_turno.setText("");
            jugando = false;
        } else {
            turnoUsuario++;
            if (turnoUsuario == secuenciaSimon.size()) {
                // Secuencia completa correcta, agregamos otro boton
                Toast.makeText(this, "¡Bien!", Toast.LENGTH_SHORT).show();
                txt_nivel.setText("Nivel: " + (secuenciaSimon.size() + 1));
                if(maxLevel < secuenciaSimon.size() + 1){
                    txt_level_max.setText("Record: " + (secuenciaSimon.size() + 1));
                    maxLevel = secuenciaSimon.size() + 1;
                    SharedPreferences.Editor editor = archivo.edit();
                    editor.putInt("record_level", secuenciaSimon.size() + 1);
                    editor.apply();
                }
                agregarNuevoBotonASecuencia();
                mostrarSecuencia();
            }
        }
    }
    private void agregarNuevoBotonASecuencia() {
        int nuevoBoton = random.nextInt(misBotones.length);
        secuenciaSimon.add(nuevoBoton);
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
            Toast toast = Toast.makeText(this /* MyActivity */, "Ya se encuentra en 'Principal'.", Toast.LENGTH_SHORT);
            toast.show();
        }else if(item.getItemId()==R.id.otro){
            Intent config = new Intent(this, configuracion.class);
            config.putExtra("cantidadBotones", NUMERO_DE_BOTONES);
            startActivity(config);
        }
        return super.onOptionsItemSelected(item);
    }
}