package activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.test1photographerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class othersPost extends AppCompatActivity {

    private ImageView profileImage, postImage, postProfileImageOnLike;
    private ImageView postHeart, postComment, postShare, postBookmark;
    private Button backBtn;
    private TextView userName , likedByNumberTv;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    String postUrl;
    String postUid;
    String postDateandTime;
    //String currentUserUid;
    boolean clicked = true;
    String count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        final String currentUserUid = firebaseAuth.getUid();

        profileImage = findViewById(R.id.post_profile_image);
        backBtn = findViewById(R.id.post_back_btn);
        userName = findViewById(R.id.post_profile_user_name);
        postImage = findViewById(R.id.post_image);
        postProfileImageOnLike = findViewById(R.id.post_profile_image_on_like);
        likedByNumberTv = findViewById(R.id.liked_by_number_tv);
        postHeart = findViewById(R.id.post_heart);
        postComment = findViewById(R.id.post_comment);
        postShare = findViewById(R.id.post_share);
       // postBookmark = findViewById(R.id.post_bookmark);


        if (getIntent().getExtras() != null) {
            this.postUrl = (String) getIntent().getExtras().get("postUrl");
            this.postUid = (String) getIntent().getExtras().get("postUid");
            this.postDateandTime = (String) getIntent().getExtras().get("postDateandTime");
            //this.currentUserUid = currentUserUid;

        }

       // Toast.makeText(this, "UID OF POST = " + postUid, Toast.LENGTH_SHORT).show();

        setProDetails(postUid);
        setProProfileImage();
        getPostLike(currentUserUid);



        postHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (clicked==true) {
                    postHeart.setBackgroundResource(R.drawable.ic_heartoff);
                    clicked = false;

                    mDb.collection("posts/"+postDateandTime+"/likes/").document(currentUserUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(othersPost.this, "like of currentUser was removed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    mDb.collection("photographers/"+postUid+"/"+"post/").document(postDateandTime).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(othersPost.this, "like of currentUser was removed", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (clicked == false){
                    postHeart.setBackgroundResource(R.drawable.ic_hearton);
                    clicked = true;

                    Map<String, Object> postDetails = new HashMap<>();
                    postDetails.put("likedby", currentUserUid);
                    postDetails.put("likedat", new Date());

                    //note this set commont in firestore overright exiting user data
                    // set() delet data and write new user data
                    //whereas update only change exixting data and dont delet everything
                    mDb.collection("photographers/"+postUid+"/"+"post/").document(postDateandTime).set(postDetails);
                    mDb.collection("posts/"+postDateandTime+"/likes/").document(currentUserUid).set(postDetails);
                    Toast.makeText(othersPost.this, "like of currentUser was added", Toast.LENGTH_SHORT).show();


                }
            }
        });



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), profile.class));
                finish();
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), profile.class));
                finish();
            }
        });


    }


    private void getPostLike(String currentUserUid){

        mDb.collection("posts/"+postDateandTime+"/likes/").document(currentUserUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               if (documentSnapshot.getString("likedby") != null) {
                   postHeart.setBackgroundResource(R.drawable.ic_hearton);
                   clicked = true;
               }
               else if (documentSnapshot.getString("likedby") == null){
                   postHeart.setBackgroundResource(R.drawable.ic_heartoff);
                   clicked = false;
               }
            }
        });

        mDb.collection("posts/"+postDateandTime+"/likes/").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                count = String.valueOf(queryDocumentSnapshots.size());
                //Toast.makeText(post.this, "likes number "+count, Toast.LENGTH_SHORT).show();
                likedByNumberTv.setText(count);

            }
        });

    }

    private void setProDetails(String uid) {

        mDb.collection("photographers")
                .document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName.setText(document.get("photographername").toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setProProfileImage() {

        Glide.with(getApplicationContext()).load(postUrl).into(profileImage);
        Glide.with(getApplicationContext()).load(postUrl).into(postImage);
        Glide.with(getApplicationContext()).load(postUrl).into(postProfileImageOnLike);

    }


}
