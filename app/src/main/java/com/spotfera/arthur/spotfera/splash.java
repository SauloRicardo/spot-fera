package com.spotfera.arthur.spotfera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class splash extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String[] permissions = new String[] {
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CLEAR_APP_CACHE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        Utils.validate(this, 0, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        //autenticacao do spotify
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(getResources().getString(R.string.client_id), AuthenticationResponse.Type.TOKEN, getResources().getString(R.string.redirect_uri));

        builder.setScopes(new String[]{"streaming", "playlist-read-private", "user-read-private"});
        builder.setShowDialog(true);
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Intent intent1 = new Intent(splash.this, MainActivity.class);
            intent1.putExtra("TOKEN_ACCESS", response.getAccessToken());
            startActivity(intent1);
            finish();
        }
        else{
            Intent intent1 = new Intent(splash.this, MainActivity.class);
            intent1.putExtra("TOKEN_ACCESS", "");
            startActivity(intent1);
            finish();
        }
    }
}
