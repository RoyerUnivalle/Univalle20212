package com.example.univalle2021_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.univalle2021_2.Connection.Connection;
import com.example.univalle2021_2.services.MyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.Inflater;

public class MainActivity2 extends AppCompatActivity {

    TextView mirecibo, tvContar;
    int contador;
    Button pintar;
    Pintar objPintar;
    SQLiteDatabase db;
    Connection conn;

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
        /*try {
            getDataHttp();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        getDataVolley();
        iniciarServicio();
        conn = new Connection(this,"univalle",null,1);
        if (conn != null){
            Toast.makeText(this, "The database has been created", Toast.LENGTH_SHORT).show();
        }
        db = conn.getWritableDatabase();
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
    /// Consumo por HTTP por volley
    public void getDataVolley(){
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://run.mocky.io/v3/d0dc703a-1a0c-49b1-9146-f7ba5b92088c";
        // Request a string response from the provided URL.
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray estudiantes = response.getJSONArray("estudiantes");
                            int cantidadEstudiantes = estudiantes.length();
                            for (int i = 0; i < cantidadEstudiantes; i++) {
                                JSONObject estudiante = estudiantes.getJSONObject(i);
                                Log.d("", (i+1)+": Nombre: "+estudiante.getString("nombre")+" "+estudiante.getString("apellido"));
                                db.execSQL("INSERT into students (id,name) values (" + (i+1)+",'"+estudiante.getString("nombre")+"');");
                                JSONArray materias = estudiante.getJSONArray("materias");
                                for (int j = 0; j < materias.length(); j++) {
                                    JSONObject materia = materias.getJSONObject(j);
                                    Log.d("", (j+1)+": Materia: "+materia.getString("name"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
                System.out.println("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(objectRequest);
    }
    /// Consumo por HTTP por HttpUrlConnection

    public  void getDataHttp() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("https://run.mocky.io/v3/d0dc703a-1a0c-49b1-9146-f7ba5b92088c");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Log.d("", "code: "+connection.getResponseCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        // connection.setRequestProperty("Accept", "application/json");
                        try {
                            InputStream in = new BufferedInputStream(connection.getInputStream());
                            Log.d("", "Respuesta: "+in);
                        } finally {
                            connection.disconnect();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void iniciarServicio(){
        Intent servicio = new Intent(this, MyService.class);
        startService(servicio);
    }
}