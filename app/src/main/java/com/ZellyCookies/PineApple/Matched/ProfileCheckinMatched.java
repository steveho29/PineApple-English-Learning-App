package com.ZellyCookies.PineApple.Matched;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ZellyCookies.PineApple.R;
import com.ZellyCookies.PineApple.Utils.CalculateAge;
import com.ZellyCookies.PineApple.Utils.User;

public class ProfileCheckinMatched extends AppCompatActivity {
    private static final String TAG = "ProfileCheckinMatched";

    private User user;
    private Context mContext = ProfileCheckinMatched.this;
    private Button sendSMSButton, sendEmailButton;
    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_checkin_matched);

    //initialize data
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        distance = intent.getIntExtra("distance", 1);

        Log.d(TAG, "onCreate: user name is" + user.getUsername());

        TextView toolbar = (TextView) findViewById(R.id.toolbartag);
        toolbar.setText("Matched");

        //sendSMSButton = (Button) findViewById(R.id.send_sms);
        sendEmailButton = (Button) findViewById(R.id.send_email);

    //setup display content
        TextView profile_name = (TextView) findViewById(R.id.profile_name);
        TextView profile_distance = (TextView) findViewById(R.id.profile_distance);
        TextView profile_numbers = (TextView) findViewById(R.id.profile_number);
        TextView profile_email = (TextView) findViewById(R.id.profile_email);
        ImageView imageView = (ImageView) findViewById(R.id.image_matched);
        TextView profile_bio = (TextView) findViewById(R.id.bio_match);
        TextView profile_interest = (TextView) findViewById(R.id.interests_match);
        TextView profile_dob = findViewById(R.id.profile_dob);

    //load user data to display
        CalculateAge cal = new CalculateAge(user.getDateOfBirth());
        int age = cal.getAge();

        profile_name.setText(user.getUsername() + ", " + age);
        profile_email.setText(user.getEmail());

        profile_dob.setText(user.getDateOfBirth());

        String append = (distance == 1) ? "mile away" : "miles away";
        profile_distance.setText(distance + " " + append);


        if (user.getDescription().length() != 0)
        {
            profile_bio.setText(user.getDescription());
        }

        if (user.getPhone_number().length() != 0)
        {
            profile_numbers.setText(user.getPhone_number());
        }
        else
        {
            //sendSMSButton.setEnabled(false);
        }

        //append interests
        StringBuilder interest = new StringBuilder();
        if (user.isSE()) {
            interest.append("SE   ");
        }
        if (user.isOop()) {
            interest.append("OOP   ");
        }
        if (user.isDesign()) {
            interest.append("UI Design   ");
        }
        if (user.isDatabase()) {
            interest.append("Database   ");
        }

        profile_interest.setText(interest.toString());

        String profileImageUrl = user.getProfileImageUrl();
        switch (profileImageUrl) {
            case "defaultFemale":
                Glide.with(mContext).load(R.drawable.img_ava_female).into(imageView);
                break;
            case "defaultMale":
                Glide.with(mContext).load(R.drawable.img_ava_male).into(imageView);
                break;
            default:
                Glide.with(mContext).load(profileImageUrl).into(imageView);
                break;
        }

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /*
        sendSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendSMS(user.getPhone_number().toString(),user.getUsername().toString());
            }
        });
        */

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendEmail(user.getEmail().toString(),user.getUsername().toString());
            }
        });

    }

    // This method will be called when send sms button in matched profile will be clicked. This open the default sms app.
    public void sendSMS(String phoneNumber, String userName)
    {
        Intent smsAppOpener = new Intent(Intent.ACTION_VIEW);
        smsAppOpener.setData(Uri.parse("sms:" +  phoneNumber));
        smsAppOpener.putExtra("sms_body", "Hi "+userName+", \n"+ "Love to have a coffee with you!!!!");
        startActivity(smsAppOpener);
    }

    // This method will be called when send email button in matched profile will be clicked. This open the default email app.
    private void sendEmail(String email, String userName)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
        intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding our Pink Moon Match!!!");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi "+userName+", \n"+ "Love to have a coffee with you!!!!");
        startActivity(Intent.createChooser(intent, ""));
    }
}