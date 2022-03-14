package com.ZellyCookies.PineApple.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ZellyCookies.PineApple.R;
import com.ZellyCookies.PineApple.Utils.TopNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BtnLikeActivity extends AppCompatActivity {
    private static final String TAG = "BtnLikeActivity";

    private Context mContext = BtnLikeActivity.this;
    private static final int ACTIVITY_NUM = 1;

    private ImageView like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btn_like);

        setupTopNavigationView();
        like = (ImageView) findViewById(R.id.like);

        Intent intent = getIntent();
        String profileUrl = intent.getStringExtra("url");

        switch (profileUrl) {
            case "defaultFemale":
                Glide.with(mContext).load(R.drawable.img_ava_female).into(like);
                break;
            case "defaultMale":
                Glide.with(mContext).load(R.drawable.img_ava_male).into(like);
                break;
            default:
                Glide.with(mContext).load(profileUrl).into(like);
                break;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent mainIntent = new Intent(BtnLikeActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        }).start();
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
}
