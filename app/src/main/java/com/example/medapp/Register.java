package com.example.medapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    EditText e1,e2,e3;
    Button b;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e1 = findViewById(R.id.name);
        e2 = findViewById(R.id.Phone);
        e3 = findViewById(R.id.password);
        b = findViewById(R.id.signup);
        db = openOrCreateDatabase("Mapmarkers", Context.MODE_PRIVATE, null);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e1.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Invalid username",Toast.LENGTH_LONG).show();
                }
                else if (e2.getText().length()<10){
                    Toast.makeText(getApplicationContext(),"Invalid phone number",Toast.LENGTH_LONG).show();
                }
                else if (e3.getText().length()>15 || e3.getText().length()<3){
                    Toast.makeText(getApplicationContext(),"Invalid password",Toast.LENGTH_LONG).show();
                }
                else{
                    Cursor c = db.rawQuery("SELECT * FROM logins WHERE phone='" + e2.getText().toString() + "'", null);
                    if (c.getCount() == 0) {
                        db.execSQL("INSERT INTO logins VALUES('" + e1.getText().toString() + "','" + e2.getText().toString() + "','" + e3.getText().toString() + "','" + 0 + "','" + 0 + "');");
                        startActivity(new Intent(Register.this,MainActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Phone number already exists",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
