package com.sfuapichallenge.droptableteam.majorloo;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by seongchanlee on 2017. 1. 8..
 */

public class WashroomManager {
    private static WashroomManager instance;
    private Set<Washroom> washrooms;

    private WashroomManager() {
        washrooms = new HashSet<>();
    }

    public static WashroomManager getInstance() {
        if (instance == null) {
            instance = new WashroomManager();
        }
        return instance;
    }

    public void addWashroom(Washroom w) {
        washrooms.add(w);
    }

    public Washroom findNearestTo(LatLng pt) {
        Washroom nearest = null;
        double nearestDelta = 1500.00;

        for(Washroom w: washrooms) {
            double delta = SphericalUtil.computeDistanceBetween(pt, w.getLatLng());
            if (delta < nearestDelta) {
                nearest = w;
                nearestDelta = delta;
            }
        }

        return nearest;
    }
}
