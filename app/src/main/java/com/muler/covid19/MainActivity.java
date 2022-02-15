package com.muler.covid19;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.muler.covid19.careFragment.CareFragment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements LocationListener {
    protected LocationManager locationManager;
    protected Context context;
    int count = 0;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String provider;
    DatabaseHelper dbhelper;
    public static String my_current_loc =null;
    protected boolean gps_enabled,network_enabled;

    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCESdb = "MY_L_DB" ;
    public String reqHandlerStreet = "";
    RequestQueue requestQueue;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLocationPermission();
        isStoragePermissionGranted();
        dbhelper = new DatabaseHelper(this);
        NukeSSLCerts nukeSSLCerts = new NukeSSLCerts();
        nukeSSLCerts.nuke();
        requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
        //requestQueue = Volley.newRequestQueue(getApplicationContext());


        sharedpreferences = getSharedPreferences(MyPREFERENCESdb, Context.MODE_PRIVATE);
        /*
        TURNING ON THE BLUETOOTH
         */
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            Intent dIntent =  new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(dIntent);
        }

        ///////////////////////////////
        startService(new Intent(this, BackGroundMotion.class));
        startService(new Intent(this, BluetoothDestance.class));
        Intent myIntent=new Intent(this,SpedDistanceService.class);
        startService(myIntent);


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        BottomNavigationView navView = findViewById(R.id.nav_view);

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


        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }

/////////////////////////////////////////////////////
            void showPopup(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon",boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.getMenuInflater().inflate(R.menu.morec, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.m44:
                                Intent i = new Intent(MainActivity.this, com.muler.covid19.Location.class);
                                startActivity(i);
                                finish();
                                return true;

                            case R.id.m33:
                              Intent iii = new Intent(MainActivity.this, TravelHistory.class);
                              startActivity(iii);
                              finish();
                                return true;

                            case R.id.m22:
                                try {
                                    final Dialog preq = new Dialog(MainActivity.this);
                                    preq.setContentView(R.layout.preq);
                                    //  preq.setTitle("");
                                    preq.setCancelable(true);
                                    TextView preqedt = preq.findViewById(R.id.preq);
                                    if((BluetoothDestance.PreCautionMove2<=0)&&(BluetoothDestance.PreCautionMove<=0)){
                                        preqedt.setText("የብሉቱዝ ማስጠንቀቂያዎች (0)"+"\n\n"+" የእንቅስቃሴ ማስጠንቀቂያዎች (0)"+"\n\n");
                                    }
                                    else{
                                    if(BluetoothDestance.PreCautionMove>0){
                                        preqedt.setText(BluetoothDestance.PreCautionMove + " ጠቅላላ የብሉቱዝ ማስጠንቀቂያዎች"+"\n\n"+"እባክዎትን ሁልጊዜ ጥንቃቄ ያድርጉ"+"\n\n");
                                    }
if(BluetoothDestance.PreCautionMove2>0){
    preqedt.setText(BluetoothDestance.PreCautionMove + " ጠቅላላ የእንቅስቃሴ ማስጠንቀቂያዎች"+"\n\n"+"እባክዎትን ሁልጊዜ ጥንቃቄ ያድርጉ"+"\n\n");

}}
                                    preq.show();
                                }
                                catch(Exception ex){
                                    Toast.makeText(getApplicationContext(),""+ex.getMessage().toString(),Toast.LENGTH_LONG).show();
                                }
                                return true;
                        }

                        //Toast.makeText(getApplicationContext(), "You Clicked : " + item.getTitle(),  Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();
            }

            ////////////////////////////////////////////////////////////////////////////
        });

        /////////////////////////////////////////////////////
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    /********
     * events which call the background services ---- banckmarkone
     */
    @Override
    protected void onStop() {
        bgRecorder();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                EventAction();
            }
        }, TimeUnit.MINUTES.toSeconds(0),TimeUnit.MINUTES.toSeconds(30));

        super.onStop();
    }
    @Override
    protected  void onDestroy() {

        bgRecorder();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                EventAction();
            }
        }, TimeUnit.MINUTES.toSeconds(0),TimeUnit.MINUTES.toSeconds(30));

        super.onDestroy();
    }
    @Override
    protected  void onStart() {
        bgRecorder();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                EventAction();
            }
        }, TimeUnit.MINUTES.toSeconds(0),TimeUnit.MINUTES.toSeconds(30));

        super.onStart();
    }
    @Override
    protected  void onResume() {
        bgRecorder();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                EventAction();
            }
        }, TimeUnit.MINUTES.toSeconds(0),TimeUnit.MINUTES.toSeconds(30));

        super.onResume();
    }
    @Override
    protected  void onRestart() {
        bgRecorder();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                EventAction();
            }
        }, TimeUnit.MINUTES.toSeconds(0),TimeUnit.MINUTES.toSeconds(30));

        super.onRestart();



    }
    /////////////////////////////////////////////////////////////////////
    /*
     */
    private void EventAction(){




        startService(new Intent(this, BackGroundMotion.class));
        startService(new Intent(this, BluetoothDestance.class));
        Intent myIntent = new Intent(this, SpedDistanceService.class);
        startService(myIntent);
    }
    @Override
    public void onLocationChanged(Location location) {
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
        Log.d("Latitude","disable");
    }
    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            final Address obj = addresses.get(0);
            String add = "Street Name :"+obj.getAddressLine(0);
            add = add + "\n" +"Country Name :"+ obj.getCountryName();
            add = add + "\n" +"Country Code :"+ obj.getCountryCode();
            add = add + "\n" + "Admin Area :"+obj.getAdminArea();
            add = add + "\n" +"Postal Code :"+ obj.getPostalCode();
            add = add + "\n" + "Sub-Admin Area :"+obj.getSubAdminArea();
            add = add + "\n" + "Locality :"+obj.getLocality();
            add = add + "\n" + "Sub Locality :"+obj.getSubLocality();

            Log.v("IGA", "Address" + add);
            my_current_loc = add;


            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("MY_loc_data", add);
            editor.putString("country", obj.getCountryName());
            editor.putString("adminarea", obj.getAdminArea());
            editor.putString("postalcode", obj.getPostalCode());
            editor.putString("subadminarea", obj.getSubAdminArea());
            editor.putString("locality", obj.getLocality());
            editor.putString("sublocality", obj.getSubLocality());
            editor.putString("streetanmee", obj.getAddressLine(0));

            editor.commit();

            ForteenDayReport.RegisterEvents(add,add,add);
            /****'
             * REQUEST QUES
             */
            //if(reqHandlerStreet != obj.getAddressLine(0)) {
                reqHandlerStreet = obj.getAddressLine(0);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, StringsList.webserviceApi,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        String newDateStr = formatter.format(date);
                        SharedPreferences sharedpreferencesinsrt = getSharedPreferences(MyPREFERENCESdb, Context.MODE_PRIVATE);
                        params.put("country", sharedpreferencesinsrt.getString("country", ""));
                        params.put("adminarea", sharedpreferencesinsrt.getString("adminarea", ""));
                        params.put("postalcode", sharedpreferencesinsrt.getString("postalcode", ""));
                        params.put("subadminarea", sharedpreferencesinsrt.getString("subadminarea", ""));
                        params.put("locality", sharedpreferencesinsrt.getString("locality", ""));
                        params.put("sublocality", sharedpreferencesinsrt.getString("sublocality", ""));
                        SharedPreferences sharedpreferencescidimei = getSharedPreferences(CareFragment.MyPREFERENCESdb, Context.MODE_PRIVATE);
                        params.put("imei", sharedpreferencescidimei.getString("myid", ""));
                        params.put("streetanmee", sharedpreferencesinsrt.getString("streetanmee", ""));
                        params.put("date", newDateStr);

                        return params;
                    }
                };
                requestQueue.add(stringRequest);
       //     }
            /***
             * RESULTS  OF VOLLEY
             */

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // Toast.makeText(this, "ERROR!!! Please Check Your Network Connection and Return Back", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu, menu);
        // getMenuInflater().inflate(R.menu.morec,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.page_settings);
                dialog.setTitle("Settings");
                dialog.setCancelable(true);
                CheckBox dialogButton = (CheckBox) dialog.findViewById(R.id.checkBox);
                dialogButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            MinimizeS(0);
                        }
                        if(isChecked){
                            MinimizeS(5);
                        }
                    }
                });
                dialog.show();


                return true;
            case R.id.aboutus:
                final Dialog dialogus = new Dialog(MainActivity.this);
                dialogus.setContentView(R.layout.page_about_us);
                dialogus.setTitle("ስለ እኛ");
                dialogus.setCancelable(true);
                //  Button dialogButtonus = (Button) dialogus.findViewById(R.id.dialogButtonOKus);
                dialogus.show();
              /*  dialogButtonus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogus.dismiss();
                    }
                });
                */
                return true;
            case R.id.abouttheapp:
                final Dialog dialogapp = new Dialog(MainActivity.this);
                dialogapp.setContentView(R.layout.page_about_app);
                dialogapp.setTitle("ስለ መተግበሪያው");
                dialogapp.setCancelable(true);
                //  Button dialogButtonapp = (Button) dialogapp.findViewById(R.id.dialogButtonOKapp);
                dialogapp.show();
               /* dialogButtonapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogapp.dismiss();
                    }
                });
                */
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("title_location_permission")
                        .setMessage("text_location_permission")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //  Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return true;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //  Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    private void bgRecorder(){
        //reqHandlerStreet = obj.getAddressLine(0);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, StringsList.webserviceApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String newDateStr = formatter.format(date);
                SharedPreferences sharedpreferencesinsrt = getSharedPreferences(MyPREFERENCESdb, Context.MODE_PRIVATE);
                params.put("country", sharedpreferencesinsrt.getString("country", ""));
                params.put("adminarea", sharedpreferencesinsrt.getString("adminarea", ""));
                params.put("postalcode", sharedpreferencesinsrt.getString("postalcode", ""));
                params.put("subadminarea", sharedpreferencesinsrt.getString("subadminarea", ""));
                params.put("locality", sharedpreferencesinsrt.getString("locality", ""));
                params.put("sublocality", sharedpreferencesinsrt.getString("sublocality", ""));
                SharedPreferences sharedpreferencescidimei = getSharedPreferences(CareFragment.MyPREFERENCESdb, Context.MODE_PRIVATE);
                params.put("imei", sharedpreferencescidimei.getString("myid", ""));
                params.put("streetanmee", sharedpreferencesinsrt.getString("streetanmee", ""));
                params.put("date", newDateStr);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private SSLSocketFactory getSocketFactory() {

        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = getResources().openRawResource(R.raw.ssl);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }


            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);


            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);


            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    Log.e("CipherUsed", session.getCipherSuite());
                    return hostname.compareTo("dmuit.abyssiniasoft.com")==0; //The Hostname of your server

                }
            };


            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            SSLContext context = null;
            context = SSLContext.getInstance("TLS");

            context.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

            SSLSocketFactory sf = context.getSocketFactory();


            return sf;

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return  null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void MinimizeS(int volume)
    {
        AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        Uri notification = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer player = MediaPlayer.create(getApplicationContext(), notification);
        player.start();
    }

}
