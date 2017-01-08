package com.sfuapichallenge.droptableteam.majorloo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<String[]> CSVwashroomList;
    private ArrayList<Washroom> washroomList;

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
