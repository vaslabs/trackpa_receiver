package com.vaslabs.trackpa_receiver;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.vaslabs.smsradar.Sms;
import org.vaslabs.smsradar.SmsListener;
import org.vaslabs.smsradar.SmsRadar;


public class LocationTrackerMap implements OnMapReadyCallback, LocationListener {

    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private final Context context;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker myPositionMarker;


    public LocationTrackerMap(Context context) {
        this.context = context;
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
        initLocationManager();
        Location currentLocation = locationManager.getLastKnownLocation(Context.LOCATION_SERVICE);
        if (currentLocation != null ) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            myPositionMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 16f));
        }

        SmsRadar.initializeSmsRadarService(context, new SmsListener() {
            @Override
            public void onSmsSent(Sms sms) {
            }

            @Override
            public void onSmsReceived(Sms sms) {
                handleSms(sms);
            }
        });


    }

    private void handleSms(Sms sms) {
        final String phoneNumber = sms.getAddress();
        Toast.makeText(context, "From: " + phoneNumber, Toast.LENGTH_LONG).show();
        final String phoneBeingTracked = PreferenceManager.getDefaultSharedPreferences(context).getString("tracking_phone", "");
        if (phoneBeingTracked.equals(""))
            return;
        if (!phoneBeingTracked.equals(phoneNumber)) {
            return;
        }

        String body = sms.getMsg();
    }



    private void initLocationManager() {
        locationManager = (LocationManager)(context.getSystemService(Context.LOCATION_SERVICE));
        if (locationManager == null) {
            Toast.makeText(context, "Please enable gps", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0L, 0f, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        if (myPositionMarker == null) {
            myPositionMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        } else {
            myPositionMarker.setPosition(latLng);
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

}

