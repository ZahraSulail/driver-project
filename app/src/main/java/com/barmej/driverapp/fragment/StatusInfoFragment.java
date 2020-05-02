package com.barmej.driverapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.barmej.driverapp.R;
import com.barmej.driverapp.callback.DriverActionsDelegates;
import com.barmej.driverapp.domain.entity.Driver;
import com.barmej.driverapp.domain.entity.FullStatus;
import com.barmej.driverapp.domain.entity.Trip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.barmej.driverapp.R.id.text_view_status;

public class StatusInfoFragment extends Fragment {
    private TextView statusTextView;
    private Button arrivedToPickupButton;
    private Button arrivedToDestinationButton;
    private Button goOffLineButton;
    private DriverActionsDelegates driverActionsDelegates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_status_info, container, false );

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        statusTextView = view.findViewById( text_view_status );
        arrivedToPickupButton = view.findViewById( R.id.button_arrived_pickup );
        arrivedToDestinationButton = view.findViewById( R.id.button_arrived_destination );
        goOffLineButton = view.findViewById( R.id.button_logout );

        goOffLineButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverActionsDelegates.goOffLine();

            }
        } );
        arrivedToDestinationButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverActionsDelegates.arrivedToDestination();
            }
        } );
        arrivedToPickupButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverActionsDelegates.arrivedToPickup();
            }
        } );
    }

    public void setDriverActionsDelegates(DriverActionsDelegates driverActionsDelegates){
        this.driverActionsDelegates = driverActionsDelegates;
    }

    public void updateWithStatus(FullStatus fullStatus){
        String driverStatus = fullStatus.getDriver().getStatus();
        if(driverStatus.equals( Driver.Status.AVAILABLE.name() )){
            statusTextView.setText( R.string.available );
            hideAllButtons();
            goOffLineButton.setVisibility( View.VISIBLE );
        }else if(driverStatus.equals( Trip.Status.GOING_TO_PICKUP.name() )){
            String tripStatus = fullStatus.getTrip().getStatus();
            statusTextView.setText( R.string.going_pickup );
            hideAllButtons();
            arrivedToPickupButton.setVisibility( View.VISIBLE );
        }else if(driverStatus.equals( Trip.Status.GOING_TO_DESTINATION.name() )){
            statusTextView.setText( R.string.going_destination );
            hideAllButtons();
           arrivedToDestinationButton.setVisibility( View.VISIBLE );
        }
    }
    private void hideAllButtons(){
        arrivedToDestinationButton.setVisibility( View.GONE );
        arrivedToPickupButton.setVisibility( View.GONE );
        goOffLineButton.setVisibility( View.GONE );
    }
}
