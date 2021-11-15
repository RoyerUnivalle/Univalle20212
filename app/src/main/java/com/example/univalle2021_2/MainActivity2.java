package com.example.univalle2021_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

public class MainActivity2 extends AppCompatActivity {

    TextView mirecibo, tvContar;
    int contador;
    Button pintar;
    Pintar objPintar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //enlazamiento
        mirecibo = findViewById(R.id.tvRecibo);
        tvContar = findViewById(R.id.tvContador);
        pintar = findViewById(R.id.btnColorear);
        Bundle recibo = getIntent().getExtras(); // <<< -- depositanto los parametros que enviaron como parametors
        mirecibo.setText("Credenciales: "+ recibo.getString("name")+ " - "+ recibo.get("passwd"));
        contador = 0;
        // Poner fragmento de forma programatica
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BlankFragment2 fragment = new BlankFragment2();
        fragmentTransaction.add(R.id.frameFragment, fragment);
        fragmentTransaction.commit();
        // Connectivy manager
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        NetworkInfo nwInfo = cm.getNetworkInfo(cm.getActiveNetwork());
        if(nwInfo.getType() == ConnectivityManager.TYPE_WIFI){
            Toast.makeText(this, "I am Wifi", Toast.LENGTH_SHORT).show();
            isWifiConn = true;
        }else if (nwInfo.getType() == ConnectivityManager.TYPE_MOBILE){
            isWifiConn = true;
            Toast.makeText(this, "I am Mobile", Toast.LENGTH_SHORT).show();
        }
        // Connectivy manager
    }
    // ir de la ventana a A la ventana B
    public void navegarEntreVentanas(View f){
        Intent navegar = new Intent(this, MainActivity.class);
        //navegar.addFlags(navegar.FLAG_ACTIVITY_CLEAR_TOP | navegar.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(navegar);
        finish(); // saca de una
    }

    public void contar(View h){ // onSaveInstansState and onRestoreInstansState
        contador = contador + 1;
        mirecibo.setText("Contador: "+ contador);
    }

    ///////////////////////////////////////////// cambio de configuracion ////////////////////////
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        contador = savedInstanceState.getInt("contador");
    }
   // util tambien android:configChanges="screenSize|orientation"
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("contador",contador);
    }
    ///////////////////////////////////////////// cambio de configuracion ////////////////////////

    ///////////////////////////////////////////// menus ////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // getDatauser
        // if getDatauser.profile === 2
        // then inflater.inflate(R.menu.professorMenu, menu);
        //else
        // then inflater.inflate(R.menu.studentsMenu, menu);
        inflater.inflate(R.menu.menu_home, menu);  // <- ponga en la UI padre (activity_main2) el menu
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mCompras:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Vamos de compras")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();
                break;
            case R.id.mPedidos:
                Toast.makeText(this, "Hola toast", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Default", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    ///////////////////////////////////////////// menus ////////////////////////

    //////////////////// UI vs sub proceso principal
    public void pintar(View d){
        // Opcion 1 que bloquea la UI
        /*for (int i = 0; i < contador; i++) {
            System.out.println(aleatorio());
            pintar.setBackgroundColor(Color.rgb(aleatorio(),aleatorio(),aleatorio()));
            try {
                Thread.sleep(1000); //<-- el por defauult asume que es el hilo principal que es el UI Thread
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        // Opcion 2 por medio de AsyncTask
        //objPintar = new Pintar();
        ///objPintar.execute(contador);

        // Opcion 3  por medio de Runnable
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < contador; i++) {
                    System.out.println(aleatorio());
                    pintar.setBackgroundColor(Color.rgb(aleatorio(),aleatorio(),aleatorio()));
                    try {
                        Thread.sleep(1000); //<-- en este contexto hace referencia al hilo recien creado
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
    public int aleatorio(){
        return  (int) (Math.random()*254) + 1;
    }

    public class Pintar extends AsyncTask<Integer,Void,Void>{ // parametro,progeso, resultado
        @Override
        protected Void doInBackground(Integer... miContador) {
            for (int i = 0; i < miContador[0]; i++) {
                if(isCancelled()){
                    break;
                }else{
                    //para llamar al método   onProgressUpdate debemos hacerlo como siguie
                    publishProgress();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }
        // este método tiene contacto con la UI
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            System.out.println(aleatorio());
            pintar.setBackgroundColor(Color.rgb(aleatorio(),aleatorio(),aleatorio()));
        }

        @Override
        protected void onCancelled(Void unused) {
            super.onCancelled(unused);
            System.out.println("hiola cancelado");
            objPintar = null;
        }
    }
    //////////////////// UI vs sub proceso principal
}