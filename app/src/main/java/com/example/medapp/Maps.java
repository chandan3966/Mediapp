package com.example.medapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Maps extends FragmentActivity implements LocationListener, OnMapReadyCallback {

    protected LocationManager locationManager;
    TextView t;
    SQLiteDatabase db;
    private int rows;
    String phone,pass;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlong = new ArrayList<>();
    double lat,longi,nlat,nlong;
    SharedPreferences sp;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);

        sp = getSharedPreferences("mycredentials",Context.MODE_PRIVATE);
        phone = sp.getString("phone","NA");
        pass = sp.getString("pass","NA");

        db = openOrCreateDatabase("Mapmarkers", Context.MODE_PRIVATE, null);
        latuslongus();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng home = new LatLng(location.getLatitude(), location.getLongitude());
        lat = location.getLatitude();
        longi = location.getLongitude();
//        float zoomLevel = (float) 5.0;
        Cursor c = db.rawQuery("SELECT * FROM logins WHERE phone='" + phone + "' and password='" + pass +"'", null);
        if (c.moveToFirst()) {
            // Modifying record if found 
            db.execSQL("UPDATE logins SET latitude='" + lat + "',longitude='" + longi+
                    "' WHERE phone='" + phone + "'");
        }
        else {

        }
        Toast.makeText(this, ""+ lat,Toast.LENGTH_LONG).show();
//        mMap.addMarker(new MarkerOptions().position(home).title(phone));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoomLevel));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "OnMapReady", Toast.LENGTH_SHORT).show();

//        LatLng home = new LatLng(lat, longi);
//        float zoomLevel = (float) 5.0;
//        mMap.addMarker(new MarkerOptions().position(home).title(phone));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoomLevel));
        for (LatLng point : latlong) {
            options.position(point);
            options.title("someTitle");
            options.snippet("someDesc");
            googleMap.addMarker(options);
        }

    }


    public void latuslongus() {
        Cursor c = db.rawQuery("SELECT * FROM logins", null);
        while (c.moveToNext()) {
            nlat =  c.getDouble(2);
            nlong =  c.getDouble(3);
            latlong.add(new LatLng(nlat, nlong));
        }
    }
}


