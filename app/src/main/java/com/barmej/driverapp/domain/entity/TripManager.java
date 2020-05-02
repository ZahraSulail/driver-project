package com.barmej.driverapp.domain.entity;

import android.text.TextUtils;

import com.barmej.driverapp.callback.CallBack;
import com.barmej.driverapp.callback.StatusCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.security.auth.callback.Callback;

import androidx.annotation.NonNull;

public class TripManager {
    private static final String TRIP_REF_PATH = "trips";
    private static final String DRIVER_REF_PATH = "drivers";

    private static TripManager instance;
    private FirebaseDatabase database;
    private Trip trip;
    private Driver driver;
    private ValueEventListener driverEventListener;
    private StatusCallBack statusCallBack;

    public TripManager() {
        database = FirebaseDatabase.getInstance();
    }

    public static TripManager getInstance() {
        if (instance == null) {
            instance = new TripManager();

        }
        return instance;
    }
    public void getDriverProfileAndMarkAvialableIfOffline(final String driverId, final CallBack callBack){
        database.getReference( DRIVER_REF_PATH ).child( driverId )
                .addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        driver = dataSnapshot.getValue( Driver.class );
                        if(driver != null) {
                            if (driver.getStatus().equals( Driver.Status.OFF_LINE.name() )) {
                                makeDriverAvialableAndNotify( callBack );
                            } else {
                                callBack.onComplete( true );
                            }

                        } else {
                            callBack.onComplete( false );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
    }
    private void makeDriverAvialableAndNotify(final CallBack callBack){
        driver.getStatus().equals( Driver.Status.AVAILABLE.name() );
        database.getReference( DRIVER_REF_PATH ).child( driver.getId()).setValue( driver )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callBack.onComplete( task.isSuccessful());
                    }
                } );
    }
    public void startListeinigForStatus(StatusCallBack statusCallBack){
        this.statusCallBack = statusCallBack;
        driverEventListener = database.getReference(DRIVER_REF_PATH).child( driver.getId())
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        driver = dataSnapshot.getValue( Driver.class );
                        if(driver != null){
                            if(driver.getStatus().equals( Driver.Status.ON_TRIP.name() )
                                    && !TextUtils.isEmpty(driver.getAssignedTrip(TripManager.this.toString()))){
                                getTripAndNotifyStatus();

                            }else{
                                FullStatus fullStatus = new FullStatus();
                                fullStatus.setDriver( driver );
                                notifyListener(fullStatus);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
    }
    private void  getTripAndNotifyStatus(){
        database.getReference(TRIP_REF_PATH).child( driver.getAssignedTrip(TripManager.this.toString()))
                .addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trip = dataSnapshot.getValue(Trip.class);
                FullStatus fullStatus = new FullStatus();
                fullStatus.setDriver( driver );
                fullStatus.setTrip( trip );
                notifyListener(fullStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void  notifyListener(FullStatus fullStatus){
        if(statusCallBack != null){
            statusCallBack.onUpdate(fullStatus);

        }
    }

    public void updateCurrentLocation(double lat, double lng){
        trip.setCurrentLat( lat );
        trip.setGetCurrentLng( lng );
        database.getReference( TRIP_REF_PATH ).child( trip.getId() ).setValue( trip );
    }

    public void updateTripToArrivedToPickUp(){
        trip.setStatus( Trip.Status.GOING_TO_DESTINATION.name());
        database.getReference( TRIP_REF_PATH ).child( trip.getId() ).setValue(trip);
        FullStatus fullStatus = new FullStatus();
        fullStatus.setTrip( trip );
        fullStatus.setDriver( driver );
        notifyListener( fullStatus );

    }

    public void updateTripToArrivedToDestination(){
        trip.setStatus( Trip.Status.ARRIVED.name());
        database.getReference( TRIP_REF_PATH ).child( trip.getId() ).setValue( trip );
        driver.setStatus( Driver.Status.AVAILABLE.name());
        driver.setAssignedTrip( null );
        trip = null;
        database.getReference( DRIVER_REF_PATH ).child( driver.getId()).setValue( driver );

        FullStatus fullStatus = new FullStatus();
        fullStatus.setDriver( driver );
        notifyListener( fullStatus );
    }

    public void goOffLine(){
        driver.setStatus( Driver.Status.OFF_LINE.name());
        driver.setAssignedTrip( null );
        database.getReference( DRIVER_REF_PATH ).child( driver.getId()).setValue( driver );
       // FirebaseAuth.getInstance().signOut();
    }

    public void stopListeningToStatus(){
        if(driverEventListener != null){
            database.getReference(  ).child( driver.getId() ).removeEventListener( driverEventListener );
            statusCallBack = null;
        }
    }
}

