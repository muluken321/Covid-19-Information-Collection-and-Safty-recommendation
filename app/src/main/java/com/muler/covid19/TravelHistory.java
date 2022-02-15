package com.muler.covid19;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.muler.covid19.careFragment.CareFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TravelHistory extends AppCompatActivity implements LocationListener {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String provider;
    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;
    protected Context context;
    ProgressBar progressBartrv1,progressBartrv2;
    TextView txtTravel;
    ArrayList<HashMap<String, String>> Datalist;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        progressBartrv1 =findViewById(R.id.progressBartrv1);
        progressBartrv2 =findViewById(R.id.progressBartrv2);
        txtTravel = findViewById(R.id.txtTravel);

        progressBartrv2.setVisibility(View.VISIBLE);
        progressBartrv1.setVisibility(View.VISIBLE);


        SharedPreferences sharedpreferencesAllT = getSharedPreferences("LocalAllTraveled", Context.MODE_PRIVATE);
        String My_local_trav = sharedpreferencesAllT.getString("sharedpreferencesAllT","");
        String imei = CareFragment.sharedpreferences.getString("myid","");
        txtTravel.setText(My_local_trav);

        for(int i=1;i<5;i++) {
            getTravelHistory("http://dmuit.abyssiniasoft.com/my_travel_all.php?imei="+imei);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex){}
        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex){}
        if(gps_enabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        if(network_enabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //  txtLat = (TextView) findViewById(R.id.textVieww);
        //txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
//Toast.makeText(getApplicationContext(),"\"Latitude:\" + location.getLatitude() + \", Longitude:\" + location.getLongitude()",
        //Toast.LENGTH_LONG).show();
        getAddress(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onProviderEnabled(String provider) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }
    @Override
    public void onProviderDisabled(String provider) {

    }
    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(TravelHistory.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" +"Country: "+ obj.getCountryName();
            add = add + "\n" + "Admin Area: "+obj.getAdminArea();
            add = add + "\n" +"Postal Code: "+ obj.getPostalCode();
            add = add + "\n" +"Sub Admin Area: "+ obj.getSubAdminArea();
            add = add + "\n" + "Locality: "+obj.getLocality();
            add = add + "\n" +"Sub Locality: "+ obj.getSubLocality();

            TextView txtTravletoday = findViewById(R.id.textmessage);
            txtTravletoday.setText("አሁን የሚገኙበት ቦታ: "+"\n"+add);
            SharedPreferences sharedpreferencesc = getSharedPreferences(CareFragment.MyPREFERENCESdb, Context.MODE_PRIVATE);
            String myimei = sharedpreferencesc.getString("myid", "");


            progressBartrv1.setVisibility(View.GONE);
        } catch (IOException e) {
            progressBartrv1.setVisibility(View.GONE);
            Toast.makeText(this, "ERROR!!! Unable to load your current location", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(TravelHistory.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /****
     * retrive travel history
     */
    private void getTravelHistory(final String urlWebService) {

        class GetallHistory extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    JSONObject reader = new JSONObject(s);

                        JSONObject user_travel = reader.getJSONObject("user_travel");
                        String date = user_travel.getString("date");
                        String country = user_travel.getString("country");
                        String address_line = user_travel.getString("address_line");
                        String admin_area = user_travel.getString("admin_area");
                        String postal_code = user_travel.getString("postal_code");
                        String sub_admin_area = user_travel.getString("sub_admin_area");
                        String locality = user_travel.getString("locality");
                        String sub_locality = user_travel.getString("sub_locality");
                        String My_sT = "\n"+"በ ቀን " + date + " የተጓዙበት ቦታ :" + "\n" +
                                "Country: " + country + "\n" +
                                "Street Name: " + address_line + "\n" +
                                "Admin Area: " + admin_area + "\n" +
                                "Postal Code:" + postal_code + "\n" +
                                "Sub Admin Area:" + sub_admin_area + "\n" +
                                "Locality:" + locality + "\n" +
                                "Sub Locality:" + sub_locality+"\n"+"\n";

                        txtTravel.setText(txtTravel.getText().toString()+"\n"+My_sT);
                        progressBartrv2.setVisibility(View.GONE);
                    SharedPreferences sharedpreferencesAllT = getSharedPreferences("LocalAllTraveled", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferencesAllT.edit();
                    editor.remove("sharedpreferencesAllT");
                    editor.putString("sharedpreferencesAllT", txtTravel.getText().toString());
                    editor.commit();

                }

                catch (Exception ex){
                    progressBartrv2.setVisibility(View.GONE);
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                } }}
        GetallHistory getallHistory = new GetallHistory();
        getallHistory.execute();
    }
    private void insertDtaT(final String urlWebServicee) {


        class InsertJsData extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject reader = new JSONObject(s);
                    JSONObject user_travel  = reader.getJSONObject("responsereg");
                    String status = user_travel.getString("status");
                    if(status.equals("true")){

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"There was some error please try again",Toast.LENGTH_LONG).show();
                    }

                }
                catch (Exception ex){
                    //Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebServicee);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        InsertJsData insertJsData = new InsertJsData();
        insertJsData.execute();
    }

}
