package com.example.profiler.activities;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profiler.R;
import com.example.profiler.StaticClass;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.activities.specific_data.MyProfileRecordsActivity;
import com.example.profiler.activities.specific_data.ProfileRecordsActivity;
import com.example.profiler.adapters.ClickableViewPager;
import com.example.profiler.adapters.CustomPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class FullScreenActivity extends Activity {

    ConstraintLayout wholeCL;
    LinearLayout textViewsLL;

    ClickableViewPager viewPager;
    CustomPagerAdapter adapter;
    ArrayList<Bitmap> pagerList = new ArrayList<>();

    TextView titleTV, descriptionTV;
    ImageView fullScreenIV;

    String title, description, uriString, viewType, from;
    int profileId;
    boolean textViewsShown=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        getExtras();
        titleTV = findViewById(R.id.titleFSTV);
        titleTV.setText(title);
        descriptionTV = findViewById(R.id.descriptionFSTV);
        if(description!=null && !description.toLowerCase().equals(StaticClass.DESCRIPTION)){
            descriptionTV.setText(description);
        }else{
            descriptionTV.setVisibility(View.GONE);
        }
        fullScreenIV = findViewById(R.id.fullScreenIV);
        viewPager = findViewById(R.id.fullScreenViewPager);

        if(viewType.equals(StaticClass.IMAGE_VIEW)){
            setImageView();
        }else{
            setViewPager();
        }
        wholeCL = findViewById(R.id.wholeCL);
        wholeCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTextViews();
            }
        });
        textViewsLL = findViewById(R.id.textViewsLL);
    }

    public void getExtras(){
        Intent intent = getIntent();
        title = intent.getStringExtra(StaticClass.TITLE);
        description = intent.getStringExtra(StaticClass.DESCRIPTION);
        uriString = intent.getStringExtra(StaticClass.URI_STRING);
        viewType = intent.getStringExtra(StaticClass.VIEW_TYPE);
        from = intent.getStringExtra(StaticClass.FROM);
        profileId = intent.getIntExtra(StaticClass.PROFILE_ID, -1);
    }

    public void setViewPager(){
        viewPager.setVisibility(View.VISIBLE);
        fullScreenIV.setVisibility(View.GONE);
        if(uriString != null){
            String[] imageStringUris = uriString.split(",");
            for(String uriStr: imageStringUris){
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(
                            getApplicationContext().getContentResolver(),
                            Uri.parse(uriStr));
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "IO Exception",
                            Toast.LENGTH_LONG).show();
                }
                pagerList.add(imageBitmap);
            }
            adapter = new CustomPagerAdapter(getApplicationContext(),
                    pagerList);
            viewPager.setAdapter(adapter);
        }
        viewPager.setOnViewPagerClickListener(new ClickableViewPager.OnClickListener() {
            @Override
            public void onViewPagerClick(ViewPager viewPager) {
                toggleTextViews();
            }
        });
    }


    public void setImageView() {
        viewPager.setVisibility(View.GONE);
        fullScreenIV.setVisibility(View.VISIBLE);
        if (uriString != null) {
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), Uri.parse(uriString));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),
                        "IO Exception",
                        Toast.LENGTH_LONG).show();
            }
            fullScreenIV.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        if(from.equals(StaticClass.My_PROFILE_RECORDS)){
            intent = new Intent(getApplicationContext(), MyProfileRecordsActivity.class);
        }else if(from.equals(StaticClass.PROFILE_RECORDS)){
            intent = new Intent(getApplicationContext(), ProfileRecordsActivity.class)
                    .putExtra(StaticClass.PROFILE_ID, profileId);
        }else if (from.equals(StaticClass.ALL_RECORDS)){
            intent = new Intent(getApplicationContext(), AllDataActivity.class)
                    .putExtra(StaticClass.TO, StaticClass.ALL_RECORDS);
        }
        startActivity(intent);
    }

    public void toggleTextViews(){
        textViewsLL.animate()
                .alpha(textViewsShown ? 0f : 1f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        textViewsLL.setVisibility(textViewsShown ? View.VISIBLE : View.GONE);
                    }
                });
        textViewsShown = !textViewsShown;
    }
}
