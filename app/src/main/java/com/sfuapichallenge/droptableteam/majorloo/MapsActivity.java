package com.sfuapichallenge.droptableteam.majorloo;


import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.util.ArrayList;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<String[]> CSVwashroomList;
    private ArrayList<Washroom> washroomList;
    LocationManager locationManager;
    LocationListener locationListener;

    //location permission
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CSVwashroomList = new ArrayList<String[]>();
        washroomList = new ArrayList<Washroom>();

        InputStream inputStream = getResources().openRawResource(R.raw.vancouver_public_washrooms);
        CSVParser csvParser = new CSVParser(inputStream);
        CSVwashroomList = csvParser.read();

        for(String[] stringList: CSVwashroomList) {
            LatLng newLatLng = new LatLng(Double.parseDouble(stringList[8]), Double.parseDouble(stringList[9]));
            Washroom washroom = new Washroom(stringList[0],stringList[1], stringList[2], stringList[3],
                    stringList[4], stringList[5], stringList[6], stringList[7], stringList[10],
                    newLatLng);

            washroomList.add(washroom);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                for(Washroom washroom: washroomList) {
<<<<<<< Updated upstream

                    Double delta = SphericalUtil.computeDistanceBetween(userLocation, washroom.getLatLng());
                    if(delta < 1000){
                        mMap.addMarker(new MarkerOptions().position(washroom.getLatLng()).title(washroom.getName()));

                    }
=======
                    mMap.addMarker(new MarkerOptions().position(washroom.getLatLng()).title(washroom.getName()));
>>>>>>> Stashed changes
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.currentLocation)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
            }
        }

        // move the camera to Vancouver
        LatLng vancouver = new LatLng(49.257, -123.193);
        for(Washroom washroom: washroomList) {
            mMap.addMarker(new MarkerOptions().position(washroom.getLatLng()).title(washroom.getName())
            .snippet("Name: " + washroom.getName() + "\n" + "Address: " + washroom.getAddress() + "\n"
                     + "Type: " + washroom.getType() + "\n" + "Location: " + washroom.getLocation() + "\n"
                    + "Summer hours: " + washroom.getSummerHours() + "\n" + "Winter hours: " + washroom.getWinterHours()+ "\n"
            + "Wheelchair Access: " + washroom.getWheelchairAccess() + "\n" +
                    "Note: " + washroom.getNote() + "\n" + "Maintainer: " + washroom.getMaintainer()));
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.
                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);
                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());
                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());
                    info.addView(title);
                    info.addView(snippet);
                    return info;
                }
            });
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(vancouver));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
}
