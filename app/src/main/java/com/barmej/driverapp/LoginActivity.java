package com.barmej.driverapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.barmej.driverapp.callback.CallBack;
import com.barmej.driverapp.domain.entity.TripManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button loginButton;

    public static Intent getStartIntent(Context context){
        return new Intent(context, LoginActivity.class );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login);

        emailEditText = findViewById( R.id.edit_text_email );
        passwordEditText = findViewById( R.id.edit_text_password );
        progressBar = findViewById( R.id.progress_bar );
        loginButton = findViewById( R.id.button_login );

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        } );

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            hideForm( true );
            fetchDriverProfileAndLogin( firebaseUser.getUid() );
        }
    }

    private void loginClicked(){
        if(!isValidEmail(emailEditText.getText())){
            emailEditText.setText( R.string.invalid_email);
            return;
        }

        if(passwordEditText.getText().length()< 6){
            passwordEditText.setText( R.string.invalid_password_length );
            return;
        }
        hideForm(true);
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword( emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String driverId = task.getResult().getUser().getUid();
                            fetchDriverProfileAndLogin(driverId);

                        }else{
                            hideForm(false);
                            Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_LONG).show();
                        }
                    }
                } );

         }

         private void hideForm(boolean hide){
          if(hide){
              progressBar.setVisibility( View.VISIBLE );

              emailEditText.setVisibility( View.INVISIBLE );
              passwordEditText.setVisibility( View.INVISIBLE );
              loginButton.setVisibility( View.INVISIBLE );
          }else{
              progressBar.setVisibility( View.INVISIBLE );

              emailEditText.setVisibility( View.VISIBLE);
              passwordEditText.setVisibility( View.VISIBLE );
              loginButton.setVisibility( View.VISIBLE );

          }
         }

         public static boolean isValidEmail(CharSequence target){
           return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher( target ).matches());
         }

         private void fetchDriverProfileAndLogin (String driverId){
             TripManager.getInstance().getDriverProfileAndMarkAvialableIfOffline( driverId, new CallBack() {
                 @Override
                 public void onComplete(boolean isSuccessful) {
                     if(isSuccessful){
                         startActivity( HomeActivity.getStartIntent(LoginActivity.this));
                         finish();
                     }else{
                         hideForm( false );
                         Toast.makeText( LoginActivity.this, R.string.login_error, Toast.LENGTH_LONG ).show();
                     }
                 }
             } );

         }


}
