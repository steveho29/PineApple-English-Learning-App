package com.ZellyCookies.PineApple.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ZellyCookies.PineApple.R;
import com.ZellyCookies.PineApple.Utils.FirebaseMethods;
import com.ZellyCookies.PineApple.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterHobby extends AppCompatActivity {
    private static final String TAG = "RegisterHobby";

    //User Info
    User userInfo;
    String password;

    private Context mContext;
    private Button hobbiesContinueButton;
    private Button seSelectionButton;
    private Button dbSelectionButton;
    private Button uiSelectionButton;
    private Button opSelectionButton;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String append = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_hobby);
        mContext = RegisterHobby.this;
        firebaseMethods = new FirebaseMethods(mContext);
        Log.d(TAG, "onCreate: started");

        Intent intent = getIntent();
        userInfo = (User) intent.getSerializableExtra("classUser");
        password = intent.getStringExtra("password");

        initWidgets();
        setupFirebaseAuth();
        init();
    }

    private void initWidgets(){
        seSelectionButton = (Button) findViewById(R.id.seSelectionButton);
        dbSelectionButton = (Button) findViewById(R.id.dbSelectionButton);
        uiSelectionButton = (Button) findViewById(R.id.uiSelectionButton);
        opSelectionButton = (Button) findViewById(R.id.opSelectionButton);
        hobbiesContinueButton = (Button) findViewById(R.id.hobbiesContinueButton);

        // Initially all the buttons needs to be grayed out so this code is added, on selection we will enable it later
        seSelectionButton.setAlpha(.5f);

        dbSelectionButton.setAlpha(.5f);

        uiSelectionButton.setAlpha(.5f);

        opSelectionButton.setAlpha(.5f);


        seSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seButtonClicked();
            }
        });

        dbSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbButtonClicked();
            }
        });

        uiSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiButtonClicked();
            }
        });

        opSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opButtonClicked();
            }
        });

    }

    public void seButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (seSelectionButton.getAlpha() == 1.0f)
        {
            seSelectionButton.setAlpha(.5f);
            seSelectionButton.setBackgroundResource(R.drawable.btn_alt);
            userInfo.setSE(false);
        }
        else
        {
            seSelectionButton.setAlpha(1.0f);
            seSelectionButton.setBackgroundResource(R.drawable.btn_main);
            userInfo.setSE(true);
        }
    }

    public void dbButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (dbSelectionButton.getAlpha() == 1.0f)
        {
            dbSelectionButton.setAlpha(.5f);
            dbSelectionButton.setBackgroundResource(R.drawable.btn_alt);
            userInfo.setDatabase(false);
        }
        else
        {
            dbSelectionButton.setAlpha(1.0f);
            dbSelectionButton.setBackgroundResource(R.drawable.btn_main);
            userInfo.setDatabase(true);

        }

    }

    public void uiButtonClicked()
    {
        // this is to toggle between selection and non selection of button
        if (uiSelectionButton.getAlpha() == 1.0f)
        {
            uiSelectionButton.setAlpha(.5f);
            uiSelectionButton.setBackgroundResource(R.drawable.btn_alt);
            userInfo.setDesign(false);
        }
        else
        {
            uiSelectionButton.setAlpha(1.0f);
            uiSelectionButton.setBackgroundResource(R.drawable.btn_main);
            userInfo.setDesign(true);

        }

    }

    public void opButtonClicked()
    {
        // this is to toggle between selection and non selection of button
        if (opSelectionButton.getAlpha() == 1.0f)
        {
            opSelectionButton.setAlpha(.5f);
            opSelectionButton.setBackgroundResource(R.drawable.btn_alt);
            userInfo.setOop(false);
        }
        else
        {
            opSelectionButton.setAlpha(1.0f);
            opSelectionButton.setBackgroundResource(R.drawable.btn_main);
            userInfo.setOop(true);

        }

    }

    public void init() {
        hobbiesContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseMethods.registerNewEmail(userInfo.getEmail(), password, userInfo.getUsername());
            }
        });
    }


    //----------------------------------------Firebase----------------------------------------

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance(); // get database instance
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) { //show when the data changes
                            //1st check: make sure the username is not already in use
                            if (firebaseMethods.checkIfUsernameExists(userInfo.getUsername(), dataSnapshot)) {
                                append = myRef.push().getKey().substring(3, 10);
                                Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                            }
                            userInfo.setUsername(userInfo.getUsername() + append);

                            //add new user to the database
                            //add new_user_account setting to the database
                            firebaseMethods.addNewUser(userInfo);
                            Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();

                            mAuth.signOut();

                            Intent intent = new Intent(RegisterHobby.this, Login.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
