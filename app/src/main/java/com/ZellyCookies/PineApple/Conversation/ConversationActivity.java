package com.ZellyCookies.PineApple.Conversation;

import android.content.Intent;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.ZellyCookies.PineApple.Conversation.Adapter.ConversationAdapter;
import com.ZellyCookies.PineApple.Conversation.Object.GroupObject;
import com.ZellyCookies.PineApple.Conversation.Object.MessageObject;
import com.ZellyCookies.PineApple.Login.Login;
import com.ZellyCookies.PineApple.Matched.ProfileCheckinMatched;
import com.ZellyCookies.PineApple.R;
import com.ZellyCookies.PineApple.Utils.GPS;
import com.ZellyCookies.PineApple.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class ConversationActivity extends AppCompatActivity {
//initialize variables and constants
    private static final String TAG = "Conversation_Activity";
    private static final String SEND_MESSAGE = "SEND_MESSAGE";
    private static final String UPDATED_MESSAGE = "UPDATED_MESSAGE";
    String action = "INIT";

    private GroupObject mGroupObject;
    private User userMatched;
    private String userId;
    private GPS gps;
    ArrayList<MessageObject> messageList;

    private RecyclerView mChat;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DocumentReference mGroupMessageDb;
    private CollectionReference mMessageId;

    private ImageView imagePerson;
    private TextView tvNamePerson;
    private EditText mMessage;
    private ImageButton btnSend, btnBack, btnInfo;

    private double latitude = 37.349642;
    private double longtitude = -121.938987;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_activity);

        gps = new GPS(this);
        setupFirebaseAuth();
        mFirestore = FirebaseFirestore.getInstance();
    //load user data
        mGroupObject = (GroupObject) getIntent().getSerializableExtra("groupObject");
        userMatched = mGroupObject.getUserMatch();
        userId = mAuth.getCurrentUser().getUid();
        mGroupMessageDb = mFirestore.collection("message").document(mGroupObject.getChatId());
    //setup display info
        btnInfo = findViewById(R.id.checkInfoUserMatched);
        btnBack = findViewById(R.id.back_matched_activity);
        imagePerson = findViewById(R.id.image_user_group);
        tvNamePerson = findViewById(R.id.name_user_group);
        btnSend = findViewById(R.id.send);
        mMessage = findViewById(R.id.messageInput);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int distance = gps.calculateDistance(latitude, longtitude, userMatched.getLatitude(), userMatched.getLongtitude());
                Intent intent = new Intent(ConversationActivity.this, ProfileCheckinMatched.class);
                intent.putExtra("classUser", userMatched);
                intent.putExtra("distance", distance);
                startActivity(intent);
            }
        });

        initializeMessage();
        getChatMessage();
        initTopBar();
    }

    List<DocumentSnapshot> messageIdList;

    private void getChatMessage() {
        mMessageId = mGroupMessageDb.collection("messages");

        mMessageId.orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    messageIdList = value.getDocuments();
                    Log.d(TAG, "getChatMessage: messageIdList " + messageIdList.size() + "-" + messageIdList.toString());
                    if (action.equals("INIT") || action.equals(SEND_MESSAGE)) {
                        for (DocumentSnapshot i : messageIdList) {
                            String messageText = "",
                                    sendBy = "",
                                    datetime = "";

                            if (i.getString("messageText") != null) {
                                messageText = i.getString("messageText");
                            }
                            if (i.getString("sendBy") != null) {
                                sendBy = i.getString("sendBy");
                            }
                            if (i.getString("sendAt") != null) {

                                datetime = i.getString("sendAt");
                            }

                            MessageObject mMessage = new MessageObject(i.getId(), sendBy, messageText, datetime);
                            messageList.add(mMessage);
                            mChatLayoutManager.scrollToPosition(messageList.size() - 1);
                            mChatAdapter.notifyDataSetChanged();
                            action = UPDATED_MESSAGE;
                        }
                        messageIdList.clear();
                    }
                }
            }
        });
    }

    private void initTopBar() {
        tvNamePerson.setText(userMatched.getUsername());
        String profileImageUrl = userMatched.getProfileImageUrl();

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

    private void initializeMessage() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ConversationAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }

    private void sendMessage() {
        String messageId = mGroupMessageDb.collection("messages").document().getId();
        final DocumentReference mMessageDb = mGroupMessageDb.collection("messages").document(messageId);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        final Map newMessageMap = new HashMap<>();
        newMessageMap.put("sendBy", userId);
        newMessageMap.put("sendAt", dateTime);
        newMessageMap.put("timestamp", FieldValue.serverTimestamp());

        if (!mMessage.getText().toString().isEmpty()) {
            newMessageMap.put("messageText", mMessage.getText().toString());
            updateDatabaseWithNewMessage(mMessageDb, newMessageMap);
            action = SEND_MESSAGE;
            messageList.clear();
            Log.d(TAG, " sendMessage over");
        }
    }

    private void updateDatabaseWithNewMessage(DocumentReference mMessageDb, Map newMessageMap) {
        mMessageDb.set(newMessageMap);
        mMessage.setText(null);
        //Need to make notification
    }

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
                    Intent intent = new Intent(ConversationActivity.this, Login.class);

                    //clear the activity stack， in case when sign out, the back button will bring the user back to the previous activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }
            }
        };
    }
}
