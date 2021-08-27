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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class shootNow extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final String msg = "activity.shootNow.shootOrder";


    private Button confirmShoot;

    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private LatLngBounds mMapBoundary;
    private StorageReference mStorageRef;

    private CardView smartphone, value, professional, premium;
    private CardView portraits, modelling, socialMedia, events, birthday, wedding, food, baby;
    private CardView hr1, hr2, hr3, hr4, hr5;

    String shootProName, shootPlan, shootType, shootHour;
    String selectedProName, selectedProPic, selectedProUid, selectedProShootBookedTimeKey, selectedProDistance;
    String dateandtimepattern = "ssmmHHddMMyyyy";

    String shootBookedTime, proOnTheWayTime, photoshootStartTime, picturesReceivedTime, selectedProShootStatus = "Shoot Booked";

    User currentUser;
    int pricePlan = 0;
    //int priceType=0;
    int priceHour = 0;
    int priceTotal = 0;
    double distanceInKilometersDouble;
    double timeOfRideInMinutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoot_now);

        smartphone = findViewById(R.id.smartphone);
        value = findViewById(R.id.Value);
        professional = findViewById(R.id.Professional);
        premium = findViewById(R.id.Premium);

        portraits = findViewById(R.id.Portraits);
        modelling = findViewById(R.id.Modelling);
        socialMedia = findViewById(R.id.SocialMedia);
        events = findViewById(R.id.Events);
        birthday = findViewById(R.id.Birthaday);
        wedding = findViewById(R.id.Wedding);
        food = findViewById(R.id.Food);
        baby = findViewById(R.id.Baby);

        hr1 = findViewById(R.id.hr1);
        hr2 = findViewById(R.id.hr2);
        hr3 = findViewById(R.id.hr3);
        hr4 = findViewById(R.id.hr4);
        hr5 = findViewById(R.id.hr5);

        confirmShoot = findViewById(R.id.confirmShoot);

        if (getIntent().getExtras() != null) {
            this.selectedProName = (String) getIntent().getExtras().get("selectedProName");
            this.selectedProPic = (String) getIntent().getExtras().get("selectedProPic");
            this.selectedProUid = (String) getIntent().getExtras().get("selectedProUid");
            this.selectedProDistance = (String) getIntent().getExtras().get("selectedProDistance");
        }

        distanceInKilometersDouble = Double.parseDouble(selectedProDistance);

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.shootNow);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        smartphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootPlan = "smartphone";
                smartphone.setCardBackgroundColor(Color.GRAY);

                pricePlan = 0;
                pricePlan = 50;

                if (priceHour == 0) {
                    confirmShoot.setText("confirm " + pricePlan + "Rs");
                } else if (priceHour != 0) {
                    int ppph = pricePlan + priceHour;
                    confirmShoot.setText("confirm " + ppph + "Rs");
                }


                value.setCardBackgroundColor(Color.WHITE);
                value.setCardElevation(35);

                professional.setCardBackgroundColor(Color.WHITE);
                professional.setCardElevation(35);

                premium.setCardBackgroundColor(Color.WHITE);
                premium.setCardElevation(35);

            }
        });

        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootPlan = "value";
                value.setCardBackgroundColor(Color.GRAY);

                pricePlan = 0;
                pricePlan = 80;

                if (priceHour == 0) {
                    confirmShoot.setText("confirm " + pricePlan + "Rs");
                } else if (priceHour != 0) {
                    int ppph = pricePlan + priceHour;
                    confirmShoot.setText("confirm " + ppph + "Rs");
                }

                smartphone.setCardBackgroundColor(Color.WHITE);
                smartphone.setCardElevation(35);

                professional.setCardBackgroundColor(Color.WHITE);
                professional.setCardElevation(35);

                premium.setCardBackgroundColor(Color.WHITE);
                premium.setCardElevation(35);
            }
        });
        professional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootPlan = "professional";
                professional.setCardBackgroundColor(Color.GRAY);

                pricePlan = 0;
                pricePlan = 120;

                if (priceHour == 0) {
                    confirmShoot.setText("confirm " + pricePlan + "Rs");
                } else if (priceHour != 0) {
                    int ppph = pricePlan + priceHour;
                    confirmShoot.setText("confirm " + ppph + "Rs");
                }

                smartphone.setCardBackgroundColor(Color.WHITE);
                smartphone.setCardElevation(35);

                value.setCardBackgroundColor(Color.WHITE);
                value.setCardElevation(35);

                premium.setCardBackgroundColor(Color.WHITE);
                premium.setCardElevation(35);
            }
        });
        premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootPlan = "premium";
                premium.setCardBackgroundColor(Color.GRAY);

                pricePlan = 0;
                pricePlan = 320;

                if (priceHour == 0) {
                    confirmShoot.setText("confirm " + pricePlan + "Rs");
                } else if (priceHour != 0) {
                    int ppph = pricePlan + priceHour;
                    confirmShoot.setText("confirm " + ppph + "Rs");
                }

                smartphone.setCardBackgroundColor(Color.WHITE);
                smartphone.setCardElevation(35);

                value.setCardBackgroundColor(Color.WHITE);
                value.setCardElevation(35);

                professional.setCardBackgroundColor(Color.WHITE);
                professional.setCardElevation(35);
            }
        });

        portraits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootType = "portraits";
                portraits.setCardBackgroundColor(Color.GRAY);

                modelling.setCardBackgroundColor(Color.WHITE);
                modelling.setCardElevation(35);

                socialMedia.setCardBackgroundColor(Color.WHITE);
                socialMedia.setCardElevation(35);

                events.setCardBackgroundColor(Color.WHITE);
                events.setCardElevation(35);

                birthday.setCardBackgroundColor(Color.WHITE);
                birthday.setCardElevation(35);

                wedding.setCardBackgroundColor(Color.WHITE);
                wedding.setCardElevation(35);

                food.setCardBackgroundColor(Color.WHITE);
                food.setCardElevation(35);

                baby.setCardBackgroundColor(Color.WHITE);
                baby.setCardElevation(35);

            }
        });
        modelling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootType = "modelling";
                modelling.setCardBackgroundColor(Color.GRAY);

                portraits.setCardBackgroundColor(Color.WHITE);
                portraits.setCardElevation(35);

                socialMedia.setCardBackgroundColor(Color.WHITE);
                socialMedia.setCardElevation(35);

                events.setCardBackgroundColor(Color.WHITE);
                events.setCardElevation(35);

                birthday.setCardBackgroundColor(Color.WHITE);
                birthday.setCardElevation(35);

                wedding.setCardBackgroundColor(Color.WHITE);
                wedding.setCardElevation(35);

                food.setCardBackgroundColor(Color.WHITE);
                food.setCardElevation(35);

                baby.setCardBackgroundColor(Color.WHITE);
                baby.setCardElevation(35);
            }
        });
        socialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootType = "socialMedia";
                socialMedia.setCardBackgroundColor(Color.GRAY);

                portraits.setCardBackgroundColor(Color.WHITE);
                portraits.setCardElevation(35);

                modelling.setCardBackgroundColor(Color.WHITE);
                modelling.setCardElevation(35);

                events.setCardBackgroundColor(Color.WHITE);
                events.setCardElevation(35);

                birthday.setCardBackgroundColor(Color.WHITE);
                birthday.setCardElevation(35);

                wedding.setCardBackgroundColor(Color.WHITE);
                wedding.setCardElevation(35);

                food.setCardBackgroundColor(Color.WHITE);
                food.setCardElevation(35);

                baby.setCardBackgroundColor(Color.WHITE);
                baby.setCardElevation(35);
            }
        });
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootType = "events";
                events.setCardBackgroundColor(Color.GRAY);

                portraits.setCardBackgroundColor(Color.WHITE);
                portraits.setCardElevation(35);

                modelling.setCardBackgroundColor(Color.WHITE);
                modelling.setCardElevation(35);

                socialMedia.setCardBackgroundColor(Color.WHITE);
                socialMedia.setCardElevation(35);

                birthday.setCardBackgroundColor(Color.WHITE);
                birthday.setCardElevation(35);

                wedding.setCardBackgroundColor(Color.WHITE);
                wedding.setCardElevation(35);

                food.setCardBackgroundColor(Color.WHITE);
                food.setCardElevation(35);

                baby.setCardBackgroundColor(Color.WHITE);
                baby.setCardElevation(35);
            }
        });
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootType = "birthday";
                birthday.setCardBackgroundColor(Color.GRAY);

                portraits.setCardBackgroundColor(Color.WHITE);
                portraits.setCardElevation(35);

                modelling.setCardBackgroundColor(Color.WHITE);
                modelling.setCardElevation(35);

                socialMedia.setCardBackgroundColor(Color.WHITE);
                socialMedia.setCardElevation(35);

                events.setCardBackgroundColor(Color.WHITE);
                events.setCardElevation(35);

                wedding.setCardBackgroundColor(Color.WHITE);
                wedding.setCardElevation(35);

                food.setCardBackgroundColor(Color.WHITE);
                food.setCardElevation(35);

                baby.setCardBackgroundColor(Color.WHITE);
                baby.setCardElevation(35);
            }
        });
        wedding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootType = "wedding";
                wedding.setCardBackgroundColor(Color.GRAY);

                portraits.setCardBackgroundColor(Color.WHITE);
                portraits.setCardElevation(35);

                modelling.setCardBackgroundColor(Color.WHITE);
                modelling.setCardElevation(35);

                socialMedia.setCardBackgroundColor(Color.WHITE);
                socialMedia.setCardElevation(35);

                events.setCardBackgroundColor(Color.WHITE);
                events.setCardElevation(35);

                birthday.setCardBackgroundColor(Color.WHITE);
                birthday.setCardElevation(35);

                food.setCardBackgroundColor(Color.WHITE);
                food.setCardElevation(35);

                baby.setCardBackgroundColor(Color.WHITE);
                baby.setCardElevation(35);
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootType = "food";
                food.setCardBackgroundColor(Color.GRAY);

                portraits.setCardBackgroundColor(Color.WHITE);
                portraits.setCardElevation(35);

                modelling.setCardBackgroundColor(Color.WHITE);
                modelling.setCardElevation(35);

                socialMedia.setCardBackgroundColor(Color.WHITE);
                socialMedia.setCardElevation(35);

                events.setCardBackgroundColor(Color.WHITE);
                events.setCardElevation(35);

                birthday.setCardBackgroundColor(Color.WHITE);
                birthday.setCardElevation(35);

                wedding.setCardBackgroundColor(Color.WHITE);
                wedding.setCardElevation(35);

                baby.setCardBackgroundColor(Color.WHITE);
                baby.setCardElevation(35);

            }
        });
        baby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootType = "baby";
                baby.setCardBackgroundColor(Color.GRAY);

                portraits.setCardBackgroundColor(Color.WHITE);
                portraits.setCardElevation(35);

                modelling.setCardBackgroundColor(Color.WHITE);
                modelling.setCardElevation(35);

                socialMedia.setCardBackgroundColor(Color.WHITE);
                socialMedia.setCardElevation(35);

                events.setCardBackgroundColor(Color.WHITE);
                events.setCardElevation(35);

                birthday.setCardBackgroundColor(Color.WHITE);
                birthday.setCardElevation(35);

                wedding.setCardBackgroundColor(Color.WHITE);
                wedding.setCardElevation(35);

                food.setCardBackgroundColor(Color.WHITE);
                food.setCardElevation(35);
            }
        });

        hr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootHour = "1";
                hr1.setCardBackgroundColor(Color.GRAY);

                priceHour = 0;
                //priceHour = 100;

                if (pricePlan == 0) {
                    confirmShoot.setText("confirm " + priceHour + "Rs");
                } else if (pricePlan != 0) {
                    int phpp = priceHour + pricePlan;
                    confirmShoot.setText("confirm " + phpp + "Rs");
                }

                hr2.setCardBackgroundColor(Color.WHITE);
                hr2.setCardElevation(35);

                hr3.setCardBackgroundColor(Color.WHITE);
                hr3.setCardElevation(35);

                hr4.setCardBackgroundColor(Color.WHITE);
                hr4.setCardElevation(35);

                hr5.setCardBackgroundColor(Color.WHITE);
                hr5.setCardElevation(35);
            }
        });
        hr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootHour = "2";
                hr2.setCardBackgroundColor(Color.GRAY);

                priceHour = 0;
                priceHour = 100;

                if (pricePlan == 0) {
                    confirmShoot.setText("confirm " + priceHour + "Rs");
                } else if (pricePlan != 0) {
                    int phpp = priceHour + pricePlan;
                    confirmShoot.setText("confirm " + phpp + "Rs");
                }

                hr1.setCardBackgroundColor(Color.WHITE);
                hr1.setCardElevation(35);

                hr3.setCardBackgroundColor(Color.WHITE);
                hr3.setCardElevation(35);

                hr4.setCardBackgroundColor(Color.WHITE);
                hr4.setCardElevation(35);

                hr5.setCardBackgroundColor(Color.WHITE);
                hr5.setCardElevation(35);
            }
        });
        hr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootHour = "3";
                hr3.setCardBackgroundColor(Color.GRAY);

                priceHour = 0;
                priceHour = 200;

                if (pricePlan == 0) {
                    confirmShoot.setText("confirm " + priceHour + "Rs");
                } else if (pricePlan != 0) {
                    int phpp = priceHour + pricePlan;
                    confirmShoot.setText("confirm " + phpp + "Rs");
                }

                hr1.setCardBackgroundColor(Color.WHITE);
                hr1.setCardElevation(35);

                hr2.setCardBackgroundColor(Color.WHITE);
                hr2.setCardElevation(35);

                hr4.setCardBackgroundColor(Color.WHITE);
                hr4.setCardElevation(35);

                hr5.setCardBackgroundColor(Color.WHITE);
                hr5.setCardElevation(35);
            }
        });
        hr4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootHour = "4";
                hr4.setCardBackgroundColor(Color.GRAY);

                priceHour = 0;
                priceHour = 300;

                if (pricePlan == 0) {
                    confirmShoot.setText("confirm " + priceHour + "Rs");
                } else if (pricePlan != 0) {
                    int phpp = priceHour + pricePlan;
                    confirmShoot.setText("confirm " + phpp + "Rs");
                }

                hr1.setCardBackgroundColor(Color.WHITE);
                hr1.setCardElevation(35);

                hr2.setCardBackgroundColor(Color.WHITE);
                hr2.setCardElevation(35);

                hr3.setCardBackgroundColor(Color.WHITE);
                hr3.setCardElevation(35);

                hr5.setCardBackgroundColor(Color.WHITE);
                hr5.setCardElevation(35);
            }
        });
        hr5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shootHour = "5";
                hr5.setCardBackgroundColor(Color.GRAY);

                priceHour = 0;
                priceHour = 400;

                if (pricePlan == 0) {
                    confirmShoot.setText("confirm " + priceHour + "Rs");
                } else if (pricePlan != 0) {
                    int phpp = priceHour + pricePlan;
                    confirmShoot.setText("confirm " + phpp + "Rs");
                }

                hr1.setCardBackgroundColor(Color.WHITE);
                hr1.setCardElevation(35);

                hr2.setCardBackgroundColor(Color.WHITE);
                hr2.setCardElevation(35);

                hr3.setCardBackgroundColor(Color.WHITE);
                hr3.setCardElevation(35);

                hr4.setCardBackgroundColor(Color.WHITE);
                hr4.setCardElevation(35);
            }
        });


        confirmShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //selectedProUid shootProName, shootPlan, shootType, shootHour

                if (selectedProUid == null) {
                    Toast.makeText(shootNow.this, "select pro", Toast.LENGTH_LONG).show();
                    return;
                }
                if (shootPlan == null) {
                    Toast.makeText(shootNow.this, "select plan", Toast.LENGTH_LONG).show();
                    return;
                }
                if (shootType == null) {
                    Toast.makeText(shootNow.this, "select type", Toast.LENGTH_LONG).show();
                    return;
                }
                if (shootHour == null) {
                    Toast.makeText(shootNow.this, "select hour", Toast.LENGTH_LONG).show();
                    return;
                }

                totalPrice();

                //make payment and according to that there are 2 outcomes
                //1. success 2. failiur
                //if success then send order to firebase
                //if failiur ask user again to make payment
                //upi id = aatmikarm@okhdfcbank , alokgupta90799@okicici
                //makePayment(currentUser.getUsername(),"alokgupta90799@okicici","photoshoot pro app 50Rs",String.valueOf(priceTotal));


                //String dateandtimepattern = "ssmmHHddMMyyyy";

                //if distance is less then 1 km then convert to meters
                if (distanceInKilometersDouble < 1) {
                    //DecimalFormat precision = new DecimalFormat("0");
                    // dblVariable is a number variable and not a String in this case
                    timeOfRideInMinutes = (double) (distanceInKilometersDouble / 35) * 60;
                } else if (distanceInKilometersDouble >= 1) {
                    //DecimalFormat precision = new DecimalFormat("0.00");
                    // dblVariable is a number variable and not a String in this case
                    // proDistance.setText(precision.format(distanceInKilometersDouble)+" km");
                    timeOfRideInMinutes = (double) (distanceInKilometersDouble / 35) * 60;

                }

                // 1st conversion for shoot booked time key
                SimpleDateFormat sdf = new SimpleDateFormat(dateandtimepattern);
                selectedProShootBookedTimeKey = sdf.format(new Date());
                shootBookedTime = converTimeToTimeFormate(selectedProShootBookedTimeKey);

                //2nd conversion for on the way after 15 min or 15*60*1000 in mili seconds
                Date proOnTheWayDate = new Date();
                proOnTheWayDate.setTime(System.currentTimeMillis() + (10 * 60 * 1000));
                proOnTheWayTime = converTimeToTimeFormate(sdf.format(proOnTheWayDate));

                //3nd conversion for photoshoot start time after 45 min or 45*60*1000 in mili seconds
                Date photoshootStartDate = new Date();
                photoshootStartDate.setTime((long) (System.currentTimeMillis() + ((10 + timeOfRideInMinutes) * 60 * 1000)));
                photoshootStartTime = converTimeToTimeFormate(sdf.format(photoshootStartDate));

                //3nd conversion for photoshoot start time after 45 min or 45*60*1000 in mili seconds
                Date picturesReceivedDate = new Date();
                double noOfHours = Double.parseDouble(shootHour);
                picturesReceivedDate.setTime((long) (System.currentTimeMillis() + ((10 + timeOfRideInMinutes + (noOfHours * 60) + 10) * 60 * 1000)));
                picturesReceivedTime = converTimeToTimeFormate(sdf.format(picturesReceivedDate));


                /////////////////////////////////////////////////////////////////////////////////////////////
                sendOrderToProFirebase(selectedProShootBookedTimeKey);
                changeProStatusToNotAvailable();
                Intent intent = new Intent(shootNow.this, proOnJob.class);
                intent.putExtra("selectedProName", selectedProName);
                intent.putExtra("selectedProPic", selectedProPic);
                intent.putExtra("selectedProUid", selectedProUid);
                intent.putExtra("selectedProShootBookedTime", selectedProShootBookedTimeKey);
                intent.putExtra("selectedProDistance", selectedProDistance);
                intent.putExtra("selectedProShootStatus", selectedProShootStatus);
                startActivity(intent);
                finish();
                // Toast.makeText(shootNow.this,"booking time"+selectedProShootBookedTime,Toast.LENGTH_LONG).show();

            }
        });
    }

    private void changeProStatusToNotAvailable() {

        Map<String, Object> status = new HashMap<>();

        status.put("status", "offline");

        mDb.collection("photographers").document(selectedProUid).update(status);
    }

    private String converTimeToTimeFormate(String time) {
        // Get date from string///////////////////////////////////////////////////////////////
        //code to convert any time formate to any other time formate
        SimpleDateFormat dateFormatter = new SimpleDateFormat("ssmmHHddMMyyyy");
        Date date = null;
        try {
            date = dateFormatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Get time from date
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        String displayValue = timeFormatter.format(date);
        return displayValue;
        //////////////////////////////////////////////////////////////////////////////////////
    }


    private void totalPrice() {

        priceTotal = pricePlan + priceHour;
        //Toast.makeText(this, "total price = "+ priceTotal, Toast.LENGTH_SHORT).show();

    }

    //////////////////////////////////////////////////////////////
    // PAYMENT
    final int UPI_PAYMENT = 0;

    void makePayment(String name, String upiId, String note, String amount) {
        Log.e("main ", "name " + name + "--up--" + upiId + "--" + note + "--" + amount);
        // main: name pavan n--up--pavan.n.sap@okaxis--Test UPI Payment--5.00
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(shootNow.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response " + resultCode);
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(shootNow.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here
                //if payment was made and money was sent to photoshoot pro app


                Toast.makeText(shootNow.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: " + approvalRefNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                //if user go to google pay or paytm but comes back without paying
                // or presses back button
                Toast.makeText(shootNow.this, "Payment cancelled by user", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: " + approvalRefNo);

            } else {
                //if user go to google pay or paytm but comes back without paying
                // or presses back button
                Toast.makeText(shootNow.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: " + approvalRefNo);

            }
        } else {
            //if internet is not turned on at any point of transation
            // before or after
            Toast.makeText(shootNow.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////


    private void sendOrderToProFirebase(final String selectedProShootBookedTimee) {
        // save user name hone number and email id and password in variables
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
                    Date timestamp = new Date();
                    String userUid = firebaseAuth.getUid();


                    Map<String, Object> orderDetails = new HashMap<>();

                    orderDetails.put("userUid", userUid);
                    orderDetails.put("geoPoint", geoPoint);
                    orderDetails.put("timestamp", timestamp);
                    orderDetails.put("userName", currentUser.getUsername());
                    orderDetails.put("userPhone", currentUser.getPhone());
                    orderDetails.put("proName", selectedProName);
                    orderDetails.put("proUid", selectedProUid);
                    orderDetails.put("proPic", selectedProPic);
                    orderDetails.put("shootPlan", shootPlan);
                    orderDetails.put("shootType", shootType);
                    orderDetails.put("shootHour", shootHour);
                    orderDetails.put("shootPrice", priceTotal);
                    orderDetails.put("shootStatus", selectedProShootStatus);
                    orderDetails.put("shootBookedTimeKey", selectedProShootBookedTimee);
                    orderDetails.put("shootBookedTime", shootBookedTime);
                    orderDetails.put("proOnTheWayTime", proOnTheWayTime);
                    orderDetails.put("photoshootStartTime", photoshootStartTime);
                    orderDetails.put("picturesReceivedTime", picturesReceivedTime);
                    orderDetails.put("proDistance", selectedProDistance);


                    // mDb.collection("orders").document(selectedProUid).set(orderDetails);
                    mDb.collection("shoots").document(shootNow.this.selectedProShootBookedTimeKey).set(orderDetails);
                }
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
                                        .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(shootNow.this, R.mipmap.ic_profile)))
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
                                        (shootNow.this,
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