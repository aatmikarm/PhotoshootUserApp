package activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.test1photographerapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class proOnJob extends AppCompatActivity implements OnMapReadyCallback {

    User currentUser;
    private Button makePaymentBtn;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;

    String selectedProName, selectedProPic, selectedProUid, selectedProShootBookedTime, selectedProPhone, selectedProDistance, selectedProShootStatus;
    ImageView proOnJob_profile_img, proOnJob_backBtn;
    TextView proOnJob_proName, arrival_time, proDistance, order_placed_time, on_the_way, photoshoot_start_time, pics_recived_time;
    CardView callBtn, shootStatusIndicator_shootBooked, shootStatusIndicator_onTheWay, shootStatusIndicator_photoshootOngoing, shootStatusIndicator_picturesReceived;
    TextView shootStatusIndicator_shootBooked_TV, shootStatusIndicator_onTheWay_TV, shootStatusIndicator_photoshootOngoing_TV, shootStatusIndicator_picturesReceived_TV;


    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private LatLngBounds mMapBoundary;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pro_on_job);

        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        makePaymentBtn = findViewById(R.id.ratingBtn);
        callBtn = findViewById(R.id.proOnJob_callBtn);
        proOnJob_profile_img = findViewById(R.id.proOnJob_profile_img);
        proOnJob_proName = findViewById(R.id.proOrder_proStatus);
        proDistance = findViewById(R.id.proDistance);
        arrival_time = findViewById(R.id.arrival_time);
        order_placed_time = findViewById(R.id.order_placed_time);
        on_the_way = findViewById(R.id.on_the_way);
        photoshoot_start_time = findViewById(R.id.photoshoot_start_time);
        pics_recived_time = findViewById(R.id.pics_recived_time);
        proOnJob_backBtn = findViewById(R.id.proOnJob_backBtn);

        //shoot status indicatores text and cardviews
        shootStatusIndicator_shootBooked_TV = findViewById(R.id.shootStatusIndicator_shootBooked_TV);
        shootStatusIndicator_onTheWay_TV = findViewById(R.id.shootStatusIndicator_onTheWay_TV);
        shootStatusIndicator_photoshootOngoing_TV = findViewById(R.id.shootStatusIndicator_photoshootOngoing_TV);
        shootStatusIndicator_picturesReceived_TV = findViewById(R.id.shootStatusIndicator_picturesReceived_TV);

        shootStatusIndicator_shootBooked = findViewById(R.id.shootStatusIndicator_shootBooked);
        shootStatusIndicator_onTheWay = findViewById(R.id.shootStatusIndicator_onTheWay);
        shootStatusIndicator_photoshootOngoing = findViewById(R.id.shootStatusIndicator_photoshootOngoing);
        shootStatusIndicator_picturesReceived = findViewById(R.id.shootStatusIndicator_picturesReceived);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.paymentScreen);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (getIntent().getExtras() != null) {
            this.selectedProName = (String) getIntent().getExtras().get("selectedProName");
            this.selectedProPic = (String) getIntent().getExtras().get("selectedProPic");
            this.selectedProUid = (String) getIntent().getExtras().get("selectedProUid");
            this.selectedProShootBookedTime = (String) getIntent().getExtras().get("selectedProShootBookedTime");
            this.selectedProDistance = (String) getIntent().getExtras().get("selectedProDistance");
            this.selectedProShootStatus = (String) getIntent().getExtras().get("selectedProShootStatus");
            //Toast.makeText(proOnJob.this, "booking time is before = "+selectedProShootBookedTime, Toast.LENGTH_SHORT).show();

        }

        getProShootAllTimes();
        setOnScreenProDetails();
        getProPhoneNumber();

        proOnJob_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(proOnJob.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", selectedProPhone, null));
                startActivity(intent);
            }
        });

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(proOnJob.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    private void setOnScreenProDetails() {
        Glide.with(getApplicationContext()).load(selectedProPic).into(proOnJob_profile_img);
        proOnJob_proName.setText(selectedProName);


        switch (selectedProShootStatus) {

            case "Shoot Booked":
                shootStatusIndicator_shootBooked_TV.setTextColor(Color.parseColor("#4CAF50"));
                shootStatusIndicator_shootBooked.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                order_placed_time.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "On the way":
                shootStatusIndicator_onTheWay_TV.setTextColor(Color.parseColor("#4CAF50"));
                shootStatusIndicator_onTheWay.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                on_the_way.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Photoshoot ongoing":
                shootStatusIndicator_photoshootOngoing_TV.setTextColor(Color.parseColor("#4CAF50"));
                shootStatusIndicator_photoshootOngoing.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                photoshoot_start_time.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Pictures received":
                shootStatusIndicator_picturesReceived_TV.setTextColor(Color.parseColor("#4CAF50"));
                shootStatusIndicator_picturesReceived.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                pics_recived_time.setTextColor(Color.parseColor("#4CAF50"));
                break;

        }


        double distanceInKilometers = Double.parseDouble(selectedProDistance);
        //if distance is less then 1 km then convert to meters
        if (distanceInKilometers < 1) {
            DecimalFormat precision = new DecimalFormat("0");
            // dblVariable is a number variable and not a String in this case
            proDistance.setText(precision.format(distanceInKilometers * 1000) + " m");
            //Toast.makeText(proOnJob.this, "distance in meters = "+precision.format(distanceInKilometers*1000)+" m", Toast.LENGTH_SHORT).show();

        } else if (distanceInKilometers >= 1) {
            DecimalFormat precision = new DecimalFormat("0.00");
            // dblVariable is a number variable and not a String in this case
            proDistance.setText(precision.format(distanceInKilometers) + " km");
        }
    }

    private void getProPhoneNumber() {
        // to get pro phone number
        mDb.collection("photographers")
                .document(selectedProUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                selectedProPhone = document.get("phone").toString();
                // Toast.makeText(proOnJob.this, "phone of pro = "+selectedProPhone, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProShootAllTimes() {

        mDb.collection("shoots").document(selectedProShootBookedTime).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                String shootBookedTime = document.get("shootBookedTime").toString();
                String proOnTheWay = document.get("proOnTheWayTime").toString();
                String photoshootStartTime = document.get("photoshootStartTime").toString();
                String picturesReceivedTime = document.get("picturesReceivedTime").toString();
                //String shootBookedTime = converTimeToTimeFormate(time);

                arrival_time.setText(photoshootStartTime);
                order_placed_time.setText(shootBookedTime);
                on_the_way.setText(proOnTheWay);
                photoshoot_start_time.setText(photoshootStartTime);
                pics_recived_time.setText(picturesReceivedTime);

                // Toast.makeText(proOnJob.this, "time of booking = "+time, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocationIfPermitted();
        setCameraView();

        setUserCurrentLocationOnMap();
        getSelectedPhotographerFromFirestore();

    }


    //step1
    //this function get the user personal details from firestore
    //then save it in a User class object
    //then pass that user class obj to userlocation class
    //and then call setLocationAndUserDetails funtion for further code
    private void setUserCurrentLocationOnMap() {
        if (mMap != null) {

            mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {

                        Location location = task.getResult();
                        final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        final Date timestamp = new Date();
                        final String uid = firebaseAuth.getUid();
                        //updated user location and details on firestore
                        mDb.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                currentUser = user;
                                //this sets marker with user details like icon name etc
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(proOnJob.this, R.mipmap.ic_profile)))
                                        .title(user.getUsername())
                                        .zIndex(1.0f)//this zIndex makes marker to be on top of other markers
                                        .snippet(user.getEmail()));
                                // ... get a map.
                                // Add a thin red line from London to New York.
//                                Polyline line = mMap.addPolyline(new PolylineOptions()
//                                        .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
//                                        .width(5)
//                                        .color(Color.RED));


                            }
                        });

                        updateLocationAndTimeInFirestore(geoPoint, timestamp);
                    }
                }
            });
        }

    }


    private void updateLocationAndTimeInFirestore(GeoPoint geoPoint, Date timestamp) {
        final String uid = firebaseAuth.getUid();
        Map<String, Object> locationAndTime = new HashMap<>();
        locationAndTime.put("geo_point", geoPoint);
        locationAndTime.put("timestamp", timestamp);
        //note this set commont in firestore overright exiting user data
        // set() delet data and write new user data
        //whereas update only change exixting data and dont delet everything
        mDb.collection("users").document(uid).update(locationAndTime);

    }


    private void getSelectedPhotographerFromFirestore() {

        mDb.collection("photographers").document(this.selectedProUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Photographer photographer = documentSnapshot.toObject(Photographer.class);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(photographer.getGeo_point().getLatitude(), photographer.getGeo_point().getLongitude()))
                        .title(photographer.getPhotographername())
                        .snippet(photographer.getEmail())
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(createCustomMarker
                                        (proOnJob.this,
                                                R.mipmap.ic_pro_round))));
            }
        });
    }


    //////////////////////////////////////////////////////////////////////////////////////////

    //this method below is usefull when you want to add a user image on map in
    // a circle fashion with all his/her details
    //.icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MainActivity.this, R.mipmap.ic_launcher_round, "")))
    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_user_marker_layout, null);
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        //TextView txt_name = (TextView) marker.findViewById(R.id.name);
        //txt_name.setText(_name);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(),
                marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }


    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    //this function below help in centering the map view to current user
    //location automatically whn activity launches
    private void setCameraView() {
        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    //this retrive current user lat lon positions for map to show from firebase
                    Location location = task.getResult();
                    final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    //put 0.1 if you want full city map
                    //put 0.01 if you want zoomed in map
                    double bottomBoundary = geoPoint.getLatitude() - 0.1;
                    double leftBoundary = geoPoint.getLongitude() - 0.1;
                    double topBoundary = geoPoint.getLatitude() + 0.1;
                    double rightBoundary = geoPoint.getLongitude() + 0.1;
                    mMapBoundary = new LatLngBounds(new LatLng(bottomBoundary, leftBoundary),
                            new LatLng(topBoundary, rightBoundary));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
                }
            }
        });
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocationIfPermitted();
            } else {
                showDefaultLocation();
            }
        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(15);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {

                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    mMap.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    mMap.addCircle(circleOptions);
                }
            };


}
