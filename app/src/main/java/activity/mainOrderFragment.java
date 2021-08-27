package activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.Arrays;


public class mainOrderFragment extends Fragment {

    TextView shootUser_name, shootUser_distance, shootUser_price_TV,shoot_subtitle_text, shootPro_status_update_TV, shootOrder_plan_text, shootOrder_type_text, shootOrder_hours_text;
    ImageView shootUser_profile_img, shootOrder_type_image, shootOrder_plan_image;
    CardView shootUser_call, navigateCardView;

    String proUid, proName, userDistanceInKm, shootHour, shootPlan, shootType, shootStatus, userPhone, shootPrice;

    double distanceInKilometers;

    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    ObjectAnimator animator;

    String proDefaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/test1photographer.appspot.com/o/default%2FproDefault.png?alt=media&token=059f8d8f-8a0c-4f9f-829d-73e434de9ac7";
    String userDefaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/test1photographer.appspot.com/o/default%2FuserDefault.png?alt=media&token=0f495f89-caa3-4bcb-b278-97548eb77490";


    public mainOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main_order, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            this.proName = arguments.get("proName").toString();
            this.proUid = arguments.get("proUid").toString();

            this.shootHour = arguments.get("shootHour").toString();
            this.shootPlan = arguments.get("shootPlan").toString();
            this.shootType = arguments.get("shootType").toString();
            this.shootStatus = arguments.get("shootStatus").toString();

            this.shootPrice = arguments.get("shootPrice").toString();
        }

        mDb = FirebaseFirestore.getInstance();
        firebaseAuth = firebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        shootUser_profile_img = (ImageView) view.findViewById(R.id.shootUser_profile_img);
        shootOrder_type_image = (ImageView) view.findViewById(R.id.shootOrder_type_image);
        shootOrder_plan_image = (ImageView) view.findViewById(R.id.shootOrder_plan_image);
        shootUser_name = (TextView) view.findViewById(R.id.shootUser_name);

        shootUser_price_TV = (TextView) view.findViewById(R.id.shootUser_price_TV);
        shootOrder_plan_text = (TextView) view.findViewById(R.id.shootOrder_plan_text);
        shootOrder_type_text = (TextView) view.findViewById(R.id.shootOrder_type_text);
        shootOrder_hours_text = (TextView) view.findViewById(R.id.shootOrder_hours_text);
        shoot_subtitle_text = (TextView) view.findViewById(R.id.shoot_subtitle_text);

        shootUser_call = (CardView) view.findViewById(R.id.shootUser_call);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set shootUser name
        shootUser_name.setText(proName);
        //set shootUser price
        shootUser_price_TV.setText(shootPrice);
        //set shootUser plan
        shootOrder_plan_text.setText(shootPlan);
        //set shootUser type
        shootOrder_type_text.setText(shootType);
        //set shootUser hour
        shootOrder_hours_text.setText(shootHour);


        //set shootUser image
        StorageReference ref = mStorageRef.child("images/" + proUid).child("profilepic.jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).into(shootUser_profile_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(getContext()).load(proDefaultImageUrl).into(shootUser_profile_img);
            }
        });


        switch (shootType) {

            case "portraits":
                shootOrder_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_portrait));
                break;
            case "modelling":
                shootOrder_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_modelling));
                break;
            case "socialMedia":
                shootOrder_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_socialmedia));
                break;
            case "events":
                shootOrder_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_celebrate));
                break;
            case "birthday":
                shootOrder_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_birthday));
                break;
            case "wedding":
                shootOrder_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_wedding));
                break;
            case "food":
                shootOrder_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_food));
                break;
            case "baby":
                shootOrder_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baby));
                break;

        }

        switch (shootPlan) {

            case "smartphone":
                shootOrder_plan_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_smartphone));
                break;
            case "value":
                shootOrder_plan_image.setImageDrawable(getResources().getDrawable(R.drawable.camera));
                break;
            case "professional":
                shootOrder_plan_image.setImageDrawable(getResources().getDrawable(R.drawable.cameraf));
                break;
            case "premium":
                shootOrder_plan_image.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pro_round));
                break;

        }

        switch (shootStatus) {
            case "Shoot Booked":
                shoot_subtitle_text.setText("Shoot Booked");
                break;
            case "On the way":
                shoot_subtitle_text.setText("On the way");
                break;
            case "Photoshoot ongoing":
                shoot_subtitle_text.setText("Photoshoot ongoing");
                break;
            case "Pictures received":
                shoot_subtitle_text.setText("Photoshoot completed");
                break;
        }

        shootUser_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDb.collection("photographers").document(proUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(documentSnapshot.get("phone")), null));
                        startActivity(intent);
                    }
                });
            }
        });
    }


    @SuppressLint("RestrictedApi")
    private void blinkingAnimation(Button shootPro_status_update_btn) {
//        animation = new AlphaAnimation(0.7f, 1.0f);
//        animation.setDuration(1000); //You can manage the blinking time with this parameter
//        animation.setStartOffset(20);
//        animation.setRepeatMode(Animation.REVERSE);
//        animation.setRepeatCount(Animation.INFINITE);
//        shootPro_status_update_btn.startAnimation(animation);

        animator = ObjectAnimator.ofInt(shootPro_status_update_btn, "backgroundColor", Color.GREEN, Color.BLACK);
        animator.setDuration(1000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();

    }

}
