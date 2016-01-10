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
import com.vaslabs.trackpa_receiver.encryption.EncryptionManager;

import org.vaslabs.smsradar.Sms;
import org.vaslabs.smsradar.SmsListener;
import org.vaslabs.smsradar.SmsRadar;


public class LocationTrackerMap implements OnMapReadyCallback, LocationListener {

    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private final Context context;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker myPositionMarker;
    private Marker trackedPositionMarker;
    
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
        final String phoneBeingTracked = PreferenceManager.getDefaultSharedPreferences(context).getString("tracking_phone", "");
        if (phoneBeingTracked.equals(""))
            return;
        if (!phoneBeingTracked.equals(phoneNumber)) {
            return;
        }

        String body = sms.getMsg();

        LatLng latLng = latLongFromMsg(body);
        if (latLng == null)
            return;

        if (trackedPositionMarker == null)
            initialiseTrackedPositionMarker(latLng);
        else
            trackedPositionMarker.setPosition(latLng);

    }

    private void initialiseTrackedPositionMarker(LatLng latLng) {
        trackedPositionMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
    }

    private LatLng latLongFromMsg(String body) {
        if (!(body.contains("Lat:") && body.contains("Lng:")))
        {
            if (body.length() > 50) {
                body = decryptBody(body);
            }
        }
        if (!(body.contains("Lat:") && body.contains("Lng:")))
        {
            return null;
        }

        String[] parts = body.split(",");
        String latData = parts[0];
        String lngData = parts[1];

        double latitude = entryToDouble(latData);
        double longitude = entryToDouble(lngData);
        return new LatLng(latitude, longitude);
    }

    private double entryToDouble(String lngData) {
        return Double.parseDouble(lngData.split(":")[1].trim());
    }

    private String decryptBody(String body) {
        try {
            EncryptionManager em = new EncryptionManager();
            return em.decrypt(body, context);
        } catch (Exception e) {
            return "";
        }
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

