package com.example.medapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener
{

    Button b,b1;
    EditText e1,e2;
    SQLiteDatabase db;
    double la,lo;
    protected LocationManager locationManager;
    ProgressDialog progress;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progress = new ProgressDialog(this);
        progress.setMessage("Please wait..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
            if (la==0){
                progress.show();
            }
            else {
                progress.dismiss();
            }
        b = findViewById(R.id.login);
        b1 = findViewById(R.id.show);
        e1 = findViewById(R.id.Phone);
        e2 = findViewById(R.id.password);
        b.setOnClickListener(this);
        b1.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);

        db = openOrCreateDatabase("Mapmarkers", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS logins(phone VARCHAR,password VARCHAR,latitude DOUBLE,longitude DOUBLE);");





    }

    @Override
    public void onClick(View v) {
        if (v.getId() == b.getId()){
            db.execSQL("INSERT INTO logins VALUES('" + e1.getText() + "','" + e2.getText() +
                    "','" + la + "','" + lo + "');");
            sp = getSharedPreferences("mycredentials", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("phone",e1.getText().toString());
            edit.putString("pass",e2.getText().toString());
            edit.commit();
            Intent i = new Intent(MainActivity.this,Maps.class);
           startActivity(i);
           finish();
        }
        if (v.getId() == b1.getId()){
            Cursor c = db.rawQuery("SELECT * FROM logins", null);
            // Checking if no records found 
            if (c.getCount() == 0) {
                showMessage("Error", "No records found");
                return;
            }
            Toast.makeText(this,"total:"+c.getCount(),Toast.LENGTH_LONG).show();
            // Appending records to a string buffer 
            StringBuffer buffer = new StringBuffer();
            while (c.moveToNext())
            {
                buffer.append("phone: " + c.getString(0) + "\n");
                buffer.append("password: " + c.getString(1) + "\n");
                buffer.append("latitude: " + c.getString(2) + "\n");
                buffer.append("longitude: " + c.getString(3) + "\n\n");
            }
            // Displaying all records 
            showMessage("Login Details", buffer.toString());

        }
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        la =  location.getLatitude();
        lo =  location.getLongitude();
        if (la==0){
            progress.show();
        }
        else {
            progress.dismiss();
        }


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
}
