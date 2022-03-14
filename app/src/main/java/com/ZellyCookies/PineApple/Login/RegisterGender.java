package com.ZellyCookies.PineApple.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ZellyCookies.PineApple.R;
import com.ZellyCookies.PineApple.Utils.User;

public class RegisterGender extends AppCompatActivity {

    String password;
    User user;

    private Button genderContinueButton;
    private Button maleSelectionButton;
    private Button femaleSelectionButton;   
    boolean male = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gender);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        password = intent.getStringExtra("password");

        maleSelectionButton = (Button) findViewById(R.id.maleSelectionButton);
        femaleSelectionButton = (Button) findViewById(R.id.femaleSelectionButton);
        genderContinueButton = (Button) findViewById(R.id.genderContinueButton);

        //By default male has to be selected
        femaleSelectionButton.setAlpha(.5f);

        maleSelectionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                maleButtonSelected();
            }
        });

        femaleSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                femaleButtonSelected();
            }
        });

        genderContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openPreferenceEntryPage();
            }
        });

    }

    public void maleButtonSelected()
    {
        male = true;
        maleSelectionButton.setBackgroundResource(R.drawable.btn_main_default);
        maleSelectionButton.setAlpha(1);
        femaleSelectionButton.setBackgroundResource(R.drawable.btn_alt);
        femaleSelectionButton.setAlpha(.5f);
    }

    public void femaleButtonSelected()
    {
        male = false;
        maleSelectionButton.setBackgroundResource(R.drawable.btn_alt);
        maleSelectionButton.setAlpha(.5f);
        femaleSelectionButton.setBackgroundResource(R.drawable.btn_main_default);
        femaleSelectionButton.setAlpha(1);
    }

    public void openPreferenceEntryPage()
    {
        String ownSex = male ? "male" : "female";
        user.setSex(ownSex);
        //set default prefer sex
        user.setPreferSex(ownSex);
        //set default photo
        String defaultPhoto = male ? "defaultMale" : "defaultFemale";
        user.setProfileImageUrl(defaultPhoto);

        Intent intent = new Intent(this, RegisterAge.class);
        intent.putExtra("password", password);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }
}
