package activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class proFullDeatilFrag extends Fragment {

    TextView proName;
    ImageView proPicc;
    TextView proDistance;
    Button proFullDetailShootBtn;
    Button proFullDetailScheduleBtn;

    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;

    proDetail_recycler_view_Adapter proDetail_adapter;
    RecyclerView proDetail_RV;

    String proNamee;
    String proPic;
    String proUid;
    String proDistanceInKm;
    ArrayList imageUrlList;

    public proFullDeatilFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profulldetail, container, false);

        Bundle arguments = getArguments();


        if (arguments != null) {

            this.proNamee = arguments.get("proName").toString();
            this.proPic = arguments.get("proPic").toString();
            this.proUid = arguments.get("proUid").toString();
            this.proDistanceInKm = arguments.get("proDistance").toString();

        }


        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        proName = (TextView) view.findViewById(R.id.proOrder_proStatus);
        proPicc = (ImageView) view.findViewById(R.id.proOnJob_profile_img);
        proDistance = (TextView) view.findViewById(R.id.proFullDetail_distance);

        proFullDetailShootBtn = (Button) view.findViewById(R.id.pro_full_detail_shoot_btn);
        proFullDetailScheduleBtn = (Button) view.findViewById(R.id.pro_full_detail_schedule_btn);

        //////////////////////////////////////////////////////////////////////////
        proDetail_RV = (RecyclerView) view.findViewById(R.id.pro_detail_frag_taken_img_rv);
        proDetail_RV.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        return view;
    }

    private ArrayList<proDetail_RV_Model> getProPostedPicsForRV() {
        final ArrayList<proDetail_RV_Model> proDetail_RV_Model = new ArrayList<>();
        mDb.collection("photographers/" + proUid + "/" + "grid/").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //final String KeyDateandTime = (String) document.get("currentDateandTime");

                                mStorageRef.child("images/" + proUid + "/" + "grid").child(document.get("currentDateandTime") + ".jpg")
                                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        proDetail_RV_Model imageUrlll = new proDetail_RV_Model();
                                        imageUrlll.setImageUrl(uri.toString());
                                        //imageUrlll.setUid(uid);
                                        //imageUrlll.setCurrentDateandTime(KeyDateandTime);
                                        proDetail_RV_Model.add(imageUrlll);


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                            }
                        }
                    }
                });
        //send finaly created arraylist that holds obj of ImageUrlll to adapter
        return proDetail_RV_Model;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        proName.setText(proNamee);

        double distanceInKilometers = Double.parseDouble(proDistanceInKm);
        //if distance is less then 1 km then convert to meters
        if (distanceInKilometers<1){
            DecimalFormat precision = new DecimalFormat("0");
            // dblVariable is a number variable and not a String in this case
            proDistance.setText(precision.format(distanceInKilometers*1000)+" m");
            //Toast.makeText(getContext(), "distance in meters = "+precision.format(distanceInKilometers*1000)+" m", Toast.LENGTH_SHORT).show();

        }else if (distanceInKilometers>=1){
            DecimalFormat precision = new DecimalFormat("0.00");
            // dblVariable is a number variable and not a String in this case
            proDistance.setText(precision.format(distanceInKilometers)+" km");
        }


        Glide.with((proFullDeatilFrag) this).load(proPic).into(proPicc);


        imageUrlList = getProPostedPicsForRV();

        // set up the RecyclerView
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 2 seconds 2000ms

                proDetail_adapter = new proDetail_recycler_view_Adapter(getContext(), imageUrlList);
                proDetail_RV.setAdapter(proDetail_adapter);


            }
        }, 2000);


        proFullDetailShootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), shootNow.class);
                intent.putExtra("selectedProName", proNamee);
                intent.putExtra("selectedProPic", proPic);
                intent.putExtra("selectedProUid", proUid);
                intent.putExtra("selectedProDistance", proDistanceInKm);
                startActivity(intent);
            }
        });

        proFullDetailScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), scheduleShoot.class));
            }
        });


        proPicc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), othersProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // intent.putExtra("othersProfileImageUrlList", imageUrlList);
                intent.putExtra("othersProfileUid", proUid);
                intent.putExtra("othersProfileProPicUrl", proPic);
                intent.putExtra("othersProfileUserName", proNamee);
                v.getContext().startActivity(intent);

            }
        });

        proName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), othersProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("othersProfileImageUrlList", imageUrlList);
                intent.putExtra("othersProfileUid", proUid);
                intent.putExtra("othersProfileProPicUrl", proPic);
                intent.putExtra("othersProfileUserName", proNamee);
                getContext().startActivity(intent);
            }
        });


    }


}
