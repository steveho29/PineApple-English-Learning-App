package com.ZellyCookies.PineApple.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.ZellyCookies.PineApple.Main.MainActivity;
import com.ZellyCookies.PineApple.Matched.Matched_Activity;
import com.ZellyCookies.PineApple.Profile.Profile_Activity;
import com.ZellyCookies.PineApple.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class TopNavigationViewHelper {
    private static final String TAG = "TopNavigationViewHelper";

    public static void setupTopNavigationView(BottomNavigationViewEx tv) {
        Log.d(TAG, "setupTopNavigationView: setting up navigationview");

        tv.enableAnimation(false);
        tv.enableItemShiftingMode(false);
        tv.enableShiftingMode(false);
        tv.setTextVisibility(false);
        tv.setIconSize(30, 30);
    }

    public static void enableNavigation(final Context context, final BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_profile:
                        Intent intent2 = new Intent(context, Profile_Activity.class);
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_main:
                        Intent intent1 = new Intent(context, MainActivity.class);
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_matched:
                        Intent intent3 = new Intent(context, Matched_Activity.class);
                        context.startActivity(intent3);

                        break;
                }
                return false;
            }
        });
    }
}
