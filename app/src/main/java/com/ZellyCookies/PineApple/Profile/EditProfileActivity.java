package com.ZellyCookies.PineApple.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ZellyCookies.PineApple.Login.Login;
import com.ZellyCookies.PineApple.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private Context mContext = EditProfileActivity.this;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mPhotoDB;

    private ImageView mProfileImage;
    private String userId, profileImageUri;
    private Uri resultUri;
    private String userSex;
    private EditText phoneNumber,aboutMe;
    private CheckBox SECheckBox,databaseCheckBox,designCheckBox,oopCheckBox;
    private boolean isSEClicked = false;
    private boolean isDatabaseClicked = false;
    private boolean isOopClicked = false;
    private boolean isDesignClicked = false;

    private RadioGroup userSexSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        TextView toolbar = (TextView) findViewById(R.id.toolbartag);
        toolbar.setText("Profile");

        setupFirebaseAuth();

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mProfileImage = (ImageView)findViewById(R.id.profileImage);

        phoneNumber = (EditText)findViewById(R.id.edit_phone);

        aboutMe = (EditText)findViewById(R.id.edit_aboutme);

        userSexSelection = (RadioGroup) findViewById(R.id.radioGroupUserSex);

        SECheckBox = (CheckBox)findViewById(R.id.checkbox_se);
        databaseCheckBox = (CheckBox)findViewById(R.id.checkbox_database);
        designCheckBox = (CheckBox)findViewById(R.id.checkbox_design);
        oopCheckBox = (CheckBox)findViewById(R.id.checkbox_oop);

        userId = mAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "onCreate: user id is" + userId);

        checkUserSex();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void getUserData(){
        mPhotoDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("profileImageUrl") != null)
                    {
                        profileImageUri = map.get("profileImageUrl").toString();
                        Log.d(TAG, "onDataChange: the profileImageUri is" + profileImageUri);

                        switch (profileImageUri) {
                            case "defaultFemale":
                                Glide.with(getApplication()).load(R.drawable.img_ava_female).into(mProfileImage);
                                break;
                            case "defaultMale":
                                Glide.with(getApplication()).load(R.drawable.img_ava_male).into(mProfileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUri).into(mProfileImage);
                                break;
                        }
                    }
                    if(map.get("phone_number")!=null)
                    {
                        phoneNumber.setText(map.get("phone_number").toString());

                    }
                    if(map.get("description")!=null)
                    {
                        aboutMe.setText(map.get("description").toString());

                    }
                    if(map.get("sex") !=null)
                    {
                        if(map.get("sex").toString().equalsIgnoreCase("male"))
                        {
                            userSexSelection.check(R.id.maleSelction);
                        }
                        else
                        {
                            userSexSelection.check(R.id.femaleSelection);
                        }

                    }
                    if(Boolean.valueOf(map.get("se").toString()) == true)
                    {
                        isSEClicked = true;
                        SECheckBox.setChecked(true);
                    }
                    else
                    {
                        isSEClicked = false;
                        SECheckBox.setChecked(false);
                    }

                    if(Boolean.valueOf(map.get("database").toString()) == true)
                    {
                        isDatabaseClicked = true;
                        databaseCheckBox.setChecked(true);

                    }
                    else
                    {
                        isDatabaseClicked = false;
                        databaseCheckBox.setChecked(false);
                    }
                    if(Boolean.valueOf(map.get("oop").toString()) == true)
                    {
                        isOopClicked = true;
                        oopCheckBox.setChecked(true);

                    }
                    else
                    {
                        isOopClicked = false;
                        oopCheckBox.setChecked(false);
                    }
                    if(Boolean.valueOf(map.get("design").toString()) == true)
                    {
                        isDesignClicked = true;
                        designCheckBox.setChecked(true);

                    }
                    else
                    {
                        isDesignClicked = false;
                        designCheckBox.setChecked(false);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void saveUserPhoto() {
        if (resultUri != null) {
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            final UploadTask uploadTask = filepath.putBytes(data);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    finish();
//                }
//            });
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
//                        FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUri.toString());
//                        mMessagesDatabaseReference.push().setValue(friendlyMessage);
                        Map userInfo = new HashMap<>();
                        userInfo.put("profileImageUrl", downloadUri.toString());
                        mPhotoDB.updateChildren(userInfo);
                    }
                }
            });

//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                    Map userInfo = new HashMap<>();
//                    userInfo.put("profileImageUrl", downloadUrl.toString());
//                    mPhotoDB.updateChildren(userInfo);
//
//                    return;
//                }
//            });
        }
    }

    private void saveUserData()
    {
        Map userInfo = new HashMap<>();
        userInfo.put("phone_number", phoneNumber.getText().toString());
        userInfo.put("description", aboutMe.getText().toString());
        userInfo.put("se",isSEClicked);
        userInfo.put("database",isDatabaseClicked);
        userInfo.put("design",isDesignClicked);
        userInfo.put("oop",isOopClicked);

//Updation of sex is not allowed once profile is created.
//        if (((RadioButton)findViewById(userSexSelection.getCheckedRadioButtonId())).getText().toString().equalsIgnoreCase("male"))
//        {
//            userInfo.put("sex","male");
//        }
//        else
//        {
//            userInfo.put("sex","female");
//        }
        mPhotoDB.updateChildren(userInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);

        }
    }


    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equals(userId)) {
                    Log.d(TAG, "onChildAdded: the sex is male" );
                    userSex = "male";
                    mPhotoDB = FirebaseDatabase.getInstance().getReference().child(userSex).child(userId);
                    getUserData();
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

        DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference().child("female");
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equals(user.getUid())) {
                    Log.d(TAG, "onChildAdded: the sex is female" );
                    userSex = "female";
                    mPhotoDB = FirebaseDatabase.getInstance().getReference().child(userSex).child(userId);
                    getUserData();

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

    public void saveAndBack(View view) {
        saveUserPhoto();
        saveUserData();
        Intent intent = new Intent(mContext, Profile_Activity.class);
        startActivity(intent);
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
                    Intent intent = new Intent(EditProfileActivity.this, Login.class);

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

    public void onHobbyCheckBoxClicked(View view)
    {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkbox_se:
                if (checked)
                {
                    isSEClicked = true;
                }
                else
                {
                    isSEClicked = false;
                }
                break;
            case R.id.checkbox_oop:
                if (checked)
                {
                    isOopClicked = true;
                }
                else
                {
                    isOopClicked = false;
                }
                break;
            case R.id.checkbox_design:
                if (checked)
                {
                    isDesignClicked = true;
                }
                else
                {
                    isDesignClicked = false;
                }
                break;
            case R.id.checkbox_database:
                if (checked)
                {
                    isDatabaseClicked = true;
                }
                else
                {
                    isDatabaseClicked = false;
                }
                break;
        }
    }
}
