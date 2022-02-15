package com.muler.covid19;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Location extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    protected LocationManager locationManager;
    private double latitude =0;
    private double longtude =0;
    protected boolean gps_enabled,network_enabled;
    ProgressBar progressBar;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        progressBar = findViewById(R.id.progressBarloadmap);
        progressBar.setVisibility(View.VISIBLE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        if((latitude !=0) && (longtude != 0)) {
            LatLng mylocation = new LatLng(latitude, longtude);
            mMap.addMarker(new
                    MarkerOptions().position(mylocation).title("እርስዎ አሁን የሚገኙበት ቦታ"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getMinZoomLevel();
            mMap.isMyLocationEnabled();
            mMap.isTrafficEnabled();
            mMap.setTrafficEnabled(true);
            mMap.isBuildingsEnabled();


            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtude), 15));

           Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(-latitude, longtude))
                    .radius(10000)

                    .strokeColor(Color.CYAN));


            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    //get latlng at the center by calling
                    LatLng midLatLng = mMap.getCameraPosition().target;
                }
            });

        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
       latitude =   location.getLatitude();
         longtude = location.getLongitude();
        getAddress(latitude, longtude);
        if((latitude !=0) && (longtude != 0)) {

            LatLng mylocation = new LatLng(latitude, longtude);
            mMap.addMarker(new
                    MarkerOptions().position(mylocation).title("እርስዎ አሁን የሚገኙበት ቦታ"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
            mMap.getUiSettings().setZoomGesturesEnabled(true);
          mMap.getMinZoomLevel();
            mMap.isMyLocationEnabled();
            mMap.isTrafficEnabled();
            mMap.setTrafficEnabled(true);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

        mMap.isBuildingsEnabled();
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longtude))
                .radius(10000)
                .strokeColor(Color.CYAN));

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                LatLng midLatLng = mMap.getCameraPosition().target;
            }
        });


    }}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

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
        Geocoder geocoder = new Geocoder(Location.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = "Street Name:"+obj.getAddressLine(0);
            add = add + "\n" + "Country:"+obj.getCountryName();
            add = add + "\n" + "Country Code:"+obj.getCountryCode();
            add = add + "\n" + "Admin Area:"+obj.getAdminArea();
            /****8 DISPLAYES NULL VALUE */
            add = add + "\n" + "Postal Code:"+obj.getPostalCode();

            add = add + "\n" + "SubAdmin area:"+obj.getSubAdminArea();
            add = add + "\n" +"Locality:"+ obj.getLocality();

            /****8 DISPLAYES NULL VALUE */

            add = add + "\n" +"Sub Locality:"+ obj.getSubLocality();

            Log.v("IGA", "Address" + add);
            TextView txtLocation = findViewById(R.id.txtLocation);
            txtLocation.setText("Your are Currenntly found in"+add);
            progressBar.setVisibility(View.GONE);
            // Toast.makeText(this, "Address=>" + add, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          //  Toast.makeText(this, "ERROR!!! Please Check Your Network Connection and Return Back", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(Location.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}