package activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.test1photographerapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.firebase.geofire.util.GeoUtils.distance;

public class MainActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private Button shootNow;
    private Button allProsBtn;
    private Button scheduleShoot;
    private ImageView shoot_orders_history;
    private ImageView main_profile_image_View;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final String msg = "activity.shootNow.shootOrder";

    private FusedLocationProviderClient mfusedLocationProviderClient;

    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    private GoogleMap mMap;
    private LatLngBounds mMapBoundary;
    private Photographer photographer;
    private User user;
    private ArrayList<Photographer> photographers = new ArrayList<>();

    List<Marker> allMapMarkers = new ArrayList<Marker>();


    RecyclerView AllphotographerListrecyclerView;
    proRecyclerViewListDataAdapter proRecyclerViewListDataAdapter;
    ArrayList<proRecyclerViewListModel> imageUrlList;
    ArrayList<proRecyclerViewListModel> tempImageUrlList;

    static FragmentManager proDetailFragmentManager;
    static FragmentManager mainOrderFragmentManager;
    GeoPoint currentUserGeoPoints;

    CardView main_profile_cardView;
    String proDefaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/test1photographer.appspot.com/o/default%2FproDefault.png?alt=media&token=059f8d8f-8a0c-4f9f-829d-73e434de9ac7";
    String userDefaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/test1photographer.appspot.com/o/default%2FuserDefault.png?alt=media&token=0f495f89-caa3-4bcb-b278-97548eb77490";


    //test code for later use
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        shootNow = findViewById(R.id.shootNow);
        scheduleShoot = findViewById(R.id.scheduleShoot);
        shoot_orders_history = findViewById(R.id.shoot_orders_history);
        main_profile_image_View = findViewById(R.id.main_profile_image_View);
        main_profile_cardView = findViewById(R.id.main_profile_cardView);


        //most imp lines below to add a fragment in your app screens
        //for pro details fragment on main screen
        proDetailFragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.pro_profile_Detail_frag_container, new proFullDeatilFrag()).addToBackStack(null).commit();

        //implementation of navigation draw fragment button
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_Map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //////////////////////////////////////////////////////////////////////////
        //Recycler View for photographers , recycler view test 2
        AllphotographerListrecyclerView = (RecyclerView) findViewById(R.id.photographer_list_recycler_view);
        AllphotographerListrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        /////////////////////////////////////////////

        main_profile_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), profile.class));

            }
        });

        shootNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), shootNow.class));
            }
        });
        scheduleShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), scheduleShoot.class));
            }
        });
        shoot_orders_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), shootOrders.class));
            }
        });

    }

    Handler handler1;
    Handler handler2;
    Runnable runnable1;
    Runnable runnable2;
    String checkToLoop;

    @Override
    protected void onResume() {
        super.onResume();

        setCurrentUserImage();

        handler1 = new Handler();
        handler2 = new Handler();
        checkToLoop = null;

        handler1.postDelayed(runnable1 = new Runnable() {
            @Override
            public void run() {
                checkForPendingShoots(firebaseAuth.getUid());
                handler1.postDelayed(this, 10000);
            }
        }, 100);

        handler2.postDelayed(runnable2 = new Runnable() {
            @Override
            public void run() {
                if (imageUrlList != null) {
                    proRecyclerViewListDataAdapter = new proRecyclerViewListDataAdapter(getApplicationContext(), imageUrlList);
                    AllphotographerListrecyclerView.setAdapter(proRecyclerViewListDataAdapter);
                }

                handler2.postDelayed(this, 14000);
            }
        }, 4000);

    }

    @Override
    protected void onPause() {
        super.onPause();

        handler1.removeCallbacks(runnable1);

        handler2.removeCallbacks(runnable2);

    }

    private void checkForPendingShoots(final String currentUserUid) {

        mDb.collection("shoots")
                .whereEqualTo("userUid", currentUserUid)
                .whereIn("shootStatus", Arrays.asList("Shoot Booked", "On the way", "Photoshoot ongoing", "Pictures received"))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().isEmpty()) {
                        noPendingShoots(currentUserUid);
                    }
                    for (final QueryDocumentSnapshot document : task.getResult()) {

                        //  onlineOfflineBtn.setVisibility(View.GONE);
                        String shootStatus = (String) document.get("shootStatus");

                        if (shootStatus.equals("Shoot Booked") && checkToLoop == null) {

                            //user_shoot_order_count_TV.setVisibility(View.VISIBLE);

                            checkToLoop = "notNull";

                            mainOrderFragmentManager = getSupportFragmentManager();
                            mainOrderFragmentManager.popBackStack();

                            mainOrderFragment mainOrderFragment = new mainOrderFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("proName", (String) document.get("proName"));
                            bundle.putString("proUid", (String) document.get("proUid"));

                            bundle.putString("shootHour", (String) document.get("shootHour"));
                            bundle.putString("shootPlan", (String) document.get("shootPlan"));
                            bundle.putString("shootType", (String) document.get("shootType"));
                            bundle.putString("shootStatus", (String) document.get("shootStatus"));

                            bundle.putString("shootPrice", String.valueOf(document.get("shootPrice")));
                            mainOrderFragment.setArguments(bundle);
                            mainOrderFragmentManager.beginTransaction().replace(R.id.pro_profile_Detail_frag_container, mainOrderFragment).addToBackStack(null).commit();

                        } else if (shootStatus.equals("On the way") && checkToLoop == null) {

                            // user_shoot_order_count_TV.setVisibility(View.VISIBLE);

                            checkToLoop = "notNull";

                            mainOrderFragmentManager = getSupportFragmentManager();
                            mainOrderFragmentManager.popBackStack();

                            mainOrderFragment mainOrderFragment = new mainOrderFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("proName", (String) document.get("proName"));
                            bundle.putString("proUid", (String) document.get("proUid"));

                            bundle.putString("shootHour", (String) document.get("shootHour"));
                            bundle.putString("shootPlan", (String) document.get("shootPlan"));
                            bundle.putString("shootType", (String) document.get("shootType"));
                            bundle.putString("shootStatus", (String) document.get("shootStatus"));

                            bundle.putString("shootPrice", String.valueOf(document.get("shootPrice")));
                            mainOrderFragment.setArguments(bundle);
                            mainOrderFragmentManager.beginTransaction().replace(R.id.pro_profile_Detail_frag_container, mainOrderFragment).addToBackStack(null).commit();

                        } else if (shootStatus.equals("Photoshoot ongoing") && checkToLoop == null) {

                            //user_shoot_order_count_TV.setVisibility(View.VISIBLE);

                            checkToLoop = "notNull";

                            mainOrderFragmentManager = getSupportFragmentManager();
                            mainOrderFragmentManager.popBackStack();

                            mainOrderFragment mainOrderFragment = new mainOrderFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("proName", (String) document.get("proName"));
                            bundle.putString("proUid", (String) document.get("proUid"));

                            bundle.putString("shootHour", (String) document.get("shootHour"));
                            bundle.putString("shootPlan", (String) document.get("shootPlan"));
                            bundle.putString("shootType", (String) document.get("shootType"));
                            bundle.putString("shootStatus", (String) document.get("shootStatus"));

                            bundle.putString("shootPrice", String.valueOf(document.get("shootPrice")));
                            mainOrderFragment.setArguments(bundle);
                            mainOrderFragmentManager.beginTransaction().replace(R.id.pro_profile_Detail_frag_container, mainOrderFragment).addToBackStack(null).commit();

                        }


                    }


                }

            }
        });

    }

    private void noPendingShoots(final String currentUserUid) {

        //onlineOfflineBtn.setVisibility(View.VISIBLE);
        mainOrderFragmentManager = getSupportFragmentManager();
        mainOrderFragmentManager.popBackStack();

        mDb.collection("users").document(currentUserUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String proStatus = documentSnapshot.getString("status");

                if (Objects.equals(proStatus, "online")) {

                    imageUrlList = getAllPhotographersFromFirestoreForRecyclerView();

                } else if (Objects.equals(proStatus, "offline")) {


                } else {

                    Map<String, Object> status = new HashMap<>();
                    status.put("status", "online");
                    mDb.collection("users").document(currentUserUid).update(status);

                    imageUrlList = getAllPhotographersFromFirestoreForRecyclerView();

                }
            }
        });
    }

    public static double distanceBetweenTwoCoordinates(double lat1, double lon1, double lat2, double lon2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result in KM
        return (c * r);
    }


    private ArrayList<proRecyclerViewListModel> getAllPhotographersFromFirestoreForRecyclerView() {
        final ArrayList<proRecyclerViewListModel> proRecyclerViewListModelList = new ArrayList<>();
        mDb.collection("photographers").whereEqualTo("status", "online").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //it performs a for loop to get each seperate user details and location
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final Photographer photographer = document.toObject(Photographer.class);

                        mStorageRef.child("images/" + photographer.getPhotographer_id()).child("profilepic.jpg")
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                proRecyclerViewListModel imageUrlll = new proRecyclerViewListModel();
                                imageUrlll.setImageUrl(uri.toString());
                                imageUrlll.setImageName(photographer.getPhotographername());
                                imageUrlll.setUid(photographer.getPhotographer_id());
                                String distanceInKm = String.valueOf(distanceBetweenTwoCoordinates(photographer.getGeo_point().getLatitude(), photographer.getGeo_point().getLongitude(), currentUserGeoPoints.getLatitude(), currentUserGeoPoints.getLongitude()));
                                imageUrlll.setDistance(distanceInKm);
                                proRecyclerViewListModelList.add(imageUrlll);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                proRecyclerViewListModel imageUrlll = new proRecyclerViewListModel();
                                imageUrlll.setImageUrl(proDefaultImageUrl);
                                imageUrlll.setImageName(photographer.getPhotographername());
                                imageUrlll.setUid(photographer.getPhotographer_id());
                                String distanceInKm = String.valueOf(distanceBetweenTwoCoordinates(photographer.getGeo_point().getLatitude(), photographer.getGeo_point().getLongitude(), currentUserGeoPoints.getLatitude(), currentUserGeoPoints.getLongitude()));
                                imageUrlll.setDistance(distanceInKm);
                                proRecyclerViewListModelList.add(imageUrlll);
                            }
                        });
                    }
                }
            }
        });
        //send finaly created arraylist that holds obj of ImageUrlll to adapter
        return proRecyclerViewListModelList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        final LatLng SYDNEY = new LatLng(26.4250256, 74.6403207);
//        final LatLng BRISBANE = new LatLng(26.4150256, 74.6103207);
//        Marker mSydney;
        mMap = googleMap;

        //to set a custom style to map , right now there is normal style set to map
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style);
        mMap.setMapStyle(mapStyleOptions);
        //this setOnMyLocationButtonClickListener is that button which helps to reposition
        //the cureent loaction , it is situated on top right corncer
        //mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        //     mMap.setOnMyLocationClickListener(onMyLocationClickListener);
//      mMap.getUiSettings().setZoomControlsEnabled(true);
        // mMap.setMinZoomPreference(11);
        enableMyLocationIfPermitted();
        //setCameraView is a function that helps in auto setting map view
        //in center position when activity launches
        setCameraView();
        //the best way og adding image to a map marker by directly using vector from galary
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(26.4320256, 74.6223207))
//                .title("Home")
//                .snippet("Home Sweet Home")
//                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_home_black_24dp)));
//        //its is anther trick/way to make beautifull user image like custom map markers ,
        //in this bellow approch , we create a whole new layout xml of marker
//        mMap.addMarker(new MarkerOptions()
//                .position(BRISBANE)
//                .title("Aatmik ARM")
//                .snippet("hello, how are you")
//                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MainActivity.this, R.mipmap.ic_profile, ""))));
        // Set a listener for marker click.
        setUserCurrentLocationOnMap();
        getAllPhotographersFromFirestore();

        ////////////////for testing LatLng
//        LatLng origin = new LatLng(26.4299544,74.6459538);
//        LatLng dest = new LatLng(26.5299544,74.3459538);


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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    //this retrive current user lat lon positions for map to show from firebase
                    Location location = task.getResult();
                    final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    //put 0.1 if you want full city map
                    //put 0.01 if you want zoomed in map
                    double bottomBoundary = geoPoint.getLatitude() - 0.08;
                    double leftBoundary = geoPoint.getLongitude() - 0.08;
                    double topBoundary = geoPoint.getLatitude() + 0.08;
                    double rightBoundary = geoPoint.getLongitude() + 0.08;
                    mMapBoundary = new LatLngBounds(new LatLng(bottomBoundary, leftBoundary),
                            new LatLng(topBoundary, rightBoundary));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
                }
            }
        });
    }


    //step1
    //this function get the user personal details from firestore
    //then save it in a User class object
    //then pass that user class obj to userlocation class
    //and then call setLocationAndUserDetails funtion for further code
    private void setUserCurrentLocationOnMap() {
        if (mMap != null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {

                        Location location = task.getResult();
                        final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        currentUserGeoPoints = geoPoint;
                        final Date timestamp = new Date();
                        final String uid = firebaseAuth.getUid();
                        //updated user location and details on firestore
                        mDb.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                final User user = documentSnapshot.toObject(User.class);
                                //this sets marker with user details like icon name etc


                                StorageReference ref = mStorageRef.child("images/" + uid).child("profilepic.jpg");
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Glide.with(MainActivity.this)
                                                .asBitmap()
                                                .load(uri)
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                                        Bitmap bitmap = getCircularBitmap(resource);

                                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                                .position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                                                                .icon(BitmapDescriptorFactory
                                                                        .fromBitmap(createCustomMarkerForUser
                                                                                (MainActivity.this,
                                                                                        bitmap)))
                                                                .title(user.getUsername())
                                                                .zIndex(1.0f)//this zIndex makes marker to be on top of other markers
                                                                .snippet(user.getEmail()));

                                                        allMapMarkers.add(marker);
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                                    }
                                                });


                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Glide.with(MainActivity.this)
                                                .asBitmap()
                                                .load(userDefaultImageUrl)
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                                        Bitmap bitmap = getCircularBitmap(resource);

                                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                                .position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                                                                .icon(BitmapDescriptorFactory
                                                                        .fromBitmap(createCustomMarkerForUser
                                                                                (MainActivity.this,
                                                                                        bitmap)))
                                                                .title(user.getUsername())
                                                                .zIndex(1.0f)//this zIndex makes marker to be on top of other markers
                                                                .snippet(user.getEmail()));

                                                        allMapMarkers.add(marker);
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                                    }
                                                });
                                    }
                                });


                                // ... get a map.//////////////////////////////////////////////////////////////////////////////////////
                                // Add a thin red line from London to New York.
                                //polyline
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

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
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
    //this funtion bellow helps in retriving/getring all registerd user details and user location
    //which is saved in users locations path in firestore database


    private void getAllPhotographersFromFirestore() {
        //final ArrayList<ImageUrl> imageUrlList = new ArrayList<>();
        //.whereEqualTo("status", "online")
        mDb.collection("photographers").whereEqualTo("status", "online")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //it performs a for loop to get each seperate user details and location
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                final Photographer photographer = document.toObject(Photographer.class);
                                StorageReference ref = mStorageRef.child("images/" + photographer.getPhotographer_id()).child("profilepic.jpg");
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {


                                        Glide.with(MainActivity.this)
                                                .asBitmap()
                                                .load(uri)
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                                        Bitmap bitmap = getCircularBitmap(resource);

                                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                                .position(new LatLng(photographer.getGeo_point().getLatitude(), photographer.getGeo_point().getLongitude()))
                                                                .title(photographer.getPhotographername())
                                                                .snippet(photographer.getEmail())
                                                                .icon(BitmapDescriptorFactory
                                                                        .fromBitmap(createCustomMarkerForPro
                                                                                (MainActivity.this,
                                                                                        bitmap))));

                                                        allMapMarkers.add(marker);
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                                    }
                                                });

                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Glide.with(MainActivity.this)
                                                .asBitmap()
                                                .load(proDefaultImageUrl)
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                                        Bitmap bitmap = getCircularBitmap(resource);

                                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                                .position(new LatLng(photographer.getGeo_point().getLatitude(), photographer.getGeo_point().getLongitude()))
                                                                .icon(BitmapDescriptorFactory
                                                                        .fromBitmap(createCustomMarkerForUser
                                                                                (MainActivity.this,
                                                                                        bitmap)))
                                                                .title(photographer.getPhotographername())
                                                                .snippet(photographer.getEmail()));

                                                        allMapMarkers.add(marker);
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    }
                });
        //send finaly created arraylist that holds obj of ImageUrlll to adapter
//        DataAdapter dataAdapter = new DataAdapter(getApplicationContext(), imageUrlList);
//        AllphotographerListrecyclerView.setAdapter(dataAdapter);
    }

    private void setCurrentUserImage() {
        // Defining the child of storageReference
        String uid = firebaseAuth.getUid();

        ////this code belo is girect code if you just want to directly put image into
        //image view without downloading it
        ///it has less feature and customizations
        StorageReference ref = mStorageRef.child("images/" + uid).child("profilepic.jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageView imageView;
                imageView = findViewById(R.id.main_profile_image_View);
                Glide.with(getApplicationContext()).load(uri).into(imageView);

            }
        });


        ///////////////////////////if yo want to download the image from firebase
        //storage and put it into a drawable file and the put this drawable file into
        //and icon or image view
//        final ImageView[] imageView = new ImageView[1];
//        StorageReference image = mStorageRef.child("images/" + uid).child("profilepic.jpg");
//
//        try {
//            final File localFile = File.createTempFile("images", "jpg");
//
//
//            image.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    String pathName = localFile.getPath();
//                    Drawable d = Drawable.createFromPath(pathName);
//                    //actionBar.setLogo(d);
//                    imageView[0] =findViewById(R.id.main_profile_image_View);
//                    imageView[0].setImageDrawable(d);
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Toast.makeText(MainActivity.this, "OOPS", Toast.LENGTH_LONG).show();
//                }
//            });
//        } catch (IOException e) {
//            Toast.makeText(MainActivity.this, "Realy off", Toast.LENGTH_LONG).show();
//        }
    }


    /////////////////////////////////////////////////////////////////////////////////


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
//                    CircleOptions circleOptions = new CircleOptions();
//                    circleOptions.center(new LatLng(location.getLatitude(),
//                            location.getLongitude()));
//                    circleOptions.radius(200);
//                    circleOptions.fillColor(Color.RED);
//                    circleOptions.strokeWidth(1);
                    //comment out below line if you want to add a red circle around
                    //the current location pointer
                    //mMap.addCircle(circleOptions);
                }
            };

    //this method below is usefull when you want to add a user image on map in
    // a circle fashion with all his/her details
    //.icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MainActivity.this, R.mipmap.ic_launcher_round, "")))
    public Bitmap createCustomMarkerForUser(Context context, Bitmap resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_user_marker_layout, null);
        final CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);

        roundedBitmapDrawable.setCornerRadius(50.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        markerImage.setImageDrawable(roundedBitmapDrawable);
        //markerImage.setImageResource(resource);


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

    public Bitmap createCustomMarkerForPro(Context context, Bitmap resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_pro_marker_layout, null);
        final CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);

        roundedBitmapDrawable.setCornerRadius(50.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        markerImage.setImageDrawable(roundedBitmapDrawable);
        //markerImage.setImageResource(resource);


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

    public static Bitmap createCustomMarkerWithNoDpUser(Context context, @DrawableRes int resource) {

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

    public static Bitmap createCustomMarkerWithNoDpPro(Context context, @DrawableRes int resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_pro_marker_layout, null);
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

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();
        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this, marker.getTitle()
                    + " has been clicked " + clickCount + " times.", Toast.LENGTH_SHORT).show();
        }
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
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

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }


}