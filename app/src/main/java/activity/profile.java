package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.test1photographerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 77;
    private static final int PICK_IMAGE_REQUEST_FOR_GRID = 78;
    String dateandtimepattern = "ssmmHHddMMyyyy";
    private ImageView profileImage;
    private Button logOutBtn, backBtn, uploadBtn;
    private TextView userName,noOfShoots;;;
    private TextView emailId;
    private TextView phoneNumber;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    private Uri filePath;
    user_upload_grid_pics_recycler_view_Adapter adapter;
    RecyclerView AlluserListrecyclerView;


    String tempPostUserName;
    String tempPostUrl;
    int shootCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.profile_user_name);
        backBtn = findViewById(R.id.backBtn);
        uploadBtn = findViewById(R.id.uploadBtn);
        emailId = findViewById(R.id.profile_email_id);
        phoneNumber = findViewById(R.id.profile_phone_number);
        logOutBtn = findViewById(R.id.logOutButton);
        noOfShoots = findViewById(R.id.noOfShoots);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(profile.this, "Upload Pictures", Toast.LENGTH_SHORT).show();
                SelectImageForGrid();

            }
        });

        //upload an image to cloude firestore storage
        profileImage = findViewById(R.id.profile_image_View);

        //////////////////////////////////////////////////////////////////////////
        //Recycler View for photographers , recycler view test 2
        final ArrayList imageUrlList = getAllUserPicsFromFirestoreForGridRecyclerView();
        // set up the RecyclerView
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 2 seconds 2000ms

                int numberOfColumns = 3;
                AlluserListrecyclerView = (RecyclerView) findViewById(R.id.user_upload_grid_pics_recycler_view);
                AlluserListrecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfColumns));
                //if you want to show recycler view in linr fashion
                // AllphotographerListrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                user_upload_grid_pics_recycler_view_Adapter userRecyclerViewListDataAdapter = new user_upload_grid_pics_recycler_view_Adapter(getApplicationContext(), imageUrlList);
                AlluserListrecyclerView.setAdapter(userRecyclerViewListDataAdapter);

            }
        }, 3500);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


        final String uid = firebaseAuth.getUid();


        mDb.collection("users")
                .document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //this document.get("password") code will give you particural feild of users list
                        //Toast.makeText(getApplicationContext(), "password = " + document.get("Full Name") ,Toast.LENGTH_LONG).show();
//                        String muserName = document.get("username").toString();
                        userName.setText(document.get("username").toString());
                        initializeUsername(document.get("username").toString());
                        //these are two diffrent ways of doing same thing ,one without
                        //variable and one with musername variable help
                        emailId.setText(document.get("email").toString());
                        phoneNumber.setText(document.get("phone").toString());


                    } else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setCurrentUserImage();

        mDb.collection("shoots").whereEqualTo("userUid", firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        shootCount = shootCount + 1;
                    }
                    noOfShoots.setText(String.valueOf(shootCount));
                }
            }
        });

    }



    private void initializeUsername(String username) {
        this.tempPostUserName = username;
    }


    private ArrayList<userRecyclerViewListModel> getAllUserPicsFromFirestoreForGridRecyclerView() {

        final ArrayList<userRecyclerViewListModel> userRecyclerViewListModelList = new ArrayList<>();
        final String uid = firebaseAuth.getUid();
        mDb.collection("users/" + uid + "/" + "grid/").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //it performs a for loop to get each seperate user details and location
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //final Grid grid = document.toObject(Grid.class);

                                final String currentDateandTime = (String) document.get("currentDateandTime");

                                //Toast.makeText(profile.this, "success TimeStamp" + document.get("currentDateandTime"), Toast.LENGTH_LONG).show();
                                //final Photographer photographer = document.toObject(Photographer.class);
                                //final String uid = firebaseAuth.getUid();
                                mStorageRef.child("images/" + uid + "/" + "grid").child(document.get("currentDateandTime") + ".jpg")
                                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        userRecyclerViewListModel imageUrlll = new userRecyclerViewListModel();
                                        imageUrlll.setImageUrl(uri.toString());
                                        imageUrlll.setUid(uid);
                                        imageUrlll.setCurrentDateandTime(currentDateandTime);
                                        userRecyclerViewListModelList.add(imageUrlll);
                                        //just toast for testing
                                        // Toast.makeText(profile.this, "success " + imageUrlll.getImageUrl(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                            }
                            //Toast.makeText(profile.this, "i value = "+i, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //send finaly created arraylist that holds obj of ImageUrlll to adapter
        return userRecyclerViewListModelList;
    }


    private void setCurrentUserImage() {
        // Defining the child of storageReference
        final String uid = firebaseAuth.getUid();
        StorageReference ref = mStorageRef.child("images/" + uid).child("profilepic.jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageView imageView;
                imageView = findViewById(R.id.profile_image_View);
                Glide.with(getApplicationContext()).load(uri).into(imageView);
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(profile.this, signin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    // Select Image method
    private void SelectImage() {   // select image from photos in galary funtion
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    // Select Image method
    private void SelectImageForGrid() {   // select image from photos in galary funtion
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST_FOR_GRID);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {   // Get the Uri of data
            filePath = data.getData();
            try { // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmap);
                ///this below code will auto upload image to firbase storage
                try {
                    uploadImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST_FOR_GRID && resultCode == RESULT_OK && data != null && data.getData() != null) {   // Get the Uri of data
            filePath = data.getData();
            try { // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //profileImage.setImageBitmap(bitmap);
                ///this below code will auto upload image to firbase storage
                try {
                    uploadImageForGrid();
                    //shareToInstagram shareToInstagram = new shareToInstagram();
                    //startActivity(shareToInstagram.shareToInstagramm(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    // UploadImage method
    private void uploadImage() throws IOException {
        if (filePath != null) {   // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //this code below helps in reducing the image size and then
            //uploading the imag to firebase storage
            final String uid = firebaseAuth.getUid();
            StorageReference childRef2 = mStorageRef.child("images/" + uid).child("profilepic.jpg");
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            //uploading the image
            UploadTask uploadTask2 = childRef2.putBytes(data);
            uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        }
    }

    // UploadImage method
    private void uploadImageForGrid() throws IOException {
        if (filePath != null) {   // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //this code below helps in reducing the image size and then
            //uploading the imag to firebase storage
            final String uid = firebaseAuth.getUid();
            // HH:mm dd/MM/yyyy
            SimpleDateFormat sdf = new SimpleDateFormat(dateandtimepattern);
            final String currentDateandTime = sdf.format(new Date());
            //Toast.makeText(this, "date and time = "+currentDateandTime, Toast.LENGTH_SHORT).show();

            StorageReference childRef2 = mStorageRef.child("images/" + uid + "/" + "grid/").child(currentDateandTime + ".jpg");


            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            //uploading the image
            UploadTask uploadTask2 = childRef2.putBytes(data);
            uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();


                    mStorageRef.child("images/" + uid + "/" + "grid/").child(currentDateandTime + ".jpg")
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            initializePostUrl(uri.toString());
                            updateProGridDataBaseForNewFileDateAndTime(currentDateandTime);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        }
    }

    private void initializePostUrl(String Url) {
        this.tempPostUrl = Url;
    }

    private void updateProGridDataBaseForNewFileDateAndTime(String timestamp) {

        final String uid = firebaseAuth.getUid();
        Map<String, Object> postdetails = new HashMap<>();
        postdetails.put("currentDateandTime", timestamp);
        postdetails.put("uid", uid);
        postdetails.put("username", tempPostUserName);
        postdetails.put("url", tempPostUrl);
        //note this set commont in firestore overright exiting user data
        // set() delet data and write new user data
        //whereas update only change exixting data and dont delet everything
        mDb.collection("users/" + uid + "/" + "grid").document(timestamp).set(postdetails);


        // mDb.collection("posts/"+timestamp).document("post").update(postdetailss);
        mDb.collection("posts").document(timestamp).set(postdetails);
        // Toast.makeText(this, "pic has been sent to storage and firestore at = "+timestamp, Toast.LENGTH_LONG).show();
    }

}
