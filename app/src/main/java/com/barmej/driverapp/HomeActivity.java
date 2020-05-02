package com.barmej.driverapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.barmej.driverapp.callback.DriverActionsDelegates;
import com.barmej.driverapp.callback.PermissionFailListener;
import com.barmej.driverapp.callback.StatusCallBack;
import com.barmej.driverapp.domain.entity.Driver;
import com.barmej.driverapp.domain.entity.FullStatus;
import com.barmej.driverapp.domain.entity.Trip;
import com.barmej.driverapp.domain.entity.TripManager;
import com.barmej.driverapp.fragment.MapsContainerFragment;
import com.barmej.driverapp.fragment.StatusInfoFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class HomeActivity extends AppCompatActivity {

    private MapsContainerFragment mapsContainerFragment;
    private StatusInfoFragment statusInfoFragment;
    private PermissionFailListener permissionFailListener = getPermissionFialListener();
    private DriverActionsDelegates driverActionsDelegates = getDriverActionDelgates();

    private StatusCallBack statusCallBack = getStatusCallBack();
    private LocationCallback locationCallback;
    private FusedLocationProviderClient locationClient;

    public static Intent getStartIntent(Context context){
        return new Intent(context, HomeActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        mapsContainerFragment = (MapsContainerFragment) getSupportFragmentManager().findFragmentById( R.id.map_container_fragment );
        mapsContainerFragment.setPermissionFailListener( permissionFailListener );

        statusInfoFragment = (StatusInfoFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_status_info );
        statusInfoFragment.setDriverActionsDelegates( driverActionsDelegates );
    }

    @Override
    protected void onResume() {
        super.onResume();
        TripManager.getInstance().startListeinigForStatus( statusCallBack );

    }

    private PermissionFailListener getPermissionFialListener( ){
        return new PermissionFailListener() {
            @Override
            public void onPermissionFail() {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this  );
                builder.setMessage( R.string.location_permission_needed );
                builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                } );

            }
        };

    }

    private DriverActionsDelegates getDriverActionDelgates(){
       return new DriverActionsDelegates() {
           @Override
           public void arrivedToPickup() {
               TripManager.getInstance().updateTripToArrivedToPickUp();
           }

           @Override
           public void arrivedToDestination() {
               TripManager.getInstance().updateTripToArrivedToDestination();

           }

           @Override
           public void goOffLine() {
               TripManager.getInstance().goOffLine();
               startActivity( LoginActivity.getStartIntent(HomeActivity.this) );
               finish();
           }
       } ;
    }

    private StatusCallBack getStatusCallBack(){

        return new StatusCallBack() {
            @Override
            public void onUpdate(FullStatus fullStatus) {
                String driverStatus = fullStatus.getDriver().getStatus();
                if(driverStatus.equals( Driver.Status.AVAILABLE.name() )){
                    showAvailableScreen(fullStatus);

                }else{
                    if(driverStatus.equals( Driver.Status.ON_TRIP.name() )){
                        showOnTripView(fullStatus);
                        trackAndSendLocationUpdate();
                    }
                }

            }
        };
    }

    private void showAvailableScreen(FullStatus fullStatus){
        mapsContainerFragment.reset();
        statusInfoFragment.updateWithStatus( fullStatus );
    }

    private void showOnTripView(FullStatus fullStatus){
        Trip trip = fullStatus.getTrip();
        mapsContainerFragment.setDestinationMarker(new LatLng( trip.getDestinationLat(), trip.getDestinationLng()));
        mapsContainerFragment.setPickUpMarker( new LatLng( trip.getPickUpLat(), trip.getGetPickUpLng()));
        statusInfoFragment.updateWithStatus( fullStatus );
    }

    private void  trackAndSendLocationUpdate() {

        if (locationCallback == null) {
            locationClient = LocationServices.getFusedLocationProviderClient( this );

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult( locationResult );

                    Location lastLocation = locationResult.getLastLocation();
                    TripManager.getInstance()
                            .updateCurrentLocation( lastLocation.getLatitude(), lastLocation.getLongitude() );
                }
            };

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
                    == PackageManager.PERMISSION_GRANTED){
                locationClient.requestLocationUpdates( new LocationRequest(), locationCallback, null );
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        TripManager.getInstance().stopListeningToStatus();
        stopLocationUpdate();
    }

    private void stopLocationUpdate(){
        if(locationClient != null && locationCallback != null){
            //locationClient.requestLocationUpdates(locationCallback );
            locationCallback = null;

        }
    }
}
