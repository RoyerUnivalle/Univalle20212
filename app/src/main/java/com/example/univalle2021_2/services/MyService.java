package com.example.univalle2021_2.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {

    //boolean flag = true;
    ShowDate obj;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // checkConnetion();
        obj = new ShowDate();
        obj.execute();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void checkConnetion()  {
        // Opcion 3  por medio de Runnable
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    // Connectivy manager
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    boolean isWifiConn = false;
                    boolean isMobileConn = false;
                    Date date = new Date();
                    SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    NetworkInfo nwInfo = cm.getNetworkInfo(cm.getActiveNetwork());
                    // runOnUiThread <<--posible solucion para desde qui acceder a la UI
                    if(nwInfo.getType() == ConnectivityManager.TYPE_WIFI){
                        //Toast.makeText(getApplicationContext(), "I am Wifi", Toast.LENGTH_SHORT).show();
                        Log.d("", i+" I am Wifi "+ formater.format(date));
                        isWifiConn = true;
                    }else if (nwInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                        isWifiConn = true;
                        //Toast.makeText(this, "I am Mobile", Toast.LENGTH_SHORT).show();
                        Log.d("", i+" I am Mobile");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        /*while (flag){
            // if x = 1
            // thne flag = false
        }*/
    }

    public class ShowDate extends AsyncTask<Void,String,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < 10; i++) {
                // Connectivy manager
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean isWifiConn = false;
                boolean isMobileConn = false;
                Date date = new Date();
                SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                NetworkInfo nwInfo = cm.getNetworkInfo(cm.getActiveNetwork());
                if(nwInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    //Toast.makeText(getApplicationContext(), "I am Wifi", Toast.LENGTH_SHORT).show();
                    Log.d("", i+" I am Wifi "+ formater.format(date));
                    publishProgress(i+" I am Wifi "+ formater.format(date));
                    isWifiConn = true;
                }else if (nwInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                    isWifiConn = true;
                    //Toast.makeText(this, "I am Mobile", Toast.LENGTH_SHORT).show();
                    Log.d("", i+" I am Mobile");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();
        }
    }
}