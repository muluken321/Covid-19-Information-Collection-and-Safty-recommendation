package com.muler.covid19;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SpedDistanceService extends Service
{
    private LocationManager locManager;
    private LocationListener locListener = new myLocationListener();
    static final Double EARTH_RADIUS = 6371.00;

    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private int counternotic = 0;
    public  static int dst_counter = 0;
    private Handler handler = new Handler();
    Thread t;

    @Override
    public IBinder onBind(Intent intent) {return null;}
    @Override
    public void onCreate() {}
    @Override
    public void onDestroy() {}
    @Override
    public void onStart(Intent intent, int startid) {}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        //Toast.makeText(getBaseContext(), "Service Started", Toast.LENGTH_SHORT).show();

        final Runnable r = new Runnable()
        {   public void run()
        {
            Log.v("Debug", "Hello");
            location();
            handler.postDelayed(this, 5000);
        }
        };
        handler.postDelayed(r, 5000);
        return START_STICKY;
    }


    public void location(){
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex){}
        try{
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex){}
        Log.v("Debug", "in on create.. 2");
        if (gps_enabled) {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locListener);
            Log.v("Debug", "Enabled..");
        }
        if (network_enabled) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locListener);
            Log.v("Debug", "Disabled..");
        }
        Log.v("Debug", "in on create..3");
    }

    private class myLocationListener implements LocationListener
    {
        double lat_old=0.0;
        double lon_old=0.0;
        double lat_new;
        double lon_new;
        double time=10;
        double speed=0.0;
        int counter = 0;
        @Override
        public void onLocationChanged(Location location) {
            counter = counter+1;
            Log.v("Debug", "in onLocation changed..");
            if(location!=null){
                locManager.removeUpdates(locListener);
                //String Speed = "Device Speed: " +location.getSpeed();
                lat_new=location.getLongitude();
                lon_new =location.getLatitude();
                String longitude = "Longitude: " +location.getLongitude();
                String latitude = "Latitude: " +location.getLatitude();
                double distance =CalculationByDistance(lat_new, lon_new, lat_old, lon_old);
                speed = distance/time;

 /*
 Toast.makeText(getApplicationContext(), longitude+"\n"+latitude+"\nDistance is: "

   +distance+"\nSpeed is: "+speed , Toast.LENGTH_LONG).show();
*/
                if(distance>50) {
                    if(counternotic ==0) {
                        addNotification();
                        counternotic++;
                        dst_counter = dst_counter+1;

                    }
                }

                lat_old=lat_new;
                lon_old=lon_new;

            }

            //    setaddress(lat_new,lon_new);
        }

        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public double CalculationByDistance(double lat1, double lon1, double lat2, double lon2) {
        double Radius = EARTH_RADIUS;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;

    }

    public void setaddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(SpedDistanceService.this, Locale.getDefault());


        try {

            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String addline = obj.getAddressLine(0);
            String countryname =obj.getCountryName();
            String countrycode = obj.getCountryCode();
            String adminarea =obj.getAdminArea();
            String postalcode =obj.getPostalCode();
            String subadminarea =obj.getSubAdminArea();
            String locality =obj.getLocality();
            String sublocality =obj.getSubLocality();


//Toast.makeText(getBaseContext(),addline+"--"+countrycode+"--"+adminarea,Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // Toast.makeText(this, "ERROR!!! Please Check Your Network Connection and Return Back", Toast.LENGTH_LONG).show();
        }
    }
    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.appicon)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setLights(Color.RED, 3000, 3000)
                        .setContentTitle("Covid Alert")
                        .setContentText("እናክዎትን! በሚክቀሳቀሱበትጊዜ ማስክ ይጠቀሙ፡፡ ሌሎች ጥንቃቄዎችንም አይዘንጉ");

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