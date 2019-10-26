package com.example.android.notificationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationTracker extends AppCompatActivity implements FetchAddressTask.onTaskCompleted{

    public static final int REQUEST_LOCATION_PERMISSION = 1;
//    public static final String LOCATION = "Location";
//    private Location mLastLocation;
    private TextView tvLocation;


    Location mLocation;
    Button btnShowInMaps;

    FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracker);

        Button btnTrackLocation = findViewById(R.id.button_location);
         btnShowInMaps = findViewById(R.id.button_maps);
        btnTrackLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
        tvLocation = findViewById(R.id.textview_location);
        btnShowInMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LocationTracker.this, MapsActivity.class);
                i.putExtra("Location",mLocation);
                startActivity(i);
            }
        });
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getLocation() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }else{
           // Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        mLocation =location;
                        new FetchAddressTask(LocationTracker.this,
                                LocationTracker.this).execute(location);
//                        tvLocation.setText(getString(R.string.location_text,
//                                location.getLatitude(),
//                                location.getLongitude(),
//                                location.getTime()));


                    }else{
                        tvLocation.setText(getString(R.string.no_location));
                    }
                }
            });
        }

        //show some loading text
        tvLocation.setText(getString(R.string.address_text , getString(R.string.loading), System.currentTimeMillis()));
    }

    private boolean checkAndRequestPermissions() {

        int permissionAccessFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionAccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_LOCATION_PERMISSION);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
            {
//                Map<String, Integer> perms = new HashMap<>();
//                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }else{
                    Toast.makeText(this, "Permissions Denied!!!", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    @Override
    public void onTaskCompleted(String result) {
        tvLocation.setText(getString(R.string.address_text, result,System.currentTimeMillis()));

    }



}
