package com.ZellyCookies.PineApple.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.ZellyCookies.PineApple.Login.Login;
import com.ZellyCookies.PineApple.R;
import com.ZellyCookies.PineApple.Utils.TopNavigationViewHelper;
import com.ZellyCookies.PineApple.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class Profile_Activity extends AppCompatActivity {
    private static final String TAG = "Profile_Activity";
    private static final int ACTIVITY_NUM = 2;
    static boolean active = false;

    //initialize display data
    private Context mContext = Profile_Activity.this;
    private ImageView imagePerson;
    private TextView name;
    private TextView DoBAndSex;
    private String userId;

    //setup firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mPhotoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: create the page");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //setup firebase method
        setupFirebaseAuth();
        setupTopNavigationView();

        //setup display content
        userId = mAuth.getInstance().getCurrentUser().getUid();
        imagePerson = findViewById(R.id.circle_profile_image);
        name = findViewById(R.id.profile_name);
        DoBAndSex = findViewById(R.id.profile_dob);

        //get user's data
        findUser();

        //setup buttons
        ImageButton edit_btn = (ImageButton) findViewById(R.id.edit_profile);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Activity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Activity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: resume to the page");
        findUser();
    }

    public void findUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //male user's data
        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(userId)) {
                    Log.d(TAG, "onChildAdded: the sex is male" );
                    getUserPhotoAndName(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (active) {
                    getUserPhotoAndName(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //female user's data
        DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference().child("female");
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equals(user.getUid())) {
                    Log.d(TAG, "onChildAdded: the sex is female" );
                    getUserPhotoAndName(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (active) {
                    getUserPhotoAndName(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //get user's info to display
    private void getUserPhotoAndName(DataSnapshot dataSnapshot){
        name.setText(dataSnapshot.getValue(User.class).getUsername());
        DoBAndSex.setText("Born: " + dataSnapshot.getValue(User.class).getDateOfBirth() + " | Gender: " + dataSnapshot.getValue(User.class).getSex());
        String profileImageUrl = dataSnapshot.getValue(User.class).getProfileImageUrl();

        //set avatar
        if (Util.isOnMainThread()) {
            switch (profileImageUrl) {
                case "defaultFemale":
                    Glide.with(getApplicationContext()).load(R.drawable.img_ava_female).into(imagePerson);
                    break;
                case "defaultMale":
                    Glide.with(getApplicationContext()).load(R.drawable.img_ava_male).into(imagePerson);
                    break;
                default:
                    Glide.with(getApplicationContext()).load(profileImageUrl).into(imagePerson);
                    break;
            }
        }
    }

    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationViewEx tvEx = (BottomNavigationViewEx) findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    //----------------------------------------Firebase----------------------------------------

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                } else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");

                    Log.d(TAG, "onAuthStateChanged: navigating back to login screen.");
                    Intent intent = new Intent(Profile_Activity.this, Login.class);

                    //clear the activity stack， in case when sign out, the back button will bring the user back to the previous activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        Log.d(TAG, "onStart: " + active);
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
        Log.d(TAG, "onStop: " + active);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
        Log.d(TAG, "onDestroy: " + active);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
