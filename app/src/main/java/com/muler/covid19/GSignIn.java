package com.muler.covid19;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.SignInButton;
import com.muler.covid19.careFragment.CareFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class GSignIn extends AppCompatActivity implements NetworkChangeReciver.ConnectionChangeCallback{
    SignInButton signin;
    Button nosignin;
   public static DatabaseHelper dbhelper;
    EditText fullname, nkname, addressl, guzotarik;
    private Dialog  dialogapp;
    public String imei;
   private long fdigits,ranrandnumber;
   RequestQueue requestQueue;
   ProgressBar progressBarhome;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_sign_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        NukeSSLCerts nukeSSLCerts = new NukeSSLCerts();
        nukeSSLCerts.nuke();
        requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
        //requestQueue = Volley.newRequestQueue(getApplicationContext());


        fullname = findViewById(R.id.fullname);
        nkname = findViewById(R.id.nikname);
        addressl = findViewById(R.id.addressmenoria);
        guzotarik = findViewById(R.id.guzotarik);
        progressBarhome = findViewById(R.id.progressBarhome);
        checkLocationPermission();
        isStoragePermissionGranted();
        progressBarhome.setVisibility(View.GONE);
        /*
        get IMEI NUMBER
         */
         fdigits = (long) (Math.random() * 100000000000000L);
         ranrandnumber = 5200000000000000L + fdigits;
/*
checking connection status

 */
           dialogapp = new Dialog(GSignIn.this);
        dialogapp.setContentView(R.layout.neterror);
        dialogapp.setTitle("Connection Error");
        dialogapp.setCancelable(false);

        IntentFilter ifil = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangeReciver netr = new NetworkChangeReciver();
        registerReceiver(netr,ifil);
        netr.setConnectionChangecallback(this);
        try {
            Connection_check check_c = new Connection_check(this);
            Boolean isc = check_c.isConnectedToInternet();
            if(!isc) {

                dialogapp.show();
            }
            else{
dialogapp.dismiss();
            }
        }
        catch (Exception e){
        }
        /*
end of checking connection status

 */


        dbhelper = new DatabaseHelper(this);
        nosignin = findViewById(R.id.nosigninbtn);
        try {
//Cursor res = dbhelper.getAllData();
            SQLiteDatabase db = dbhelper.getWritableDatabase();
            Cursor mCursoor = db.rawQuery("SELECT fullname FROM user_info", null);
            if(mCursoor != null && mCursoor.getCount() > 0){
                Intent i = new Intent(GSignIn.this, MainActivity.class);
                startActivity(i);
                finish();
            }


        }
        catch (Exception ex){

        }





        nosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imei = ranrandnumber+fullname.getText().toString();
                if(TextUtils.isEmpty(fullname.getText())){
                    fullname.setError( "እባክዎትን በትክክል  ይሙሉ");
                }
                if(TextUtils.isEmpty(addressl.getText())){
                    addressl.setError( "እባክዎትን በትክክል  ይሙሉ");
                }

                else {
                    progressBarhome.setVisibility(View.VISIBLE);
                    String profileonline =   "ሙሉ ስም: "+fullname.getText().toString()+"\n"+
                            "ቅፅል ስም: "+nkname.getText().toString()+"\n"+
                            "አድራሻ: "+addressl.getText().toString()+"\n"+
                            "የበፊት የጉዞ ታሪክ: "+guzotarik.getText().toString();
                    SharedPreferences sharedpreferences = getSharedPreferences(CareFragment.MyPREFERENCESdb, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("profile", profileonline);
                    editor.putString("myid", imei);
                    editor.commit();

/*
                    RegisterUser registerUser= new RegisterUser();
                    registerUser.execute(imei,fullname.getText().toString(),nkname.getText().toString(),
                            addressl.getText().toString(), guzotarik.getText().toString());
*/


                    /****'
                     * CHECKING POF REQUEST QUESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
                     */

StringRequest stringRequest = new StringRequest(Request.Method.POST, StringsList.SignupuRl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    dbhelper.insertData(fullname.getText().toString(),nkname.getText().toString(),
                                            addressl.getText().toString(),guzotarik.getText().toString());
                                    progressBarhome.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"በትክክል ተመዝግበዋል",Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(GSignIn.this, MainActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBarhome.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Network Error"+error.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("imei", imei);
                            params.put("fn", fullname.getText().toString());
                            params.put("nn", nkname.getText().toString());
                            params.put("ad", addressl.getText().toString());
                            params.put("hb", guzotarik.getText().toString());

                            return params;
                        }
                    };

                    requestQueue.add(stringRequest);

                    /***
                     * TESTING OF VOLLEYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY
                     */
                }
            }
        });


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
                                ActivityCompat.requestPermissions(GSignIn.this,
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
                        // locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
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
                ActivityCompat.requestPermissions(GSignIn.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return true;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //  Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    /*
    INSERTING DATA TO USER INFO MYSQL DATABASE
     */
    private void registerUser(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

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

                        dbhelper.insertData(fullname.getText().toString(),nkname.getText().toString(),
                                addressl.getText().toString(),guzotarik.getText().toString());

                        Toast.makeText(getApplicationContext(),"--"+status+"በትክክል ተመዝግበዋል",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(GSignIn.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
Toast.makeText(getApplicationContext(),"There was some error please try again",Toast.LENGTH_LONG).show();
                    }

                }
                catch (Exception ex){
Toast.makeText(getApplicationContext(),"እባክዎ እንደገና ይሞከሩ",Toast.LENGTH_LONG).show();
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
                }

            }
        }

        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    @Override
    public void onConnectionChange(boolean isConnected) {

if(!isConnected){

    dialogapp.show();
}
else{
dialogapp.dismiss();
}
    }
    /******
     * Server SSL Certificate validation
     */

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




}
