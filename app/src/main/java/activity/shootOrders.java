package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.example.test1photographerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class shootOrders extends AppCompatActivity {

    RecyclerView shootOrdersrecyclerView;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_shoot_orders);

        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        //////////////////////////////////////////////////////////////////////////
        //Recycler View for photographers , recycler view test 2
        final ArrayList<shootOrdersRVModelList> shootOrdersList = getAllOrdersOfCurrentUserFromFirestoreForRecyclerView();


        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                //Do something after 2 seconds 2000ms
                shootOrdersrecyclerView = findViewById(R.id.shoot_orders_RV);
                shootOrdersRVAdapter shootOrdersRVAdapter = new shootOrdersRVAdapter(getApplicationContext(), shootOrdersList);
                shootOrdersrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                shootOrdersrecyclerView.setAdapter(shootOrdersRVAdapter);

            }
        }, 3500);


        /////////////////////////////////////////////


    }

    private ArrayList<shootOrdersRVModelList> getAllOrdersOfCurrentUserFromFirestoreForRecyclerView() {
        String currentUserUid = firebaseAuth.getUid();
        final ArrayList<shootOrdersRVModelList> shootOrdersRVModelList = new ArrayList<>();

        mDb.collection("shoots").whereEqualTo("userUid", currentUserUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //it performs a for loop to get each seperate user details and location
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        shootOrdersRVModelList shootOrderModelListttt = new shootOrdersRVModelList();

                        shootOrderModelListttt.setPhotographer_id((String) document.get("proUid"));
                        shootOrderModelListttt.setPhotographer_name((String) document.get("proName"));
                        shootOrderModelListttt.setPhotographer_pic_url((String) document.get("proPic"));
                        shootOrderModelListttt.setShoot_plan((String) document.get("shootPlan"));
                        shootOrderModelListttt.setShoot_type((String) document.get("shootType"));
                        shootOrderModelListttt.setShoot_hour((String) document.get("shootHour"));
                        shootOrderModelListttt.setPhotographer_arrival_time((String) document.get("photoshootStartTime"));
                        shootOrderModelListttt.setShootBookedTimeKey((String) document.get("shootBookedTimeKey"));
                        shootOrderModelListttt.setProDistance((String) document.get("proDistance"));
                        shootOrderModelListttt.setShoot_status((String) document.get("shootStatus"));

                        shootOrdersRVModelList.add(shootOrderModelListttt);
                        //Toast.makeText(shootOrders.this, "orders = "+document.get("userUid"), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //send finaly created arraylist that holds obj of ImageUrlll to adapter
        return shootOrdersRVModelList;
    }

}
