package com.example.medapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener
{

    Button b,b1,b2;
    EditText e1,e2;
    SQLiteDatabase db;
    double la,lo;
    protected LocationManager locationManager;
    ProgressDialog progress;
    SharedPreferences sp;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permissions were already given",Toast.LENGTH_LONG).show();
        }
        else{
            requestlocationpermission();
        }

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
        b2 = findViewById(R.id.list);
        e1 = findViewById(R.id.Phone);
        e2 = findViewById(R.id.password);
        b.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);

        db = openOrCreateDatabase("Mapmarkers", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS logins(name VARCHAR,phone VARCHAR,password VARCHAR,latitude DOUBLE,longitude DOUBLE);");





    }

    @Override
    public void onClick(View v) {
        if (v.getId() == b.getId()){
            if (e1.getText().length()<10){
                Toast.makeText(this,"Invalid phone number",Toast.LENGTH_LONG).show();
            }
            else if (e2.getText().length()>15 || e2.getText().length()<3){
                Toast.makeText(this,"Invalid password",Toast.LENGTH_LONG).show();
            }
            else{
                int k=0;
                Cursor c = db.rawQuery("SELECT * FROM logins WHERE phone='" + e1.getText().toString() + "' and password = '" + e2.getText().toString() + "'", null);
                    while (c.moveToNext()){
                        if (c.getCount()!=0) {
                            db.execSQL("UPDATE logins SET latitude='" + la + "',longitude='" + lo +
                                    "' WHERE phone='" + e1.getText().toString() + "'");
                            sp = getSharedPreferences("mycredentials", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("phone", e1.getText().toString());
                            edit.putString("pass", e2.getText().toString());
                            edit.commit();
                            Intent i = new Intent(MainActivity.this, Maps.class);
                            startActivity(i);
                            finish();
                            k++;
                        }
//                        else {
//                            Toast.makeText(this, "Please check your Credentials", Toast.LENGTH_LONG).show();
//                        }
                    }
                    if (k==0){
                        Toast.makeText(this, "Please check your Credentials", Toast.LENGTH_LONG).show();

                }

            }
//                Toast.makeText(this,"Already Exists",Toast.LENGTH_LONG).show();
        }
        if (v.getId() == b1.getId()){
//
            Intent i = new Intent(MainActivity.this,Register.class);
            startActivity(i);

        }
        if (v.getId() == b2.getId()){
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
                buffer.append("name: " + c.getString(0) + "\n");
                buffer.append("phone: " + c.getString(1) + "\n");
                buffer.append("password: " + c.getString(2) + "\n");
                buffer.append("latitude: " + c.getString(3) + "\n");
                buffer.append("longitude: " + c.getString(4) + "\n\n");
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

//    public boolean validate(){
//        int c = 0;
//        boolean a;
//        Cursor s =  db.rawQuery("SELECT * FROM logins", null);
//        while (s.moveToNext()){
//            if (e1.getText().toString() == s.getString(0)){
//                c++;
//            }
//        }
//        if (c>0){
//            a = false;
//        }
//        else{
//            a = true;
//        }
//        return a;
//    }

    public void requestlocationpermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION) &&ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)){
            new AlertDialog.Builder(this).setTitle("permissions needed").setMessage("This permission is needed for location access.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
