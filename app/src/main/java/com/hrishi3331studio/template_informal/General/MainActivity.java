package com.hrishi3331studio.template_informal.General;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import com.hrishi3331studio.template_informal.Dialogs.LoaderDialog;
import com.hrishi3331studio.template_informal.Notifications.Notifications;
import com.hrishi3331studio.template_informal.R;
import com.hrishi3331studio.template_informal.Support.ContactUs;
import com.hrishi3331studio.template_informal.Support.Support;
import com.hrishi3331studio.template_informal.User.Profile;
import com.hrishi3331studio.template_informal.UserAuth.SignUp;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private ActionBarDrawerToggle mToggle;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private LoaderDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit_dialog.dismiss();
            }
        });

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
}