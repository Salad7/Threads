package com.example.msalad.threads;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.msalad.threads.Register.RegisterActivity;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.loading.newton.NewtonCradleLoading;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataApi mGeoDataClient;
    private PlaceDetectionApi mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;


    // not granted.
    //private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private DatabaseReference threadRef;
    public ArrayList<LatLon> coors_near_me;
    private String threadCode;
    private String MY_PREFS_NAME = "MY_PREFS_NAME";
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    boolean isFound = false;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private  FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        threadRef = database.getReference();
        avLoadingIndicatorView = findViewById(R.id.avi);
        avLoadingIndicatorView.show();
        //Intent i = new Intent(this,RegisterActivity.class);
        //startActivity(i);
        if (getIntent().getExtras() != null) {
            threadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String tc = getIntent().getStringExtra("threadCode");
                    String topicPosition = getIntent().getStringExtra("topicPosition");
                    String threadTitle = getIntent().getStringExtra("threadTitle");
                    Topics topic = new Topics();
                    DataSnapshot specificThreadPath = dataSnapshot.child("Threads").child(tc).child("topics").child(topicPosition);
                    topic.setTopicTitle(specificThreadPath.child("topicTitle").getValue(String.class));
                    topic.setAnonCode((Map) specificThreadPath.child("anonCode").getValue());
                    topic.setTimeStamp(specificThreadPath.child("timeStamp").getValue(Integer.class));
                    topic.setHostUID(specificThreadPath.child("UID").getValue(String.class));
                    topic.setPosition(Integer.parseInt(topicPosition));
                    topic.setUpvoters((ArrayList) specificThreadPath.child("upvoters").getValue());
                    topic.setParent(specificThreadPath.child("parent").getValue(String.class));
                    topic.setReplies(specificThreadPath.child("replies").getValue(Integer.class));
                    topic.setTopicInvite(specificThreadPath.child("topicInvite").getValue(String.class));
                    topic.setNotifyList((ArrayList) specificThreadPath.child("notifyList").getValue());
                    if (specificThreadPath.child("messages").exists()) {
                        topic.setMessages((ArrayList) specificThreadPath.child("messages").getValue());
                    }
                    Log.d("MainActivity Topic","threadCode "+tc+" Topic position "+topicPosition);
                    Post p = new Post();
                    p.setUpvoters(topic.getUpvoters());
                    p.setParent(topic.getParent());
                    p.setPosition(topic.getPosition());
                    p.setAnonCode(topic.getAnonCode());
                    p.setHostUID(topic.getHostUID());
                    p.setMessages(topic.getMessages());
                    p.setReplies(topic.getReplies());
                    p.setTimeStamp(topic.getTimeStamp());
                    p.setTopicTitle(topic.getTopicTitle());
                    p.setTopicInvite(topic.getTopicInvite());
                    Intent intent = new Intent(MainActivity.this, PostActivity.class);
                    intent.putExtra("post", p);
                    intent.putExtra("tt", threadTitle);
                    intent.putExtra("threadCode",tc);
                    intent.putExtra("notifyList", topic.getNotifyList());
                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map11);
        //if (mapFragment != null) {
        mapFragment.getMapAsync(this);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.GeoDataApi;
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.PlaceDetectionApi;
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



    }

    private void startThread(int i, String testKey, DataSnapshot dataSnapshot){
        Log.d("MainActivity "," called startThread");
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("threadCode", testKey);
        editor.apply();
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if(!dataSnapshot.child("Anons").child(androidId).exists()){
            HashMap map = new HashMap();
            map.put("threadCode",testKey);
            String threadName = dataSnapshot.child("Threads").child(testKey).child("threadTitle").getValue(String.class);
            map.put("threadName",threadName);
            map.put("timeStamp",ThreadFinder.getTimeStamp());
            threadRef.child("Anons").child(androidId).child(0+"").updateChildren(map);
        }
        else{
            int openAnon = -1;
            boolean isAnonExistant = false;
            for(int x = 0; x < ThreadFinder.MAX_SETTINGS_THREAD; x++){
                //If the thread exists
                //Check if that threads code matches testKey
                //If we find a match, dont add and start.
                //If we get to the end and dont find match, add the threadCode and continue
                if(dataSnapshot.child("Anons").child(androidId).child(x+"").exists()){
                    if(dataSnapshot.child("Anons").child(androidId).child(x+"").child("threadCode").getValue(String.class).equals(testKey)){
                        Intent intent = new Intent(MainActivity.this, ThreadActivity.class);
                        startActivity(intent);
                        x = ThreadFinder.MAX_SETTINGS_THREAD+1;
                        isFound = true;
                        isAnonExistant = true;
                    }

                }
                if(openAnon < 0){
                    openAnon = i;
                }
            }
            if(isAnonExistant == false) {
                HashMap map = new HashMap();
                map.put("threadCode", testKey);
                String threadName = dataSnapshot.child("Threads").child(testKey).child("threadTitle").getValue(String.class);
                map.put("threadName", threadName);
                map.put("timeStamp", ThreadFinder.getTimeStamp());
                threadRef.child("Anons").child(androidId).child(openAnon + "").updateChildren(map);
                Intent intent = new Intent(MainActivity.this, ThreadActivity.class);
                startActivity(intent);
                isFound = true;
            }

        }

    }


    private void createThread(){
        Double lat = convertDouble(mLastKnownLocation.getLatitude());
        Double lon =  convertDouble(mLastKnownLocation.getLongitude());
        String[] pos1 = (lat + "").split("\\.");
        // Log.d("MainActivityCrash ", "lat " + lat + " lon " + lon + " pos1 " + pos1.length);
        String[] pos2 = (lon + "").split("\\.");
        String pureKey = pos1[0] + "!" + pos1[1] + "*" + pos2[0] + "!" + pos2[1];
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("threadCode", pureKey);
        editor.apply();
        Intent intent = new Intent(MainActivity.this, CreateThread.class);
       // startActivity(intent);
        //Log.d("should CreateThread", "SS");
    }
    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Intent i = new Intent(this,MainActivity.class);
                   // startActivity(i);
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 17));
                        } else {
                        }
                        //Toast.makeText(getApplicationContext(),mLastKnownLocation.getLongitude() + " " + mLastKnownLocation.getLatitude(),Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity ",mLastKnownLocation.getLatitude()+" "+mLastKnownLocation.getLongitude());
                        coors_near_me =  ThreadFinder.runSimulationQuad2(convertDouble(mLastKnownLocation.getLatitude())-.00005,convertDouble(mLastKnownLocation.getLongitude())-.00005,MainActivity.this);
                        Toast.makeText(MainActivity.this,"Size of coors near me = "+coors_near_me.size(), Toast.LENGTH_SHORT ).show();
                        PolylineOptions rectOptions = new PolylineOptions();
                        rectOptions.color(Color.parseColor("#448aff"));
                        //for(int n = 0; n < coors_near_me.size(); n++){
                            // Instantiates a new Polyline object and adds points to define a rectangle
                            rectOptions.add(new LatLng(coors_near_me.get(0).lat, coors_near_me.get(0).lon));
                            rectOptions.add(new LatLng(coors_near_me.get(0).lat, coors_near_me.get(120).lon));
                            rectOptions.add(new LatLng(coors_near_me.get(120).lat, coors_near_me.get(120).lon));
                            rectOptions.add(new LatLng(coors_near_me.get(120).lat, coors_near_me.get(0).lon));
                            rectOptions.add(new LatLng(coors_near_me.get(0).lat, coors_near_me.get(0).lon));
                        //}
                        Polyline polyline = mMap.addPolyline(rectOptions);


                    }
                });

            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
        mMap.setMinZoomPreference(17);
        //new ThreadAsyncTask().execute();
        threadRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object[] keys = ((HashMap) dataSnapshot.child("Threads").getValue()).keySet().toArray();
                String[] decoder = new String[keys.length];
                for(int i = 0; i < decoder.length; i++){
                    String k = (keys[i].toString().replace("!",".")).replace("*","d");
                    decoder[i] = k;
                    Log.d("kvalue",k);
                    //int indexOfBar = k.indexOf('|');
                    String parts[] =  k.split("d");
                    Log.d("split",parts[0]+"ad dasd");
                    Double lat2 = Double.parseDouble(k.split("d")[0]);
                    Double lon2 = Double.parseDouble(k.split("d")[1]);
                    Double d = distance(mLastKnownLocation.getLatitude(),lat2,mLastKnownLocation.getLongitude(),lon2,0.0,0.0);
                    Log.d("Distance from me",d+"");
                    Toast.makeText(MainActivity.this," Distance from thread "+d,Toast.LENGTH_LONG).show();
                    if(d < 25){
                        //Log.d("Found thread, ", )
                        Double lat = lat2;
                        Double lon = lon2;
                        String[] pos1 = (lat + "").split("\\.");
                        String[] pos2 = (lon + "").split("\\.");
                        String testKey = pos1[0] + "!" + pos1[1] + "*" + pos2[0] + "!" + pos2[1];
                            startThread(i,testKey,dataSnapshot);
                            i = decoder.length;
                            isFound = true;
                    }
                }
                Log.d("MainActivity keys",((HashMap) dataSnapshot.child("Threads").getValue()).keySet().toArray()+"");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public Double convertDouble(Double l){
        String la = l+"";
        String la2 = "";
        //Log.d("MainActivity ", "convertDouble before "+l);
        int index = la.indexOf('.');

        for(int i = 0; i < index+6; i++){
            la2+=la.charAt(i);
        }
        Log.d("MainActivity ", "convertDouble after "+la2);
        return Double.parseDouble(la2);



    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


    public class ThreadAsyncTask extends AsyncTask<Void,Void,Void>{



        @Override
        protected Void doInBackground(Void... voids) {

            searchIfThreadExistsInFirebase();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public void searchIfThreadExistsInFirebase(){


            threadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(int i = 0; i < coors_near_me.size(); i++) {
                        Double lat = coors_near_me.get(i).lat;
                        Double lon = coors_near_me.get(i).lon;
                        String[] pos1 = (lat + "").split("\\.");
                        String[] pos2 = (lon + "").split("\\.");
                        String testKey = pos1[0] + "!" + pos1[1] + "*" + pos2[0] + "!" + pos2[1];
                        if (dataSnapshot.child("Threads").child(testKey).exists()) {
                            startThread(i,testKey,dataSnapshot);
                            i = coors_near_me.size()+1;
                            isFound = true;
                        }
                    }
                    if (!isFound) {
                        Log.d("MainActivity"," createThread called");
                        createThread();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
