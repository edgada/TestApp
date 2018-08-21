package com.edgaras.testapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static List<FoundedPlace> allPlaces = new ArrayList<>();

    private MapView mapView;
    private GoogleMap gmap;
    private Button btnLookUp;
    private EditText txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLookUp = (Button)findViewById(R.id.btnFind);
        txtSearch = (EditText)findViewById(R.id.search_input);

        btnLookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtSearch.getText().length() > 0)
                {
                    if(DetectConnection.checkInternetConnection(getApplicationContext()))
                    {
                        Boolean success = false;
                        try{
                            success = new getPlacesRequest().execute(txtSearch.getText().toString()).get();

                            if(success == false) Toast.makeText(getApplicationContext(), R.string.toast_error, Toast.LENGTH_SHORT).show();
                            else
                            {
                                txtSearch.setText("");
                                hideKeyboard(MainActivity.this);
                                afterSearch();
                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(), R.string.toast_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else Toast.makeText(getApplicationContext(), R.string.toast_no_internet_error, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getApplicationContext(), R.string.error_empty_word, Toast.LENGTH_SHORT).show();
            }
        });

        Bundle mapViewBundle = null;

        mapView = findViewById(R.id.map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

    }

//map code-------------------------------------------------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setIndoorEnabled(false);
        UiSettings uiSettings = gmap.getUiSettings();

        uiSettings.setMyLocationButtonEnabled(false);

        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setRotateGesturesEnabled(false);

        LatLng vno = new LatLng(54.6872, 25.2797);

        gmap.moveCamera(CameraUpdateFactory.newLatLng(vno));
    }

    public void addMapMarker(LatLng point, int lifespan, String name) {
        MarkerOptions mo = new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(name);

        final Marker m = gmap.addMarker(mo);

        try
        {
            new CountDownTimer(lifespan * 1000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    m.remove();
                }

            }.start();
        }
        catch (Exception e)
        {

        }
    }
//--------------------------------------------------------------------------------------------------------------

    private void afterSearch(){
        if(allPlaces.size() >0)
        {
            for (FoundedPlace fp:allPlaces)
            {
                LatLng coordinates = new LatLng(fp.getLatitude(),fp.getLongitude());
                addMapMarker(coordinates, fp.getLifespan(), fp.getName());
            }
            allPlaces.clear();
        }
        else Toast.makeText(getApplicationContext(), R.string.error_not_found, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    };

    private void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
