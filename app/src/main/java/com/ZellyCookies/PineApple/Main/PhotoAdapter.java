package com.ZellyCookies.PineApple.Main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.ZellyCookies.PineApple.R;

import java.util.List;



public class PhotoAdapter extends ArrayAdapter<Cards> {
    Context mContext;


    public PhotoAdapter(@NonNull Context context, int resource, @NonNull List<Cards> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Cards card_item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        ImageButton btnInfo = (ImageButton) convertView.findViewById(R.id.checkInfoBeforeMatched);

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileCheckinMain.class);
                intent.putExtra("name", card_item.getName() +  ", " + card_item.getAge());
                intent.putExtra("photo", card_item.getProfileImageUrl());
                intent.putExtra("dob", card_item.getDob());
                intent.putExtra("bio", card_item.getBio());
                intent.putExtra("interest", card_item.getInterest());
                intent.putExtra("distance", card_item.getDistance());
                mContext.startActivity(intent);
            }
        });

        name.setText(card_item.getName() +  ", " + card_item.getAge());

        switch (card_item.getProfileImageUrl()) {
            case "defaultFemale":
                Glide.with(getContext()).load(R.drawable.img_ava_female).into(image);
                break;
            case "defaultMale":
                Glide.with(getContext()).load(R.drawable.img_ava_male).into(image);
                break;
            default:
                Glide.with(getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }

        return convertView;
    }
}
