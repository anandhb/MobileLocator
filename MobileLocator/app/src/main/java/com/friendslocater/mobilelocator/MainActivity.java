package com.friendslocater.mobilelocator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.friendslocater.mobilelocator.BackgroundServices.GPSTracker;
import com.friendslocater.mobilelocator.fragmentsmenu.CameraImportFragment;
import com.friendslocater.mobilelocator.userProfile.userProfileDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private String androidDeviceID;

    public GoogleMap gMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        androidDeviceID = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        authListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {

                Log.d("onAuthStateChanged ", "onAuthStateChanged");

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

            }
        };

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Create group....", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(MainActivity.this, GenerateRandomString.class);
                startActivity(intent);
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Background Service for Location fetching */
        startService(new Intent(MainActivity.this,GPSTracker.class));
    }
    private void getUserData(Map<String,Object> users) {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            phoneNumbers.add((String) singleUser.get("mobileNumber"));

            Log.d("collectPhoneNumbers", " Error User Object first_name = " + singleUser.get("first_name"));
        }

        // Log.d("collectPhoneNumbers"," User Object Size = "+ phoneNumbers);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                Toast.makeText(this, " SIGN OUT ", Toast.LENGTH_SHORT).show();
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            CameraImportFragment cameraImportFragment = new CameraImportFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_camera,cameraImportFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //sign out method
    public void signOut() {
        auth.signOut();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("OnStart ", " Listener Registered");
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("OnStop ", " Listener Removed");
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("onMapReady","Insideeeeeeeeeeeeee AIzaSyCAiU-K2Mm2ohtiNB9zaH-NjHNQeoWllg0");
        gMap = googleMap;
        if (googleMap != null) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            FetchmyLocation fetchLocation = new FetchmyLocation();
            Location lt = fetchLocation.getLocation(this);


            LatLng myLocation = new LatLng(lt.getLatitude(),lt.getLongitude());

            //Move the camera instantly to hamburg with a zoom of 15.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));


            //	mgooglemap.setBuildingsEnabled(true);
        }
        else
        {
            //Toast.makeText(getApplicationContext(), "mgooglemap Null", Toast.LENGTH_SHORT).show();
        }
        fetchDatafromFirebaseDB();

    }

    /* Adding Marker With Given LatLng*/
    public void pointMarker(double bLat, double bLong, String userName, String mobileNumber)
    {

        Log.d("Donor_Fragment","pointMarker() Values = "+ bLat+"-"+bLong+"-"+userName);


        if(gMap == null)
        {
            Log.d("Donor_Fragment","GMAP NULL");
        }else {
            Log.d("Donor_Fragment","GMAP NOT NULL");

            LatLng myLocation = new LatLng(bLat, bLong);
           /* gMap.addMarker(new MarkerOptions()
                    .position(myLocation).title(title)
                    .flat(true));*/
            //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.logout_72)));

            Marker marker = gMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .flat(true)
                    .title(userName)
                    .snippet(mobileNumber)
            );

            marker.showInfoWindow();
        }

    }
    public void fetchDatafromFirebaseDB()
    {

        gMap.clear();

        //Get datasnapshot at your "users" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("MobileLocaterUserProfile");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Get map of users in datasnapshot
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            if (postSnapshot.getKey().equals(androidDeviceID)) {
                            } else {
                                userProfileDetails userData = postSnapshot.getValue(userProfileDetails.class);

                                Log.d("Donor_Fragment","Location = "+ userData.getFirst_name());

                                pointMarker(userData.getLatitude(), userData.getLongitude(), userData.getFirst_name(),userData.getMobileNumber());

                                                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                        Log.e("MainActivity", "Error" + databaseError.getMessage());
                    }
                });
    }

}
