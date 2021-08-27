package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test1photographerapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    //defining view objects
    private Button buttonSignup, btnSignIn, btnResetPassword;
    private EditText mfullName;
    private EditText mphoneNumber;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mDb;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String TAG = "DocSnippets";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mfullName = (EditText) findViewById(R.id.fullName);
        mphoneNumber = (EditText) findViewById(R.id.phoneNumber);


        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        progressDialog = new ProgressDialog(this);
        enableMyLocationIfPermitted();

        // reset password screen
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this, ResetPasswordActivity.class));
            }
        });


        //signin screen login screen
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this, signin.class));

            }
        });


        //attaching listener to button
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocationIfPermitted();
            }
        }
    }


    private void registerUser() {

        //getting email and password from edit texts
        String name = mfullName.getText().toString();
        String phone = mphoneNumber.getText().toString();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checking if name , phone number , email and passwords are empty

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            //display some message here and store user name and phone number to firebase firestore
                            //this function will send user data to firevbase firestore ( user nasme and phone number )
                            sendUserDataAndLocation();
                            Toast.makeText(signup.this, "Successfully registered", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            //display some message here
                            Toast.makeText(signup.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }


    private void sendUserDataAndLocation() {
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
                    // Create a new user with a first and last name
                    String uid = firebaseAuth.getUid();
                    String fName = mfullName.getText().toString().trim();
                    String fPhone = mphoneNumber.getText().toString().trim();
                    String fEmail = editTextEmail.getText().toString().trim();
                    String fPassword = editTextPassword.getText().toString().trim();
                    User user = new User(fEmail, uid, fName, fPhone, fPassword, geoPoint, timestamp);
                    //note this set commont in firestore overright exiting user data
                    //set() delet data and write new user data
                    //whereas update only change exixting data and dont delet everything
                    mDb.collection("users").document(uid).set(user);
                }
            }
        });
    }
}