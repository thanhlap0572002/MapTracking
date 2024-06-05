package com.pethoalpar.osmdroidexmple;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private SearchView mapSearchview;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private MapLocationHandler mapLocationHandler;
    private Handler handler;
    private Runnable firebaseUpdateRunnable;
    private boolean isButton1Enabled = true;
    private int currentLocationCount = 1;

    private PopupWindow popupWindow;
    private WebView webView;
    private String ipAddress = "192.168.100.6";
    private String cameraStreamPath = "/video";

    private TextView timeTextView;
    private Handler timeHandler;
    private Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        // Khi nút popupAlertButton được nhấn
        Button popupAlertButton = findViewById(R.id.popup_alert_button);
        popupAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

        // Khi nút myButton được nhấn
        Button myButton = findViewById(R.id.myButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức hiển thị Weather
                Weather weatherOverlay = new Weather(MainActivity.this, currentLocation);
                weatherOverlay.showDialog();
                weatherOverlay.startFlashingScreen();
            }
        });

        // Khi nút buttonLocation được nhấn
        Button buttonLocation = findViewById(R.id.Button_firebase);
        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức hiển thị Locations
                LocationOverlay locationOverlay = new LocationOverlay(MainActivity.this, currentLocation);
                locationOverlay.showDialog();
                locationOverlay.startFlashingScreen();
            }
        });

        mapSearchview = findViewById(R.id.mapSearch);
        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setZoom(15.0);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        mapLocationHandler = new MapLocationHandler(this, mapView);

        mapSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = mapSearchview.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    Marker marker = new Marker(mapView);
                    marker.setPosition(new GeoPoint(latLng.latitude, latLng.longitude));
                    marker.setTitle(location);
                    mapView.getOverlays().add(marker);
                    mapView.invalidate();

                    GeoPoint geoPoint = new GeoPoint(latLng.latitude, latLng.longitude);
                    mapView.getController().setCenter(geoPoint);
                    mapView.getController().setZoom(10);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference searchLocationRef = database.getReference("search_location");
                    searchLocationRef.setValue(latLng);

                    String searchKey = UUID.randomUUID().toString();
                    DatabaseReference saveLocation = database.getReference("Save Way Location").child(searchKey);
                    saveLocation.setValue(latLng);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        handler = new Handler();
        firebaseUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable(MainActivity.this) && !isAppInBackground()) {
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());
                    String formattedTime = dateFormat.format(currentTime);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference currentLocationRef = database.getReference("current_location");
                    DatabaseReference newLocationRef = currentLocationRef.push();
                    newLocationRef.child("lat").setValue(currentLocation.getLatitude());
                    newLocationRef.child("lon").setValue(currentLocation.getLongitude());
                    newLocationRef.child("Ngày").setValue(formattedTime);
                }
                handler.postDelayed(this, 120000);
            }
        };
    }

    private void showPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        webView = popupView.findViewById(R.id.webview);
        timeTextView = popupView.findViewById(R.id.timeTextView);

        setupWebView(webView);
        webView.loadUrl("http://" + ipAddress + cameraStreamPath);

        // Cập nhật thời gian thực
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                timeHandler.postDelayed(this, 1000); // Cập nhật mỗi giây
            }
        };
        timeHandler.post(timeRunnable);

        Button closeButton = popupView.findViewById(R.id.button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (timeHandler != null) {
                    timeHandler.removeCallbacks(timeRunnable);
                }
            }
        });
    }

    private void setupWebView(WebView webView) {
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
    }

    private void updateTime() {
        String currentTime = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault()).format(new Date());
        timeTextView.setText(currentTime);
    }

    private boolean isAppInBackground() {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        return myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    setupMap();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference currentLocationRef = database.getReference("current_location");
                    currentLocationRef.setValue(currentLocation);

                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy", Locale.getDefault());
                    String formattedTime = dateFormat.format(currentTime);
                    DatabaseReference saveCurrent = database.getReference("Save Way Current_location").child(formattedTime);
                    saveCurrent.setValue(currentLocation);
                } else {
                    Toast.makeText(MainActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupMap() {
        mapView.getController().setCenter(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));

        Marker myLocationMarker = new Marker(mapView);
        myLocationMarker.setPosition(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
        myLocationMarker.setTitle("My Location");
        mapView.getOverlays().add(myLocationMarker);

        CompassOverlay compassOverlay = new CompassOverlay(this, mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        mapView.invalidate();

        mapLocationHandler.startLocationUpdates();

        handler.postDelayed(firebaseUpdateRunnable, 120000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapLocationHandler.stopLocationUpdates();
        handler.removeCallbacks(firebaseUpdateRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied, please allow", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
