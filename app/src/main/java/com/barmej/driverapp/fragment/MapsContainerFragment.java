package com.barmej.driverapp.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.barmej.driverapp.R;
import com.barmej.driverapp.callback.PermissionFailListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MapsContainerFragment extends Fragment implements OnMapReadyCallback {
    public static final int  REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private Marker pickUpMarker;
    private  Marker destinationMrker;
    private PermissionFailListener permissionFailListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_maps, container, true );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        SupportMapFragment mapFragment = ( SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.map );
        if(mapFragment != null){
            mapFragment.getMapAsync( this );
        }
    }
    private void checkLocationPermissinAndSetUpUserLocation(){
        if(ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED){
            setUpUserLocation();

        }else {
            ActivityCompat.requestPermissions( getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION );

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
     mMap = googleMap;
     checkLocationPermissinAndSetUpUserLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if(requestCode ==  REQUEST_LOCATION_PERMISSION){
            if(permissions.length == 1 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                setUpUserLocation();
            }else{
                if(permissionFailListener != null){
                    permissionFailListener.onPermissionFail();
                }
            }

        }else{
            super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        }
    }
    private void setUpUserLocation(){
        if(mMap != null)
            return;
            mMap.setMyLocationEnabled( true );
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient( getActivity() );
        locationClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
             if(location != null){
                 LatLng currentLatlng = new LatLng( location.getLatitude(), location.getLongitude());
                 CameraUpdate update = CameraUpdateFactory.newLatLngZoom( currentLatlng, 16 );
                 mMap.moveCamera( update );
             }
            }
        } );

    }
    public void removeMapLocationLayout(){
        if(ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled( false );
        }
    }
   public LatLng captureCenter(){
    if(mMap == null) return null;

    return mMap.getCameraPosition().target;
   }
   public void setPickUpMarker(LatLng target){
        if(mMap != null) return;;
        if(pickUpMarker == null){
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.pickup );
            MarkerOptions options = new MarkerOptions();
            options.icon( descriptor );
            options.position( target);
            pickUpMarker = mMap.addMarker( options );
        }else {
            pickUpMarker.setPosition( target );
        }


   }
   public void setDestinationMarker(LatLng target){
       if(mMap != null) return;;
       if(destinationMrker == null){
           BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource( R.drawable.destination );
           MarkerOptions options = new MarkerOptions();
           options.icon( descriptor );
           options.position( target);
           destinationMrker = mMap.addMarker( options );
       }else {
           destinationMrker.setPosition( target );
       }

   }

   public void reset(){
        if(mMap != null) return;;
        mMap.clear();
        pickUpMarker = null;
        destinationMrker = null;
        setUpUserLocation();
   }
   public void showDriverCurrentLocationOnMap(LatLng driverLating){
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom( driverLating, 16 );
        mMap.moveCamera( update );

   }

   public void setPermissionFailListener(PermissionFailListener permissionFailListener){
        this.permissionFailListener = permissionFailListener;
   }
}
