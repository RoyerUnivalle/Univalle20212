package com.example.univalle2021_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnDelegado, btnInterface; // Dos instancias de un mismo mismo tipo de objetos
    EditText name,passwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // R es una matriz de recursos (folder res)
        // R.id
        // R.layout
        setContentView(R.layout.activity_main); // <--- ordenar el renderizado
        Toast.makeText(this, "Hola Oncreate", Toast.LENGTH_SHORT).show();
        // ENLAZAMIENTO
        btnDelegado  = findViewById(R.id.btnDelegado);
        btnInterface = findViewById(R.id.btnInterfaz);
        name = findViewById(R.id.etName);
        passwd = findViewById(R.id.etPasswd);
        // cambiar propiedades
        btnDelegado.setText("Hi Delegate");
        btnInterface.setText("Hi Interface");
        // Eventos
        btnInterface.setOnClickListener(this);
        btnDelegado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navegarEntreVentanas(view);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Hola onStart", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "Hola onStop", Toast.LENGTH_SHORT).show();
    }
    // ir de la ventana a A la ventana B
    public void navegarEntreVentanas(View f){
        Intent navegar = new Intent(this, MainActivity2.class);
        // navegar.addFlags(navegar.FLAG_ACTIVITY_CLEAR_TOP | navegar.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle entrega = new Bundle();
        // 1. Validar que el usuario haya tipieado el name and el password. Hint: macht | machtes
        entrega.putString("name", name.getText().toString());
        entrega.putString("passwd", passwd.getText().toString());
        navegar.putExtras(entrega);
        startActivity(navegar);
        finish(); // saca de una
    }

    @Override
    public void onClick(View view) {
        navegarEntreVentanas(view);
    }
}