package com.myapplicationdev.demolocationdetection;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    Button btnGetLastLocation, btnGetLocationUpdate, btnRemoveLocationUpdates;
    FusedLocationProviderClient client;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetLastLocation = findViewById(R.id.btnGetLastLocation);
        btnGetLocationUpdate = findViewById(R.id.btnGetLocationUpdate);
        btnRemoveLocationUpdates = findViewById(R.id.btnRemoveLocationUpdate);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (!checkPermission()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                /*TODO: Assertions should be used to check for something that should never happen,
           whereas exceptions (try-catch statement) should be used to check for something that could happen.*/
                assert locationResult != null;

                // Returns the most recent location found in this result, or null if no locations were found.
                Location currLocation = locationResult.getLastLocation();

                String toastMsg = String.format("New Location found\nLatitude: %s\nLongitude: %s"
                        , currLocation.getLatitude(), currLocation.getLongitude());
                Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_SHORT).show();


            }
        };

        btnGetLocationUpdate.setOnClickListener(view -> {

            // TODO: Runtime permission check
            if (checkPermission()) {

                // Begin the detection process by requesting a quality of service for location updates from the API.
                mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Set the priority of the request.
                mLocationRequest.setInterval(10000); // Set the millisecond interval for active location updates.
                mLocationRequest.setFastestInterval(5000); // Set the fastest interval for location updates explicitly in milliseconds.
                mLocationRequest.setSmallestDisplacement(100); // Set the minimum distance in meters between location updates.
                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            } else {
                String toastMsg = "Location information cannot be retrieved because permission has not been granted.";
                Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_SHORT).show();

                // Activity Helper requests permissions to be granted to this application
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        });
        btnRemoveLocationUpdates.setOnClickListener(view -> {
            // All location updates for the given location result listener are removed.
            client.removeLocationUpdates(mLocationCallback);

            String toastMsg = "Location updates were successfully removed.";
            Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
        });

        btnGetLastLocation.setOnClickListener(view -> {
            if (checkPermission()) {

                // Returns the most recent location that is currently available.
                Task<Location> task = client.getLastLocation();

                task.addOnSuccessListener(MainActivity.this, location -> {

                    String toastMsg;
                    if (location != null) {
                        toastMsg = String.format("Last Location found\nLatitude: %s\nLongitude: %s"
                                , location.getLatitude(), location.getLongitude());
                    } else {
                        toastMsg = "No last known location found";
                    }
                    Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_SHORT).show();

                });
            } else {
                String toastMsg = "Permission not granted to retrieve location info";
                Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }

        });
    }

    private boolean checkPermission() {

        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        return permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED;
    }
}