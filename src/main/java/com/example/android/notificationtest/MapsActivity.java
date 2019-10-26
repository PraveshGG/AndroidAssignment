package com.example.android.notificationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,FetchAddressTask.onTaskCompleted {

    private GoogleMap mMap;
    private static final String TAG = "checkRaw";
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    int distanceBetween;
    FusedLocationProviderClient mFusedLocationProviderClient;
    String clickedMarkerLocation="";
    Location mUserLocation;
    private static final String PRIMARY_CHANNEL_ID =    "primary_notification_channel";
    private static final  int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map_options is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    mUserLocation = location;
                    LatLng myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(myLocationLatLng).title("You're here."));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocationLatLng, 13));
                }else{

                }
            }
        });
        try{
            boolean isSuccess = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_file));
            if(!isSuccess){
                Log.e(TAG, "Style parsing failed");
            }
        }catch(Resources.NotFoundException e){
            Log.e(TAG, "Cant find the Style", e);

        }
        // Add a marker in Sydney and move the camera


        //GroundOverlayOptions homeOverlay = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.android)).position(sydney, 1000);

        setMapLongClick(mMap);
        setPoiClick(mMap);
        enableMyLocation();



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                new FetchAddressTask(MapsActivity.this,
                        MapsActivity.this).execute(setLocation(marker.getPosition()));

                distanceBetween = (int) mUserLocation.distanceTo(setLocation(marker.getPosition()));
                Bitmap androidImage = BitmapFactory.decodeResource(
                        getResources(),R.drawable.starter_img) ;
                NotificationCompat.Builder notifyBuilder =  getNotificationBuilder();
                notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(androidImage)
                        .setBigContentTitle("Distance between you and " ));
                mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
                return false;
            }
        });
    }

    private void setMapLongClick(GoogleMap googleMap){
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                String snippet = String.format(Locale.getDefault(), "Lat %1.5f, Long: %2.5f",
                        latLng.latitude,latLng.longitude);

                new FetchAddressTask(MapsActivity.this,
                        MapsActivity.this).execute(setLocation(latLng));

                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.dropped_pin))
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            }
        });
    }

    private void setPoiClick(final GoogleMap map){
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest)
            {

                new FetchAddressTask(MapsActivity.this,
                        MapsActivity.this).execute(setLocation(pointOfInterest.latLng));
                Toast.makeText(MapsActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                Marker poiMarker = map.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng)
                        .title("Hey"));
                poiMarker.showInfoWindow();
            }
        });
    }

    private void enableMyLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //check if the location permission is granted or not
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    enableMyLocation();
                    break;
                }
        }
    }

    @Override
    public void onTaskCompleted(String result) {

        clickedMarkerLocation =result;
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Address is:" + result, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public Location setLocation(LatLng latlng ){
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(latlng.latitude);
        temp.setLongitude(latlng.longitude);

        return temp;
    }
    public void sendNotifications(View view){
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());

    }

    public void createNotificationChannel(){
        Toast.makeText(this, "channel created", Toast.LENGTH_SHORT).show();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            //create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Test Alerts",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Hey this is to notify you that notification service works :D");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }else{
            Toast.makeText(this, "else ma gayo ne ta bro", Toast.LENGTH_SHORT).show();
        }
    }
    public NotificationCompat.Builder getNotificationBuilder(){

        Intent notificationIntent =new Intent(this, MapsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent broadcastHybridIntent = new Intent(this, NotificationReceiver.class);
        broadcastHybridIntent.putExtra("msg","Hybrid");
        broadcastHybridIntent.setAction("Hybrid");

        PendingIntent actionHybridIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID,
                broadcastHybridIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Intent broadcastSatelliteIntent = new Intent(this, NotificationReceiver.class);
        broadcastSatelliteIntent.putExtra("msg","Satellite");
        broadcastSatelliteIntent.setAction("Satellite");

        PendingIntent actionSatelliteIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID,
                broadcastSatelliteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
//
        Intent broadcastTerrainIntent = new Intent(this, NotificationReceiver.class);
        broadcastTerrainIntent.putExtra("msg","Terrain");
        broadcastTerrainIntent.setAction("Terrain");

        PendingIntent actionTerrainIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID,
                broadcastTerrainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);






        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("notification")
                .setContentText( clickedMarkerLocation + " is: " + distanceBetween +" meters.")
                .setSmallIcon(R.mipmap.ic_notification_icon)
                .setChannelId(PRIMARY_CHANNEL_ID )
                .addAction(R.mipmap.ic_launcher, "Hybrid", actionHybridIntent)
                .addAction(R.mipmap.ic_launcher, "Satellite", actionSatelliteIntent)
                .addAction(R.mipmap.ic_launcher, "Terrain", actionTerrainIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return notifyBuilder;

    }

}
