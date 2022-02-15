package com.muler.covid19;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class BackGroundMotion extends Service {
    int startMode;       // indicates how to behave if the service is killed
    IBinder binder;      // interface for clients that bind
    boolean allowRebind; // indicates whether onRebind should be used
    double magnitudeprevi = 0;
    int stepcount = 0;
    int count = 0;
    @Override
    public void onCreate() {
        // The service is being created
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager sensormanager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener stepdetecter = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorevent) {
                if(sensorevent !=null){
                    float x_acceleration = sensorevent.values[0];
                    float y_acceleration = sensorevent.values[1];
                    float z_acceleration = sensorevent.values[2];
                    double magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double magnitudedatta = magnitude - magnitudeprevi;
                    magnitudeprevi = magnitude;
                    if(magnitudedatta > 6){
                        stepcount++;
                    }

                    ////////////////////////////////////////////
                    if((stepcount==40)||(stepcount ==44)||(stepcount ==50)||(stepcount ==70)){
BluetoothDestance.PreCautionMove2 = BluetoothDestance.PreCautionMove2+1;
                        Toast.makeText(getApplicationContext(),"እባክዎትን በበየትዎ ይሁኑ!",Toast.LENGTH_LONG).show();
                        addNotification();

 // SpedDistanceService.Jump_move = SpedDistanceService.Jump_move+1;
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }


        };
        sensormanager.registerListener(stepdetecter, sensor, sensormanager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
       // return mAllowRebind;
        return true;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        //super.onRebind(Service.START_REDELIVER_INTENT);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // The service is no longer used and is being destroyed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle("Covid-19 Alert")
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setLights(Color.RED, 3000, 3000)
                        .setContentText("Please Keep Your Self Safe!");


        Intent notificationIntent = new Intent(this, TravelHistory.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {

        }
    }
}