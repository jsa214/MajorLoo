package com.sfuapichallenge.droptableteam.majorloo;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Rating;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.gson.Gson;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<String[]> CSVwashroomList;
    private ArrayList<Washroom> washroomList;
    private WashroomManager washroomManager;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static RatingBar ratingBar;
    private Button rateButton;
    SharedPreferences  mPrefs;

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

        mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("Washrooms", "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<Washroom>>(){}.getType();
            washroomList = gson.fromJson(json, type);
        }else{
            washroomList = new ArrayList<Washroom>();
        }

        CSVwashroomList = new ArrayList<String[]>();

        washroomManager = washroomManager.getInstance();

        InputStream inputStream = getResources().openRawResource(R.raw.vancouver_public_washrooms);
        CSVParser csvParser = new CSVParser(inputStream);
        CSVwashroomList = csvParser.read();
        for(String[] stringList: CSVwashroomList) {
            LatLng newLatLng = new LatLng(Double.parseDouble(stringList[8]), Double.parseDouble(stringList[9]));
            Washroom washroom = new Washroom(stringList[0],stringList[1], stringList[2], stringList[3],
                    stringList[4], stringList[5], stringList[6], stringList[7], stringList[10],
                    newLatLng, 0);

            washroomList.add(washroom);
            washroomManager.addWashroom(washroom);
        }
        rateButton = (Button) findViewById(R.id.rateButton);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.INVISIBLE);
        rateButton.setVisibility(View.INVISIBLE);



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

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                ratingBar.setVisibility(View.INVISIBLE);
                rateButton.setVisibility(View.INVISIBLE);
            }
        });

        // when location changes
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                ArrayList<Marker> markerList = new ArrayList<>();



                for (final Washroom washroom : washroomList) {
                    Double delta = SphericalUtil.computeDistanceBetween(userLocation, washroom.getLatLng());

                    if (delta < 1500) {
                         Marker marker = mMap.addMarker(new MarkerOptions().position(washroom.getLatLng()).title(washroom.getName())
                                .snippet("Name: " + washroom.getName() + "\n" + "Address: " + washroom.getAddress() + "\n"
                                        + "Type: " + washroom.getType() + "\n" + "Location: " + washroom.getLocation() + "\n"
                                        + "Summer hours: " + washroom.getSummerHours() + "\n" + "Winter hours: " + washroom.getWinterHours() + "\n"
                                        + "Wheelchair Access: " + washroom.getWheelchairAccess() + "\n" +
                                        "Note: " + washroom.getNote() + "\n" + "Maintainer: " + washroom.getMaintainer()));
                        marker.setTag(washroom);
                        markerList.add(marker);

                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                ratingBar.setVisibility(View.VISIBLE);
                                rateButton.setVisibility(View.VISIBLE);
                                Context context = getApplicationContext();
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

                                final Washroom w = findWashroomByName(marker.getTitle());
                                //Toast.makeText(MapsActivity.this,marker.getTitle() + ": " +  Float.toString(w.getNumOfStars()), Toast.LENGTH_SHORT).show();

                                ratingBar.setRating(w.getNumOfStars());


//                                ratingBar.setOnRatingBarChangeListener(
//                                        new RatingBar.OnRatingBarChangeListener() {
//                                            @Override
//                                            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//
//                                            }
//                                        }
//                                );
                                rateButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        w.setNumOfStars(ratingBar.getRating());
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                        Gson gson = new Gson();
                                        String json = gson.toJson(washroomList);
                                        prefsEditor.putString("Washrooms", json);
                                        prefsEditor.commit();
                                        //Toast.makeText(MapsActivity.this, w.getName() + ": " + Float.toString(ratingBar.getRating()), Toast.LENGTH_SHORT).show();

                                    }
                                });
                                return info;
                            }
                        });
                    }
                }

                for (Marker m: markerList) {
                    if(m.getTag() == washroomManager.findNearestTo(userLocation)) {
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    }
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
                ArrayList<Marker> markerList = new ArrayList<>();


                for (final Washroom washroom : washroomList) {
                    Double delta = SphericalUtil.computeDistanceBetween(userLocation, washroom.getLatLng());
                    if (delta < 1500) {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(washroom.getLatLng()).title(washroom.getName())
                                .snippet("Name: " + washroom.getName() + "\n" + "Address: " + washroom.getAddress() + "\n"
                                        + "Type: " + washroom.getType() + "\n" + "Location: " + washroom.getLocation() + "\n"
                                        + "Summer hours: " + washroom.getSummerHours() + "\n" + "Winter hours: " + washroom.getWinterHours() + "\n"
                                        + "Wheelchair Access: " + washroom.getWheelchairAccess() + "\n" +
                                        "Note: " + washroom.getNote() + "\n" + "Maintainer: " + washroom.getMaintainer()));
                        marker.setTag(washroom);
                        markerList.add(marker);

                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                ratingBar.setVisibility(View.VISIBLE);
                                rateButton.setVisibility(View.VISIBLE);
                                Context context = getApplicationContext();
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
                                final Washroom w = findWashroomByName(marker.getTitle());
                                //Toast.makeText(MapsActivity.this, marker.getTitle() + ": " +  Float.toString(w.getNumOfStars()), Toast.LENGTH_SHORT).show();

                                ratingBar.setRating(w.getNumOfStars());
//                                ratingBar.setOnRatingBarChangeListener(
//                                        new RatingBar.OnRatingBarChangeListener() {
//                                            @Override
//                                            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//
//                                            }
//                                        }
//                                );
                                rateButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        w.setNumOfStars(ratingBar.getRating());
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                        Gson gson = new Gson();
                                        String json = gson.toJson(washroomList);
                                        prefsEditor.putString("Washrooms", json);
                                        prefsEditor.commit();

                                        //Toast.makeText(MapsActivity.this,w.getName() + ": " +  Float.toString(ratingBar.getRating()), Toast.LENGTH_SHORT).show();

                                    }
                                });
                                return info;
                            }
                        });
                    }
                }

                for (Marker m: markerList) {
                    if(m.getTag() == washroomManager.findNearestTo(userLocation)) {
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    }
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
            }
        }

        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    /*
     * from http://stackoverflow.com/questions/18053156/set-image-from-drawable-as-marker-in-google-map-version-2
     */

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Washroom findWashroomByName (String name){
        for(Washroom w : washroomList) {
            if(w.getName().equals(name)) {
                return w;
            }
        }
        return null;
    }




}
