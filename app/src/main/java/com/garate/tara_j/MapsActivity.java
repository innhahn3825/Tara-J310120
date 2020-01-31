package com.garate.tara_j;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //CLASS DECLARATION
    private GoogleMap mMap;
    boolean isFirstTime;
    LocationManager locationManager;
    public static final String TAG = "MapsActivity";

    //WIDGETS
    private EditText OriginSearch, DestinationSearch;
    private ImageButton buttonOrigin, buttonDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        OriginSearch = findViewById(R.id.origin_search);
//        DestinationSearch = findViewById(R.id.destination_search);
        buttonOrigin = findViewById(R.id.buttonOrigin);
        buttonDestination = findViewById(R.id.buttonDestination);
        buttonOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrigin();
            }
        });
        buttonDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDestination();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Marker
        isFirstTime = true;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //FIRE permissions
        Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // CHECK if all permissions are GRANTED
                if (report.areAllPermissionsGranted()) {
                    Geolocation();
                    ShowDefault();
                    mMap.setMyLocationEnabled(true);
                    //init();
                }
                else {
                    showSettingsDialog();
                    ShowDefault();
                }

                // CHECK if any permission is PERMANENTLY DENIED
                if (report.isAnyPermissionPermanentlyDenied()) {
                    showSettingsDialog();
                    ShowDefault();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest(); //PROMPT for the permission
            }
        }).withErrorListener(new PermissionRequestErrorListener() {

            @Override
            public void onError(DexterError error) {
                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check(); //CHECKS FOR ERROR
    }

//    private void init() {
//        OriginSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
//                    originGeolocate();
//                }
//                return false;
//            }
//        });
//
////        DestinationSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
////            @Override
////            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
////                if (actionId == EditorInfo.IME_ACTION_SEARCH
////                        || actionId == EditorInfo.IME_ACTION_DONE
////                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
////                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
////                    destinationGeolocate();
////                }
////                return false;
////            }
////        };
//
////        hideSoftKeyboard();
//    }

    private void openOrigin() {
        Intent intent = new Intent(this, Origin.class);
        startActivity(intent);
    }

    private void openDestination() {
        Intent intent = new Intent(this, Destination.class);
        startActivity(intent);
    }

    private void originGeolocate() {
        String searchString = OriginSearch.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d(TAG, "originGeolocate: IOException " + e.getMessage());
        }

        if (list.size() > 0){
            Address address = list.get(0);
        }
    }

//    private void destinationGeolocate() {
//        String searchString = mSearchText.getText().toString();
//        Geocoder geocoder = new Geocoder(MapsActivity.this);
//        List<Address> list = new ArrayList<>();
//        try {
//            list = geocoder.getFromLocationName(searchString, 1);
//        } catch (IOException e) {
//            Log.d(TAG, "originGeolocate: IOException " + e.getMessage());
//        }
//
//        if (list.size() > 0){
//            Address address = list.get(0);
//        }
//    }

    //DEFAULT MAP VIEW
    private void ShowDefault() {
        LatLng davaoDefault = new LatLng(7.0707, 125.6087);
        if (isFirstTime == true) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(davaoDefault, 12f));
        }
        isFirstTime = false;
    }

    //METHOD for the GEOLOCATION
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Geolocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //CHECK if network provider is ENABLED
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @SuppressWarnings("MoveFieldAssignmentToInitializer")
                @Override
                public void onLocationChanged(Location location) {

                    //GET latitude
                    double latitude = location.getLatitude();
                    //GET longitude
                    double longitude = location.getLongitude();
                    //INSTANTIATE LatLng class
                    LatLng latLng = new LatLng(latitude, longitude);
                    //INSTANTIATE Geocoder class
                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {
                        List<Address> adressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = adressList.get(0).getSubLocality() + ", ";
                        str += adressList.get(0).getLocality() + ", ";
                        str += adressList.get(0).getCountryName();
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
//                        if (isFirstTime == true) {
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
//                        }
//                        isFirstTime = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        //GPS Backup
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //GET latitude
                    double latitude = location.getLatitude();
                    //GET longitude
                    double longitude = location.getLongitude();
                    //INSTANTIATE LatLng class
                    LatLng latLng = new LatLng(latitude, longitude);
                    //INSTANTIATE Geocoder clas
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> adressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = adressList.get(0).getSubLocality()+ ", ";
                        str += adressList.get(0).getLocality() + ", ";
                        str += adressList.get(0).getCountryName();
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
//                        if (isFirstTime == true) {
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
//                        }
//                        isFirstTime = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }

    //OPEN SETTINGS in case the user DENIES
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("TaraJ needs location and Storage permissions to function. You can grant them in the application's settings. (TaraJ > Permissions)");
        builder.setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
