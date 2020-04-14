package com.hrishi3331studio.gotaxi.General;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hrishi3331studio.gotaxi.Dialogs.LoaderDialog;
import com.hrishi3331studio.gotaxi.Notifications.Notifications;
import com.hrishi3331studio.gotaxi.R;
import com.hrishi3331studio.gotaxi.Support.ContactUs;
import com.hrishi3331studio.gotaxi.Support.Support;
import com.hrishi3331studio.gotaxi.User.Profile;
import com.hrishi3331studio.gotaxi.UserAuth.SignUp;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.navigation.v5.milestone.Milestone;
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, MapboxMap.OnMapClickListener{

    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private ActionBarDrawerToggle mToggle;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private LoaderDialog dialog;

    private MapView mapView;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine engine;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private Location origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mapbox.getInstance(MainActivity.this, getString(R.string.access_token));

        mapView = (MapView)findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        MilestoneEventListener listener = new MilestoneEventListener() {
            @Override
            public void onMilestoneEvent(RouteProgress routeProgress, String instruction, Milestone milestone) {

            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My App");
        mDrawer = (DrawerLayout) findViewById(R.id.mDrawer);
        mNavigation = (NavigationView) findViewById(R.id.mNavigation);
        mToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(MainActivity.this, SignUp.class));
                    finish();
                }
            }
        });

        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.contact:
                        startActivity(new Intent(MainActivity.this, ContactUs.class));
                        break;

                    case R.id.support:
                        startActivity(new Intent(MainActivity.this, Support.class));
                        break;

                    case R.id.profile:
                        startActivity(new Intent(MainActivity.this, Profile.class));
                        break;

                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, SignUp.class));
                        finish();
                        break;

                    default:
                        break;
                }
                mDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        View header = mNavigation.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.user_name);
        TextView email = (TextView) header.findViewById(R.id.header_email);
        RoundedImageView image = (RoundedImageView)header.findViewById(R.id.user_header_image);

        if (mUser != null) {
            email.setText(mUser.getEmail());
            name.setText(mUser.getDisplayName());
            try{
                Picasso.get().load(mUser.getPhotoUrl()).into(image);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        dialog = new LoaderDialog(MainActivity.this);
        checkConnectivity();

    }

    private void checkConnectivity() {
        if(!isNetworkAvailable(MainActivity.this)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View DialogLayout = inflater.inflate(R.layout.connection_dialog, null);
            builder.setView(DialogLayout);

            Button ok = (Button) DialogLayout.findViewById(R.id.btn_ok);
            Button cancel = (Button) DialogLayout.findViewById(R.id.btn_cancel);

            final android.app.AlertDialog exit_dialog = builder.create();

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            exit_dialog.show();
        }
        else {
            //dialog.showLoader();
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.exit_dialog, null);
        builder.setView(DialogLayout);

        Button ok = (Button) DialogLayout.findViewById(R.id.btn_ok);
        Button cancel = (Button) DialogLayout.findViewById(R.id.btn_cancel);

        final android.app.AlertDialog exit_dialog = builder.create();

        ok.setOnClickListener(v -> finish());

        cancel.setOnClickListener(v -> exit_dialog.dismiss());

        exit_dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }

        if (id == R.id.notifications){
            startActivity(new Intent(MainActivity.this, Notifications.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    public void startLocation(){
        if (PermissionsManager.areLocationPermissionsGranted(MainActivity.this)){
            engine = new LocationEngineProvider(MainActivity.this).obtainBestLocationEngineAvailable();
            engine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
            engine.activate();

            Location last_location = engine.getLastLocation();
            if (last_location != null){
                origin = last_location;
                setCameraPosition(origin);
            }else {
                engine.addLocationEngineListener(this);
            }

            locationLayerPlugin = new LocationLayerPlugin(mapView, mapboxMap, engine);
            locationLayerPlugin.setLocationLayerEnabled(true);
            locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
            locationLayerPlugin.setRenderMode(RenderMode.NORMAL);

        }
        else {
            permissionsManager = new PermissionsManager(MainActivity.this);
            permissionsManager.requestLocationPermissions(MainActivity.this);
        }
    }

    public void setCameraPosition(Location location){

        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(),16));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        engine.requestLocationUpdates();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            origin = location;
            setCameraPosition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted){
            startLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (engine != null){
            engine.deactivate();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setOnMapClickListener(this);


    }
}