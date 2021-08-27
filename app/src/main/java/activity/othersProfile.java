package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class othersProfile extends AppCompatActivity {

    String dateandtimepattern = "ssmmHHddMMyyyy";
    private ImageView profileImage;
    private Button backBtn, shootBtn;
    private TextView userName,noOfShoots;;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;

    private Uri filePath;
    othersProfile_grid_pics_RV_Adapter othersProfile_grid_pics_rv_adapter;
    RecyclerView othersProfileGridPics_RV;

    String tempPostUserName;
    String tempPostUrl;
    //ArrayList<userRecyclerViewListModel> othersProfileImageUrlList;
    String othersProfileUid;
    String othersProfilePicUrl;
    String othersProfileUserName;

    int shootCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_others_profile);

        userName = findViewById(R.id.others_profile_user_name);
        backBtn = findViewById(R.id.others_profile_backBtn);
        shootBtn = findViewById(R.id.othersProfileShootBtn);
        profileImage = findViewById(R.id.others_profile_image_View);
        noOfShoots = findViewById(R.id.noOfShoots);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        if (getIntent().getExtras() != null) {
            //this.othersProfileImageUrlList = (ArrayList<userRecyclerViewListModel>) getIntent().getExtras().get("othersProfileImageUrlList");
            this.othersProfileUid = (String) getIntent().getExtras().get("othersProfileUid");
            this.othersProfilePicUrl = (String) getIntent().getExtras().get("othersProfileProPicUrl");
            this.othersProfileUserName = (String) getIntent().getExtras().get("othersProfileUserName");

        }


        //////////////////////////////////////////////////////////////////////////
        //Recycler View for photographers , recycler view test 2
        final ArrayList othersProfileImageUrlList = getProPicsForOthersProfileGrid_RV();
        // set up the RecyclerView
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 2 seconds 2000ms

                int numberOfColumns = 3;
                othersProfileGridPics_RV = (RecyclerView) findViewById(R.id.others_profile_grid_pics_RV);
                othersProfileGridPics_RV.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfColumns));
                //if you want to show recycler view in linr fashion
                // AllphotographerListrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                othersProfile_grid_pics_rv_adapter = new othersProfile_grid_pics_RV_Adapter(getApplicationContext(), othersProfileImageUrlList);
                othersProfileGridPics_RV.setAdapter(othersProfile_grid_pics_rv_adapter);

            }
        }, 3500);


        setOthersProfileUserImage();
        setOthersProfileUserName();


        shootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(othersProfile.this, shootNow.class);
                intent.putExtra("selectedProName",othersProfileUserName);
                intent.putExtra("selectedProPic",othersProfilePicUrl);
                intent.putExtra("selectedProUid",othersProfileUid);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        mDb.collection("shoots").whereEqualTo("proUid", othersProfileUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    private void setOthersProfileUserImage() {
        Glide.with(getApplicationContext()).load(othersProfilePicUrl).into(profileImage);
    }


    private void setOthersProfileUserName() {
        userName.setText(othersProfileUserName);
        //Toast.makeText(this, "Pro Uid = " + othersProfileImageUrlList, Toast.LENGTH_SHORT).show();
    }


    private ArrayList<proDetail_RV_Model> getProPicsForOthersProfileGrid_RV() {

        final ArrayList<proDetail_RV_Model> userRecyclerViewListModelList = new ArrayList<>();
        mDb.collection("photographers/" + othersProfileUid + "/" + "grid/").get()
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
                                mStorageRef.child("images/" + othersProfileUid + "/" + "grid").child(document.get("currentDateandTime") + ".jpg")
                                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        proDetail_RV_Model imageUrllll = new proDetail_RV_Model();
                                        imageUrllll.setImageUrl(uri.toString());
                                        imageUrllll.setUid(othersProfileUid);
                                        imageUrllll.setCurrentDateandTime(currentDateandTime);
                                        userRecyclerViewListModelList.add(imageUrllll);
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


}
