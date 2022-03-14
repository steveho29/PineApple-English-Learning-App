package com.ZellyCookies.PineApple.Matched;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ZellyCookies.PineApple.Conversation.ConversationActivity;
import com.ZellyCookies.PineApple.Conversation.Object.GroupObject;
import com.ZellyCookies.PineApple.Login.Login;
import com.ZellyCookies.PineApple.R;
import com.ZellyCookies.PineApple.Utils.FirebaseMethods;
import com.ZellyCookies.PineApple.Utils.GPS;
import com.ZellyCookies.PineApple.Utils.TopNavigationViewHelper;
import com.ZellyCookies.PineApple.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class Matched_Activity extends AppCompatActivity {

    private static final String TAG = "Matched_Activity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = Matched_Activity.this;
    private String userId, userSex, lookforSex;
    private double latitude = 37.349642;
    private double longtitude = -121.938987;
    private EditText search;
    private ArrayList<String> idGroupList;

    ProfileAdapter mAdapter;

    List<GroupObject> matchList = new ArrayList<>();
    List<GroupObject> copyList = new ArrayList<>();
    GPS gps;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference dbRef;
    private FirebaseMethods firebaseMethods;
    private FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched);

        firebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();
        setupTopNavigationView();
        searchFunc();

        userId = mAuth.getInstance().getCurrentUser().getUid();
        gps = new GPS(this);
        dbRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        checkUserSex();

        mAdapter = new ProfileAdapter(Matched_Activity.this, R.layout.matched_item, matchList);
        ListView listView = (ListView) findViewById(R.id.matchList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: The list has been clicked");
                checkClickedItem(position);
            }
        });
    }

    private void searchFunc() {
        search = (EditText) findViewById(R.id.searchBar);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText();
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText();
            }
        });
    }

    private void searchText() {
        String text = search.getText().toString().toLowerCase(Locale.getDefault());
        if (text.length() != 0) {
            if (matchList.size() != 0) {
                matchList.clear();
                for (GroupObject groupObject : copyList) {
                    if (groupObject.getUserMatch().getUsername().toLowerCase(Locale.getDefault()).contains(text)) {
                        matchList.add(groupObject);
                    }
                }
            }
        } else {
            matchList.clear();
            matchList.addAll(copyList);
        }

        mAdapter.notifyDataSetChanged();
    }

    public void checkUserSex() {
        DatabaseReference maleDb = dbRef.child("male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equals(userId)) {
                    Log.d(TAG, "onChildAdded: the sex is male");
                    userSex = "male";
                    //update location
                    latitude = dataSnapshot.getValue(User.class).getLatitude();
                    longtitude = dataSnapshot.getValue(User.class).getLongtitude();

                    lookforSex = dataSnapshot.getValue(User.class).getPreferSex();
                    findMatchUID();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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

        DatabaseReference femaleDb = dbRef.child("female");
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equals(userId)) {
                    Log.d(TAG, "onChildAdded: the sex is female");
                    userSex = "female";

                    //update location
                    latitude = dataSnapshot.getValue(User.class).getLatitude();
                    longtitude = dataSnapshot.getValue(User.class).getLongtitude();

                    lookforSex = dataSnapshot.getValue(User.class).getPreferSex();
                    findMatchUID();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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

    private void findMatchUID() {
        Log.d(TAG, "findMatchUID: start to find match");
        idGroupList = new ArrayList<>();
        final DatabaseReference groupRef = dbRef.child(userSex).child(userId).child("group");

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    final String uid = ds.getKey();
                    final String idGroup = (String) ds.getValue();

                    Log.d(TAG, "onDataChange: uid :" + uid + " - idGroup :" + idGroup);

                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User user = firebaseMethods.getUser(dataSnapshot, lookforSex, uid);
                            if (!checkDup(user)) {
                                Log.d(TAG, "Day la idGroup :");
                                GroupObject groupObject = new GroupObject(idGroup, user);
                                matchList.add(groupObject);
                                copyList.add(groupObject);
                                mAdapter.notifyDataSetChanged();
                                Log.d(TAG, "onDataChange: match list size is " + matchList.size());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: test cancel");
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private boolean checkDup(User user) {
        if (matchList.size() != 0) {
            for (GroupObject groupObject : matchList) {
                if (groupObject.getUserMatch().getUsername() == user.getUsername()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkClickedItem(int position) {

        GroupObject groupObject = matchList.get(position);
        User user = groupObject.getUserMatch();
        //calculate distance
        int distance = gps.calculateDistance(latitude, longtitude, user.getLatitude(), user.getLongtitude());

        Intent intent = new Intent(this, ConversationActivity.class);
//        intent.putExtra("classUser", user);
//        intent.putExtra("distance", distance);
        intent.putExtra("groupObject", groupObject);
        startActivity(intent);
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


    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

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
                    Intent intent = new Intent(Matched_Activity.this, Login.class);

                    //clear the activity stack， in case when sign out, the back button will bring the user back to the previous activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}