package com.pethoalpar.osmdroidexmple;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapLocationHandler {

    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public MapLocationHandler(Context context, MapView mapView) {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMapLocation(location.getLatitude(), location.getLongitude());
            }
            // Implement other LocationListener methods as needed
        };
    }

    public void startLocationUpdates() {
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void stopLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void updateMapLocation(double latitude, double longitude) {
        GeoPoint currentLocation = new GeoPoint(latitude, longitude);
        myLocationOverlay.enableFollowLocation();
        myLocationOverlay.enableMyLocation();

        // Ghi tọa độ lên Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference currentLocationRef = database.getReference("current_location");
        currentLocationRef.child("latitude").setValue(latitude);
        currentLocationRef.child("longitude").setValue(longitude);
    }

}
